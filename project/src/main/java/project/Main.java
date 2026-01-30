package project;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.huskerdev.openglfx.GLExecutor;
import com.huskerdev.openglfx.canvas.GLCanvas;

public class Main extends Application {

    public void start(Stage stage) {
        GLCanvas.Builder builder = new GLCanvas.Builder();
        builder.setExecutor(new GLExecutor());
        builder.setFlipY(true);
        builder.setMSAA(4);
        builder.setFps(60.0);
        builder.setSwapBuffers(2);
        
        GLCanvas canvas = new GLCanvas(builder);

        Scene scene = new Scene(canvas, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        launch(args);
    }
}