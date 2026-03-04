package project;

import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.Renderer.Controls;
import project.Renderer.SimRenderer;
import project.Renderer.Camera.Camera;

import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main extends Application {
    // TODO: should probably make a class to hold these values (not Renderer since
    // that object will have to be recreated if the user decides to changes mssa or
    // swapbuffers variables)
    public static double fps = 60;
    public static int msaa = 4;
    public static int swapBuffers = 2;

    private long window;

    public void start(Stage stage) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(300, 300, "Window", NULL, NULL);
        if(window == NULL) {
            throw new RuntimeException("Failed to create GLFW window.");
        }

        setSystemProperties();

        StackPane rootPane = new StackPane();

        Camera camera = new Camera();
        camera.setViewYXZ(new Vector3f(0.0f, 0.0f, -2.5f), new Vector3f(0.0f, 0.0f, 0.0f));
        camera.setPerspectiveProjection((float)Math.toRadians(45.0f), 1280.0f/720.0f, 0.001f, 1.0f);

        SimRenderer renderer = new SimRenderer(fps, msaa, swapBuffers, camera);
        rootPane.getChildren().add(renderer.getCanvas());

        stage.setScene(new Scene(rootPane, 1280, 720));
        stage.setTitle("Orbital Motion Simulator");
        stage.show();

        //Controls controls = new Controls(renderer);
    }

    private void setSystemProperties() {
        System.setProperty("prism.vsync", "false");
    }

    public static void main(String[] args) {
        launch(args);
    }
}