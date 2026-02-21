package project;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

import com.huskerdev.openglfx.canvas.GLCanvas;

import static org.lwjgl.opengl.GL11.*;

public class Main extends Application {

    public void start(Stage stage) {
        System.setProperty("prism.vsync", "false");

        StackPane rootPane = new StackPane();

        rootPane.getChildren().add(createGLCanvas());

        stage.setScene(new Scene(rootPane, 1280, 720));
        stage.setTitle("Orbital Motion Simulator");
        stage.show();
    }

    private GLCanvas createGLCanvas() {

        GLCanvas.Builder glCanvasBuilder = new GLCanvas.Builder();
        glCanvasBuilder.setFlipY(true);
        glCanvasBuilder.setExecutor(LWJGLExecutor.LWJGL_MODULE);
        glCanvasBuilder.setFps(60);
        glCanvasBuilder.setMSAA(4);
        glCanvasBuilder.setSwapBuffers(2);

        var canvas = glCanvasBuilder.build();

        canvas.addOnInitEvent(_ -> {

        });

        canvas.addOnRenderEvent(_ -> {
            glClearColor(1.0f, 1.0f, 0.0f, 1.0f);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
        });

        return canvas;
    }

    public static void main(String[] args) {
        launch(args);
    }
}