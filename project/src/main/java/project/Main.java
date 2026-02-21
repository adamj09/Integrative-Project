package project;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.Renderer.SimRenderer;

public class Main extends Application {
    // TODO: should probably make a class to hold these values (not Renderer since
    // that object will have to be recreated if the user decides to changes mssa or
    // swapbuffers variables)
    public static double fps = 60;
    public static int msaa = 4;
    public static int swapBuffers = 2;

    public void start(Stage stage) {
        setSystemProperties();

        StackPane rootPane = new StackPane();

        SimRenderer renderer = new SimRenderer(fps, msaa, swapBuffers);
        rootPane.getChildren().add(renderer.getCanvas());

        stage.setScene(new Scene(rootPane, 1280, 720));
        stage.setTitle("Orbital Motion Simulator");
        stage.show();
    }

    private void setSystemProperties() {
        System.setProperty("prism.vsync", "false");
    }

    public static void main(String[] args) {
        launch(args);
    }
}