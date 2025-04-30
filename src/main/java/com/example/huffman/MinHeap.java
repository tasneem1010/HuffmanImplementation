package com.example.huffman;

//priority queue class
public class MinHeap {

    Node[] array; //queue
    int size; //size of array
    int count; // count of nods in array

    public MinHeap() {
        size = 1;
        array = new Node[size];
        count = 0;
    }

    //insert to node to heap
    public void insert(Node node) {

        array[count] = node;
        int i = count;
        while (i > 0 && array[i].frequency < array[(i - 1) / 2].frequency) { // keep ordered based on frequency
            Node temp = array[(i - 1) / 2];
            array[(i - 1) / 2] = array[i];
            array[i] = temp;
            i = (i - 1) / 2;
        }
        count++;
        if (count == size) {
            resize();
        }
    }

    //double array size when full
    private void resize() {
        size = size * 2;
        Node[] temp = new Node[size];
        for (int i = 0; i < size / 2; i++) {
            temp[i] = array[i];
        }
        array = temp;
    }

    // dequeue smallest node (heap root)
    public Node removeMin() {
        Node min = array[0];
        array[0] = array[count - 1];
        count--;
        heapify(array, count, 0);
        return min;
    }
    // heapify queue from input index to the bottom
    private void heapify(Node[] array, int size, int root) {
        int smallest = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < size && array[left].frequency < array[smallest].frequency) {
            smallest = left;
        }
        if (right < size && array[right].frequency < array[smallest].frequency) {
            smallest = right;
        }
        if (smallest != root) {
            Node temp = array[root];
            array[root] = array[smallest];
            array[smallest] = temp;
            heapify(array, size, smallest);
        }
    }
}
