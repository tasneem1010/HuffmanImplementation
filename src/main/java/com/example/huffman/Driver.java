package com.example.huffman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

//driver class to run the program
public class Driver extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainScreen menu = new MainScreen();
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMinX());
        stage.setY(primaryScreenBounds.getMinY());
        stage.setWidth(primaryScreenBounds.getWidth());
        stage.setHeight(primaryScreenBounds.getHeight());
        menu.showScreen(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}