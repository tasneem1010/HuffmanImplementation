package com.example.huffman;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

//main screen class
public class MainScreen {

    boolean isCompress = true; //is the mode compress or decompress
    String headerString; //header of compressed file
    Compress c; //compress instance variable
    Decompress d; //decompress instance variable

    //set new scene to main stage to show main screen
    public void showScreen(Stage stage) {

        Label title = new Label("Huffman Compressor");
        Button compress = new Button("Compress");
        Button decompress = new Button("Decompress");

        HBox operations = new HBox(compress, decompress);
        operations.getStyleClass().add("pane");
        Button browse = new Button("Browse Files");
        Button start = new Button("Start");
        //stats button to show the file size before and after compression and compression percentage
        Button stats = new Button("Statistics");
        //show huffman code (huffman code: byte - frequency - size -huffman)
        Button huffman = new Button("Huffman");
        //show header
        Button header = new Button("Header");
        HBox data = new HBox(stats, huffman, header);
        data.getStyleClass().add("pane");
        Label err = new Label();
        err.getStyleClass().add("error-label");
        VBox main = new VBox(title, operations, err);

        main.getStyleClass().add("pane");
        Scene scene = new Scene(main, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);

        //button events

        browse.setOnAction(e -> {
            file = chooseFile(stage, err);
            if (file != null) {
                if (!main.getChildren().contains(start)) {
                    main.getChildren().add(start);
                }
            }
        });


        start.setOnAction(e -> {
            if (isCompress) {
                c = new Compress(file);
                try {
                    c.compress();
                    headerString = c.header;
                } catch (Exception ex) {
                    err.setText(ex.getMessage());
                }
                if (c.isDone) {
                    err.setText("File Compressed Successfully");
                    if (!main.getChildren().contains(data)) {
                        main.getChildren().add(data);
                    }
                }
            } else {
                d = new Decompress(file);
                d.readToDecompress();
                headerString = d.header;
                if (d.isDone) {
                    err.setText("File Decompressed Successfully");
                    if (!main.getChildren().contains(data)) {
                        main.getChildren().add(data);
                    }
                }
            }
            file = null;
        });
        compress.setOnAction(e -> {
            err.setText("");
            isCompress = true;
            if (!main.getChildren().contains(browse)) {
                main.getChildren().add(browse);
            }
            main.getChildren().remove(data);

        });
        decompress.setOnAction(e -> {
            err.setText("");
            isCompress = false;
            if (!main.getChildren().contains(browse)) {
                main.getChildren().add(browse);
            }
            main.getChildren().remove(data);
        });

        header.setOnAction(e -> headerDialog(headerString));

        stats.setOnAction(e -> {
            long lengthb4;
            long lengthAfter;
            if (isCompress) {
                lengthb4 = c.inFile.length(); // file before compression
                lengthAfter = c.outFile.length(); //file after compression
            } else {
                lengthb4 = d.outFile.length(); //compressed file
                lengthAfter = d.inFile.length(); //compressed file
            }
            statsDialog(lengthb4, lengthAfter);
        });

        huffman.setOnAction(e -> {
            ObservableList<Node> list =  FXCollections.observableArrayList();
            if (isCompress) {
                c.getTreeLeaves(list,c.root);
            } else {
                d.getTreeLeaves(list,d.root);
            }
            huffDialog(list);

        });


    }

    //show huffman screen dialog
    //takes list of nodes as param to set table view
    private void huffDialog(ObservableList list) {
        Stage stage = new Stage();

        TableView table = getTable(list);
        Scene scene = new Scene(table, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    File file;
    //show header screen dialog
    //takes param header string to show to user in label
    public void headerDialog(String header) {
        Stage stage = new Stage();
        Label title = new Label("File Header:");
        Label h = new Label(header);
        h.getStyleClass().add("small");
        h.setWrapText(true);
        h.setMaxWidth(1000);
        VBox vBox = new VBox(title, h);
        vBox.getStyleClass().add("pane");
        Scene scene = new Scene(vBox, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    //show stats dialog
    //to show the user the file size before and after compression in addition to the compression percentage
    public void statsDialog(long unCompressed, long compressed) {
        Stage stage = new Stage();
        Label one = new Label("File size before compression: ");
        Label one1 = new Label((double) unCompressed / 1000 + " KBs");
        Label two = new Label("File size After compression: ");
        Label two1 = new Label((double) compressed / 1000 + " KBs");

        Label three = new Label("Compression percentage: ");
        Label three1 = new Label((1-(double) compressed / (double) unCompressed) * 100 + "%");

        HBox f = new HBox(one, one1);
        f.getStyleClass().add("pane");
        HBox s = new HBox(two, two1);
        s.getStyleClass().add("pane");
        HBox th = new HBox(three, three1);
        th.getStyleClass().add("pane");
        VBox vBox = new VBox(f, s, th);
        vBox.getStyleClass().add("pane");
        Scene scene = new Scene(vBox, 1080, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    //event handler for choosing file
    //param: stage to set new scene and Label to show possible errors
    public File chooseFile(Stage stage, Label error) {
        FileChooser fileChooser = new FileChooser();
        if (!isCompress) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Huffman files (.huff)", "*.huff"));
        }
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            error.setText("Select a File");
            return null;
        }
        if (isCompress) {
            if (file.getName().split("\\.").length > 0) {
                if (file.getName().split("\\.")[1].equals("huff")) {
                    error.setText("File is Already Compressed");
                    file = null;
                }
            }
        }
        return file;
    }

    //return table view object
    //param list of nodes
    public TableView getTable(ObservableList list){
        TableView<Node> tableView = new TableView<>();
        TableColumn<Node, Byte> indexColumn = new TableColumn<>("Byte");
        indexColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        indexColumn.setPrefWidth(200);

        TableColumn<Node, Integer> frequency = new TableColumn<>("Frequency");
        frequency.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        frequency.setPrefWidth(200);


        TableColumn<Node, String> codeColumn = new TableColumn<>("Huffman Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("huffmanCode"));
        codeColumn.setPrefWidth(200);

        TableColumn<Node, Integer> codeSizeColumn = new TableColumn<>("Code Size");
        codeSizeColumn.setCellValueFactory(new PropertyValueFactory<>("codeSize"));
        codeSizeColumn.setPrefWidth(200);

        tableView.getColumns().addAll(indexColumn, frequency,codeColumn,codeSizeColumn);


        tableView.setItems(list);
        return tableView;
    }


}
