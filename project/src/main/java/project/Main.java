package project;

import javafx.application.*;
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
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        launch(null);
    }
}