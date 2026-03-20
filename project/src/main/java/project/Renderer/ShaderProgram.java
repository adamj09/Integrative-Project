package project.Renderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.joml.Vector4f;

public class ShaderProgram {
    private int ID;

    public ShaderProgram(String vertFilepath, String fragFilepath) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        String vertexShaderSource = "", fragmentShaderSource = "";

        try {
            List<Future<String>> futures = executor.invokeAll(Arrays.asList(new ShaderLoaderTask(vertFilepath), new ShaderLoaderTask(fragFilepath)));
            vertexShaderSource = futures.get(0).get();
            fragmentShaderSource = futures.get(1).get();

            executor.shutdown();
        } catch (InterruptedException | ExecutionException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
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
            System.err.println("Fragment shader compilation failed: " + glGetShaderInfoLog(fragmentShader));
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

    public void addUniformMat4f(String name, FloatBuffer buffer) {
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, buffer);
    }

    public void addUniformVec4f(String name, Vector4f vec) {
        glUniform4f(glGetUniformLocation(ID, name), vec.x, vec.y, vec.z, vec.w);
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

    public int getID() {
        return ID;
    }
}
