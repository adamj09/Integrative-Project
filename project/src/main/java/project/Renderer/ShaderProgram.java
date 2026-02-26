package project.Renderer;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL46.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShaderProgram {
    private int ID;

    ShaderProgram(String vertFilepath, String fragFilepath) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        var vertexShaderSourceFuture = executor.submit(new ShaderLoaderTask(vertFilepath));
        var fragmentShaderSourceFuture = executor.submit(new ShaderLoaderTask(fragFilepath));

        String vertexShaderSource = "", fragmentShaderSource = "";

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            executor.shutdown();


            vertexShaderSource = vertexShaderSourceFuture.get();
            fragmentShaderSource = fragmentShaderSourceFuture.get();
        } catch (InterruptedException | ExecutionException ex) {
            // TODO: handle exceptions
        }

        // Create and compile shaders.
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        // Check for failed compilation, print errors, and exit.
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == 0) {
            System.err.println("Vertex shader compilation failed: " + glGetShaderInfoLog(vertexShader));
            System.exit(1);
        }
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == 0) {
            System.err.println("Vertex shader compilation failed: " + glGetShaderInfoLog(fragmentShader));
            System.exit(1);
        }

        // Create the shader program using the two shaders.
        ID = glCreateProgram();
        glAttachShader(ID, vertexShader);
        glAttachShader(ID, fragmentShader);
        glLinkProgram(ID);

        // Check for failed linking, print errors, and exit.
        if (glGetProgrami(ID, GL_LINK_STATUS) == 0) {
            System.err.println("Linking shader program failed: " + glGetProgramInfoLog(ID));
            System.exit(1);
        }

        // Delete shaders as they are no longer needed after program creation and
        // linking.
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(ID);
    }

    private class ShaderLoaderTask implements Callable<String> {
        String filepath;

        public ShaderLoaderTask(String filepath) {
            this.filepath = filepath;
        }

        @Override
        public String call() {
            // Create new file for shader and check if exists.
            File file = new File(filepath);
            if (!file.exists()) {
                System.err.println("Shader specified at " + filepath + " could not be found!");
                System.exit(1);
            }

            // Read shader file.
            String source = "";
            try {
                source = Files.readString(Path.of(file.getPath()));
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }

            return source;
        }
    }
}
