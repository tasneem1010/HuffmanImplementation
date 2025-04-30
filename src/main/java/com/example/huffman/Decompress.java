package com.example.huffman;

import java.io.*;

//class containing decompression tools

public class Decompress extends Huffman {


    public Decompress(File file) {
        super(file);
    }

    //read file header to decompress
    public void readToDecompress() {
        try (DataInputStream stream = new DataInputStream(new FileInputStream(inFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String ext = "";
            byte extSize = stream.readByte();
            stringBuilder.append(byteToString(extSize));
            if (extSize <= 0) return;

            while (extSize > 0) {
                ext += String.valueOf(Character.toChars(stream.read()));
                stringBuilder.append(bytesToString(ext.getBytes()));
                extSize--;
            }
            int treeSize = stream.readInt();
            stringBuilder.append(intToString(treeSize));

            System.out.println("tree size (number of bits): " + treeSize);
            String treeRep = readBytesToString(stream, treeSize);
            stringBuilder.append(treeRep);
            System.out.println(treeRep);

            root = reconstructTree(treeRep.toCharArray()); //the reconstructed tree read from file
            assignCodes(root, "");

            //decode compressed data and write it to new file

            outFile = new File(inFile.getAbsolutePath().split("\\.")[0] + "." + ext);
            if (!outFile.exists()) outFile.createNewFile();
            int x = treeRep.length();
            int tRB = x % 8 == 0 ? x / 8 : x / 8 + 1;
            int headerSizeInBytes = 1 + ext.toCharArray().length + 4 + tRB;
            header = stringBuilder.toString();
            decompress(stream, headerSizeInBytes * 8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //read compressed data and decompress it

    public void decompress(DataInputStream input, int headerSize) {
        try (DataOutputStream output = new DataOutputStream(new FileOutputStream(outFile))) {
            int padding = input.read();
            System.out.println("Padding: " + padding);

            Node currentNode = root;
            int bitCount = 0;

            int fileSize = (int) inFile.length() * 8;
            int dataSize = fileSize - (headerSize + padding + 8); //number of bits to read

            int count;
            byte[] writeBuffer = new byte[8];
            int writeBufferIndex = 0;
            while ((count = input.read(buffer)) != -1) {
                for (int i = 0; i < count; i++) {
                    byte b = buffer[i];
                    for (int j = 7; j >= 0; j--) { // process each bit in the current byte (from most to least significant)
                        if (bitCount >= dataSize) {
                            break;
                        }
                        // extract the current bit by shifting it to the lsb position and preforming 'and' with 1 to turn all other bits to zero
                        int bit = (b >> j) & 0b00000001;

                        //based on the tree forming method 0 indicates left child while 1 indicates right

                        currentNode = (bit == 0) ? currentNode.left : currentNode.right;

                        if (currentNode.left == null && currentNode.right == null) { //write the data when leaf is reached
                            writeBuffer[writeBufferIndex++] = (byte) (currentNode.data & 0xff);
                            if(writeBufferIndex == 8)
                            {
                                output.write(writeBuffer, 0, writeBuffer.length);
                                writeBufferIndex = 0;
                            }
                            currentNode = root;
                        }
                        bitCount++;
                    }
                }
            }

            if(writeBufferIndex > 0){
                output.write(writeBuffer, 0, writeBufferIndex);
            }
            isDone = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }


    int rebuildIndex = 0;

    //reconstruct huffman tree from read binary string
    public Node reconstructTree(char[] chars) {

        if (rebuildIndex >= chars.length) return root;

        char currentChar = chars[rebuildIndex];
        rebuildIndex++;


        if (currentChar == '0') {

            Node root = new Node();
            root.left = reconstructTree(chars);
            root.right = reconstructTree(chars);
            return root;
        } else if (currentChar == '1') {
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                data.append(chars[rebuildIndex]);
                rebuildIndex++;
            }
            return new Node((byte) Integer.parseInt(data.toString(), 2), 0);
        }
        return null;
    }
}
