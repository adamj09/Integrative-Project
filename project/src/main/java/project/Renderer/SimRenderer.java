package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;

public class SimRenderer {
    private ShaderProgram shaderProgram;
    private String vertexShaderPath = "project/shaders/main.vert", fragmentShaderPath = "project/shaders/main.frag";

    private Camera camera;

    private int VAO, EBO;
    private int[] VBO = new int[2];

    private World world = new World();

    private Mesh mesh;

    public SimRenderer(Camera camera) {
        this.camera = camera;
    }

    public void setUpBuffers() {
        
    }

    public void init() {
        shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);

        mesh = world.getObjects().get("name").getMesh();
        mesh.packVerticesIntoBuffer();
        mesh.packIndicesIntoBuffer();

        float[] colors = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f
        };

        VAO = glGenVertexArrays();        
        VBO[0] = glGenBuffers();
        VBO[1] = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(VAO);

        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(colors.length);
        colorsBuffer.put(colors).flip();

        // --- Vertex Attributes ---
        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3, 3 * Float.BYTES, VBO[0], mesh.getVertexBuffer());
        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 1, 3, 3 * Float.BYTES, VBO[1], colorsBuffer);

        shaderProgram.bindElementBuffer(EBO, mesh.getIndexBuffer(), GL_STATIC_DRAW);

        Matrix4f test = new Matrix4f().identity();
        test.translate(new Vector3f(0.f,0.f, -10.f));

        shaderProgram.use();

        FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniform("model", test.get(modelMatrixBuffer));

        glEnable(GL_DEPTH_TEST);
    }

    public void loop(float deltaTime) {
        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniform("view", camera.getView().get(viewMatrixBuffer));

        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniform("projection", camera.getProjection().get(projectionMatrixBuffer));

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glDrawElements(GL_TRIANGLES, mesh.getIndices().size() * 3, GL_UNSIGNED_INT, 0);
    }
}
