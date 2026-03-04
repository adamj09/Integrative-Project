package project.Renderer;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.Camera.Camera;

public class SimRenderer extends Renderer {
    private ShaderProgram shaderProgram;
    private Camera camera;
    private int VAO, EBO;
    private int[] VBO = new int[2];

    public SimRenderer(double fps, int msaa, int swapBuffers, Camera camera) {
        super(fps, msaa, swapBuffers);
        this.camera = camera;
    }

    @Override
    public void init() {
        shaderProgram = new ShaderProgram(
                "project/shaders/main.vert",
                "project/shaders/main.frag"
            );

        float[] vertices = {
                0.5f,  0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                -0.5f,  0.5f, 0.0f
        };

        float[] colors = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f
        };
  
        int[] indices = {
            0, 1, 3,
            1, 2, 3
        };

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length);
        verticesBuffer.put(vertices).flip();

        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors).flip();

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        Matrix4f test = new Matrix4f().scale(0.5f);

        VAO = glGenVertexArrays();        
        VBO[0] = glGenBuffers();
        VBO[1] = glGenBuffers();
        EBO = glGenBuffers();

        // Bind VAO FIRRSSTT then do everything else
        glBindVertexArray(VAO);

        // --- Vertex Attributes ---

        // Note: order matters here: whatever buffer is binded must immediately be given
        // an attribute pointer BEFORE binding the next buffer.
        glBindBuffer(GL_ARRAY_BUFFER, VBO[0]);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, VBO[1]);
        glBufferData(GL_ARRAY_BUFFER, colorsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glEnable(GL_DEPTH_TEST);

        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        camera.getViewMatrix().get(viewMatrixBuffer);

        int viewLoc = glGetUniformLocation(shaderProgram.getID(), "view");
        glUniformMatrix4fv(viewLoc, false, viewMatrixBuffer);

        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        camera.getProjectionMatrix().get(projectionMatrixBuffer);

        int projectionLoc = glGetUniformLocation(shaderProgram.getID(), "projection");
        glUniformMatrix4fv(projectionLoc, false, projectionMatrixBuffer);

        shaderProgram.use();
    }

    @Override
    public void loop() {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for(int i = 0; i < 3; i++) {

        }

        // Bind EBO and draw
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }
}
