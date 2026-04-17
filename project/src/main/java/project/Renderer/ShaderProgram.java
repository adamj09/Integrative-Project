package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Class representing an OpenGL shader program.
 * 
 * @author Adam Johnston
 */
public class ShaderProgram {
    /**
     * The OpenGL shader program ID.
     */
    private int ID;

    private HashMap<String, Integer> uniformBlockBindings = new HashMap<>();

    /**
     * Creates a new shader program from the specified vertex and fragment shaders
     * (links the shaders).
     * 
     * @param vertexShader   vertex shader to link.
     * @param fragmentShader fragment shader to link.
     */
    public ShaderProgram(int vertexShader, int fragmentShader) {
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
    }

    /**
     * Activates the shader program for use.
     */
    public void use() {
        glUseProgram(ID);
    }

    /**
     * Adds a 4x4 float matrix uniform to the shader program.
     * 
     * @param name   the name of the uniform variable in the shader program.
     * @param buffer the FloatBuffer containing the matrix data (must be in
     *               column-major order).
     */
    public void addUniformMat4f(String name, FloatBuffer buffer) {
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, buffer);
    }

    /**
     * Adds a 3D float vector uniform to the shader program.
     * 
     * @param name the name of the uniform variable in the shader program.
     * @param vec  the Vector3f to be added.
     */
    public void addUniformVec3f(String name, Vector3f vec) {
        glUniform3f(glGetUniformLocation(ID, name), vec.x, vec.y, vec.z);
    }

    /**
     * Adds a 2D float vector uniform to the shader program.
     * 
     * @param name the name of the uniform variable in the shader program.
     * @param vec  the Vector2f to be added.
     */
    public void addUniformVec2f(String name, Vector2f vec) {
        glUniform2f(glGetUniformLocation(ID, name), vec.x, vec.y);
    }

    /**
     * Adds a uniform block binding to the shader program.
     * 
     * @param blockName    the name of the uniform block in the shader program.
     * @param blockBinding the uniform block binding point.
     */
    public void addUniformBlockBinding(String blockName, int blockBinding) {
        if (!uniformBlockBindings.containsKey(blockName)) {
            glUniformBlockBinding(ID, glGetUniformBlockIndex(ID, blockName), blockBinding);
            uniformBlockBindings.put(blockName, blockBinding);
        }
    }

    public void dispose() {
        glDeleteProgram(ID);
    }

    /**
     * @return The OpenGL shader program ID.
     */
    public int getID() {
        return ID;
    }
}
