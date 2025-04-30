package com.example.huffman;

// tree node class
public class Node {
    Node left; //left child
    Node right; //right child
    int frequency; //byte frequency in file
    byte data; // byte value
    String huffmanCode; // huffman code for the byte

    int codeSize; //huffman code size

    public Node(byte data,int frequency) {
        this.data = data;
        this.frequency = frequency;
    }

    public Node() {
        data = -1;
        frequency = 0;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getData() {
        return  Integer.toBinaryString(data & 0xFF);
    }

    public void setData(byte data) {
        this.data = data;
    }

    public String getHuffmanCode() {
        return huffmanCode;
    }

    public void setHuffmanCode(String huffmanCode) {
        this.huffmanCode = huffmanCode;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }
}
