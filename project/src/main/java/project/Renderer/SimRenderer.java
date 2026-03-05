package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;

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

        Mesh mesh = Mesh.icosphere(1.0f);

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

        VAO = glGenVertexArrays();        
        VBO[0] = glGenBuffers();
        VBO[1] = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(VAO);

        FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(mesh.getVertices().length);
        verticesBuffer.put(mesh.getVertices()).flip();

        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors).flip();

        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices).flip();

        // --- Vertex Attributes ---
        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3, 3 * Float.BYTES, VBO[0], verticesBuffer);
        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 1, 3, 3 * Float.BYTES, VBO[1], colorsBuffer);

        shaderProgram.bindElementBuffer(EBO, indicesBuffer, GL_STATIC_DRAW);

        shaderProgram.use();

        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        camera.getProjectionMatrix().get(projectionMatrixBuffer).flip();
        shaderProgram.addFloatUniform("projection", projectionMatrixBuffer);

        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        camera.getViewMatrix().get(viewMatrixBuffer).flip();
        shaderProgram.addFloatUniform("view", viewMatrixBuffer);

        // FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        // test.get(modelMatrixBuffer).flip();
       // shaderProgram.addFloatUniform("model", modelMatrixBuffer);

        Matrix4f test = new Matrix4f().identity();
        test.rotate((float)Math.toRadians(45), new Vector3f(0.5f, 0.5f, 0.5f));

        shaderProgram.use();

        FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniform("model", test.get(modelMatrixBuffer));
    }

    @Override
    public void loop() {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
