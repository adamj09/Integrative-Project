package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Shader {
    private int shader;
    private int shaderType;

    public Shader(String filepath, int shaderType) {
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

        // Create and compile shaders.
        shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);

        // Check for failed compilation, print errors, and exit.
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.err.println("Shader compilation failed: " + glGetShaderInfoLog(shader) + "(" + filepath + ")");
            System.exit(1);
        }

        this.shaderType = shaderType;
    }

    public int getShader() {
        return shader;
    }

    public int getShaderType() {
        return shaderType;
    }
}
