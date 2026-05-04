package oms.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Represents an OpenGL shader.
 * 
 * @author Adam Johnston
 */
public class Shader {
    /**
     * The OpenGL shader ID.
     */
    private int shader;

    /**
     * The OpenGL shader type.
     */
    private int shaderType;

    /**
     * Creates a new shader from the specified file path and shader type (loads and
     * compiles the shader).
     * 
     * @param filename   The name of the file containing the shader source code.
     * @param shaderType The OpenGL shader type.
     */
    public Shader(String filename, int shaderType) {
        InputStream stream = getClass().getResourceAsStream("/shaders/" + filename);
        Scanner scanner = new Scanner(stream).useDelimiter("\\A");

        String source = "";

        while(scanner.hasNext()) {
            source += scanner.next();
        }
        scanner.close();

        // Create and compile shaders.
        shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);

        // Check for failed compilation, print errors, and exit.
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.err.println("Shader compilation failed: " + glGetShaderInfoLog(shader) + "(" + filename + ")");
            System.exit(1);
        }

        this.shaderType = shaderType;
    }

    /**
     * Deletes the OpenGL shader object.
     */
    public void dispose() {
        glDeleteShader(shader);
    }

    /**
     * @return The OpenGL shader ID.
     */
    public int getShader() {
        return shader;
    }

    /**
     * @return The OpenGL shader type.
     */
    public int getShaderType() {
        return shaderType;
    }
}
