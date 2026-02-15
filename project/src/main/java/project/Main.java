package project;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;
import com.huskerdev.openglfx.canvas.GLCanvas;

public class Main extends Application {

    public void start(Stage stage) {
        var canvas = new GLCanvas.Builder().setExecutor(LWJGLExecutor.LWJGL_MODULE).build();

        Scene scene = new Scene(canvas, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        launch(args);
    }
}