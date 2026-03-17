package project.Renderer.Renderers;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.ShaderProgram;
import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;
import project.Renderer.World.World;

public class SimRenderer {
    private ShaderProgram shaderProgram;
    private String vertexShaderPath = "project/shaders/main.vert", fragmentShaderPath = "project/shaders/main.frag";

    private Camera camera;

    private int VAO, EBO;
    private int[] VBO = new int[2];

    private World world = new World("Test");

    private Mesh bodyMesh;

    public SimRenderer(Camera camera) {
        this.camera = camera;
    }

    public void setUpData() {

    }

    public void init() {
        shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);

        bodyMesh = world.getBody().getMesh();
        bodyMesh.packVerticesIntoBuffer();
        bodyMesh.packIndicesIntoBuffer();

        VAO = glGenVertexArrays();
        VBO[0] = glGenBuffers();
        VBO[1] = glGenBuffers();
        EBO = glGenBuffers();

        glBindVertexArray(VAO);

        FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(3);
        world.getBody().getColor().get(colorsBuffer);

        // --- Vertex Attributes ---
        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3, 3 * Float.BYTES, VBO[0],
                bodyMesh.getVertexBuffer());
        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 1, 3, 3 * Float.BYTES, VBO[1], colorsBuffer);

        shaderProgram.bindElementBuffer(EBO, bodyMesh.getIndexBuffer(), GL_STATIC_DRAW);

        Matrix4f test = new Matrix4f().identity();
        test.translate(new Vector3f(0.f, 0.f, -10.f));

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
        glDrawElements(GL_TRIANGLES, bodyMesh.getIndices().size() * 3, GL_UNSIGNED_INT, 0);
    }
}
