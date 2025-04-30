package com.example.huffman;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;

//huffman class containing compression and decompression utilities
public class Huffman{

    File inFile; //input file
    File outFile; //output file
    Node root; //huffman tree root
    Boolean isDone; //the operation was successful
    public byte[] buffer = new byte[8]; // read/ write buffer
    public String[] codes = new String[256]; //huffman codes for each byte
    String header; // file header


    //create huffman instance
    public Huffman(File file) {
        inFile = file;
        isDone = false;
    }


    //traverse tree -preorder- to set huffman codes accordingly
    public void assignCodes(Node node, String code) {
        if (node == null) return;

        if (node.left == null && node.right == null) { //set huffman code for leaf nodes
            node.huffmanCode = code;
            node.codeSize = code.length();
            codes[node.data & 0xff] = code;
        }

        if (node.left != null) {    // append zero when branching left
            assignCodes(node.left, code + "0");
        }

        if (node.right != null) {   //append one when branching right
            assignCodes(node.right, code + "1");
        }
    }

    //turn byte to binary string keeping the leading zeros
    public String byteToString(byte b) {
        String s = Integer.toBinaryString(b & 0xFF);
        String zeros = "";
        for (int i = s.length(); i < 8; i++) {
            zeros += "0";

        }
        return zeros + s;
    }
    //turn int to binary string keeping the leading zeros
    public String intToString(int num) {
        String s = Integer.toBinaryString(num);
        String zeros = "";
        for (int i = s.length(); i < 32; i++) {
            zeros += "0";

        }
        return zeros + s;
    }

    //read number of bits from input stream to String (for reading tree representation)
    public String readBytesToString(InputStream in, int bitsToRead) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int totalBitsRead = 0;


        while (totalBitsRead < bitsToRead) { //keep reading till wanted bits are reached
            int currentByte = in.read();

            if (currentByte == -1) {
                throw new IOException("Unexpected end of stream while reading tree representation");
            }

            for (int bitPosition = 7; bitPosition >= 0; bitPosition--) { //iterate through current byte
                int bit = (currentByte >> bitPosition) & 1; // separate each bit to append to string
                stringBuilder.append(bit);
                totalBitsRead++;

                if (totalBitsRead == bitsToRead) { //stop reading when wanted bits are read
                    return stringBuilder.toString();
                }
            }
        }

        return stringBuilder.toString();
    }
    //array of bytes to string
    public String bytesToString(byte[] bytes){
        StringBuilder s = new StringBuilder();
        for (byte b :
                bytes) {
            s.append(byteToString(b));

        }
        return s.toString();
    }

    //add all tree leaves to Observable list to use as table view
    public void getTreeLeaves(ObservableList list,Node root){
        if (root== null) return;

        if (root.left==null && root.right== null){
            list.add(root);
        }

        getTreeLeaves(list,root.left);
        getTreeLeaves(list,root.right);
    }
}
