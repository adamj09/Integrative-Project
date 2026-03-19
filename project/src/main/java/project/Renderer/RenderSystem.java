package project.Renderer;

import project.Renderer.Camera.Camera;
import project.Renderer.World.World;
import project.Renderer.World.WorldObject;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class RenderSystem {
    private Camera camera;
    private World world;
    private WorldObject body;

    private int VAO, EBO, VBO;
    private int indexCount;

    private static ShaderProgram shaderProgram;
    private static String vertexShaderPath = "project/shaders/main.vert", fragmentShaderPath = "project/shaders/main.frag";

    public RenderSystem(World world) {
        this.world = world;
        this.camera = world.getCamera();
        body = world.getBody();

        init();
    }

    public void init() {
        // Set up and use shader program
        shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
        shaderProgram.use();

        setUpBuffers();
    }

    public void setUpBuffers() {
        setUpVertexBuffer();
        setUpIndexBuffer();
        setUpUniforms();
    }

    public void loop(float deltaTime) {
        updateUniforms();
        draw();
    }

    public void setUpVertexBuffer() {
        body.getMesh().packVerticesIntoBuffer();
        body.getMesh().packIndicesIntoBuffer();

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        glBindVertexArray(VAO);

        shaderProgram.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3, 3 * Float.BYTES, VBO,
                body.getMesh().getVertexBuffer());
    }

    public void setUpIndexBuffer() {
        EBO = glGenBuffers();

        shaderProgram.bindElementBuffer(EBO, body.getMesh().getIndexBuffer(), GL_STATIC_DRAW);
        indexCount = body.getMesh().getIndices().size() * 3;
    }

    public void setUpUniforms() {
        shaderProgram.addFloatUniformVec4("color", world.getBody().getColor());

        FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniformMat4("model", body.getTransformMatrix().get(modelMatrixBuffer));
    }

    // TODO: move matrix buffers to camera class so that we aren't recreating buffers for every shader program
    public void updateUniforms() {
        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniformMat4("view", camera.getView().get(viewMatrixBuffer));

        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniformMat4("projection", camera.getProjection().get(projectionMatrixBuffer));
    }

    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    public int getEBO() {
        return EBO;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public void setWorld(World world) {
        this.world = world;
        this.camera = world.getCamera();
        VAO = 0;
        VBO = 0;
        EBO = 0;

        setUpBuffers();
    }
}
