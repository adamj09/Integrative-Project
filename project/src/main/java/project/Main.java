package project;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.Renderer.SimRenderer;

public class Main extends Application {

    public void start(Stage stage) {
        System.setProperty("prism.vsync", "false");

        StackPane rootPane = new StackPane();

        SimRenderer renderer = new SimRenderer();

        rootPane.getChildren().add(renderer.getCanvas());

        stage.setScene(new Scene(rootPane, 1280, 720));
        stage.setTitle("Orbital Motion Simulator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}