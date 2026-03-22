package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.joml.Vector3f;

public class ShaderProgram {
    private int ID;

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

    public void use() {
        glUseProgram(ID);
    }

    public void addUniformMat4f(String name, FloatBuffer buffer) {
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, buffer);
    }

    public void addUniformVec3f(String name, Vector3f vec) {
        glUniform3f(glGetUniformLocation(ID, name), vec.x, vec.y, vec.z);
    }

    public int getID() {
        return ID;
    }
}
