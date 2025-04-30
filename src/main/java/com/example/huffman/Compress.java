package com.example.huffman;

import java.io.*;

// huffman subclass containing compression tools
public class Compress extends Huffman {

    public int[] frequency = new int[256]; //frequency array


    public Compress(File file) {
        super(file);
    }

    //compress file
    public void compress() throws Exception {
        try {
            readFile();
            formTree();
            assignCodes(root, "");
            header();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    //read file and find frequency for each byte
    public void readFile() {
        try (InputStream input = new FileInputStream(inFile)) {
            int count;
            while ((count = input.read(buffer)) != -1) {// reads the size of the buffer and stores it in buffer

                for (int i = 0; i < count; i++) {
                    //the byte is stored as an int so if the byte is 10000000 its -2^7 = -128
                    byte b = buffer[i];
                    int unsignedInt = b & 0xff;
                    frequency[unsignedInt]++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //write header to compressed file using buffer array
    private void header() {
        try {
            StringBuilder header = new StringBuilder();
            String[] file = inFile.getAbsolutePath().split("\\.");
            String ogFileName = file[0];
            String extension = file[file.length - 1];
            outFile = new File(ogFileName + ".huff");
            if (!outFile.exists()) outFile.createNewFile();
            DataOutputStream output = new DataOutputStream(new FileOutputStream(outFile));

            byte extSize = (byte) extension.getBytes().length;  //maximum filename size = 255 byte
            output.write(extSize);//1 byte extension size
            header.append(byteToString(extSize));
            System.out.println("extension size: " + extSize);
            output.write(extension.getBytes()); //extension as ascii
            header.append(bytesToString(extension.getBytes()));
            StringBuilder treeRep = new StringBuilder();
            treeAsString(root, treeRep);
            int treeSize = treeRep.length();
            output.writeInt(treeSize); // 4 byte tree size
            header.append(intToString(treeSize));
            System.out.println("tree string size = " + treeSize);

            char[] chars = String.valueOf(treeRep).toCharArray();
            // write tree as bytes to file
            //turn string to bytes
            byte currentByte = 0;
            int bitCount = 0;
            int i = 0;
            for (int j = 0; j < chars.length; j++) {
                char bit = chars[j];
                currentByte <<= 1;  //shift left to add next bit
                if (bit == '1') {
                    currentByte = (byte) (currentByte | 0b00000001);  // set lsb to 1
                }
                bitCount++;
                if (bitCount == 8) {
                    if (i == 8) {
                        i = 0;
                        output.write(buffer);
                        header.append(bytesToString(buffer));
                    }
                    buffer[i] = currentByte;// add byte to buffer
                    i++;
                    currentByte = 0;
                    bitCount = 0;
                }
            }
            if (bitCount > 0) {
                currentByte <<= (8 - bitCount); //shift left-over bits to add padding to the correct place
                buffer[i] = currentByte;// add byte to buffer
                i++;
            }
            if (i > 0) { // write the unwritten bytes in buffer
                output.write(buffer, 0, i);
            }
            this.header = header.toString();
            writeCompressedData(output); //write compressed data to file
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //turn the tree to its equivalent binary string representation
    public void treeAsString(Node node, StringBuilder treeRep) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            treeRep.append("1").append(byteToString(node.data));
        } else {
            treeRep.append("0");
        }

        treeAsString(node.left, treeRep);
        treeAsString(node.right, treeRep);
    }

    //build huffman tree
    public void formTree() {
        MinHeap heap = new MinHeap();
        for (int i = 0; i < frequency.length; i++) {
            if (frequency[i] == 0) continue;
            heap.insert(new Node((byte) i, frequency[i]));
        }

        while (heap.count > 1) {
            Node z = new Node();
            Node left = heap.removeMin();
            Node right = heap.removeMin();
            z.left = left;
            z.right = right;
            z.frequency = left.frequency + right.frequency;
            heap.insert(z);
        }
        this.root = heap.array[0];
    }

    //write compressed data to file using 8 byte buffer
    public void writeCompressedData(DataOutputStream output) {
        try (DataInputStream input = new DataInputStream(new FileInputStream(inFile))) {
            // reads the size of the buffer and stores it in buffer
            int count;

            int fileBits = 0;
            for (int i = 0; i < codes.length; i++) {
                if (codes[i] != null) {
                    fileBits += frequency[i] * codes[i].length();
                }
            }

            // calculate padding
            byte padding = (byte) ((8 - (fileBits % 8)) % 8);
            output.write(padding);

            byte[] writeBuffer = new byte[8];
            int index = 0;
            byte bitBuffer = 0;
            int bitCount = 0;
            while ((count = input.read(buffer)) != -1) {
                //string to bytes
                for (int i = 0; i < count; i++) {
                    String currentCode = codes[buffer[i] & 0xff];
                    char[] chars = currentCode.toCharArray();

                    for (int j = 0; j < chars.length; j++) {

                        char bit = chars[j];
                        bitBuffer <<= 1;

                        if (bit == '1') {
                            bitBuffer |= 1; // lsb = 1
                        }
                        bitCount++;
                        if (bitCount == 8) {
                            if (index == 8) {
                                index = 0;
                                output.write(writeBuffer);
                            }
                            writeBuffer[index] = bitBuffer;// add byte to buffer
                            index++;
                            bitBuffer = 0;
                            bitCount = 0;
                        }
                    }
                }
            }
            if (bitCount > 0) {
                bitBuffer <<= (8 - bitCount);
                writeBuffer[index] = bitBuffer;
                index++;
            }
            if (index > 0) {
                output.write(writeBuffer, 0, index);
            }
            isDone = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
