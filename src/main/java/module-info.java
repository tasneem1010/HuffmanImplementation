module com.example.huffman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.huffman to javafx.fxml;
    exports com.example.huffman;
}