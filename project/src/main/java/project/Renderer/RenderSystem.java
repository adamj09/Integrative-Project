package project.Renderer;

import project.Renderer.Camera.Camera;
import project.Renderer.World.World;
import project.Renderer.World.WorldObject;

import static org.lwjgl.opengl.GL41.*;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class RenderSystem {
    private Camera camera;
    private World world;
    private WorldObject body;

    private int VAO, EBO, VBO;
    private int uboMatrices;
    private int indexCount;

    private static ShaderProgram shaderProgram;
    private static String vertexShaderPath = "project/shaders/main.vert",
            fragmentShaderPath = "project/shaders/main.frag";

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

        glEnable(GL_DEPTH_TEST);

        setUpBuffers();
    }

    public void loop(float deltaTime) {
        updateUniforms();
        draw();
    }

    private void setUpBuffers() {
        setUpVertexBuffer();
        setUpIndexBuffer();
        setUpUniforms();
    }

    private void setUpVertexBuffer() {
        body.getMesh().packVerticesIntoBuffer();
        body.getMesh().packIndicesIntoBuffer();

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        glBindVertexArray(VAO);

        BufferTools.bindVertexBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, 0, 3, 3 * Float.BYTES, VBO,
                body.getMesh().getVertexBuffer());
    }

    private void setUpIndexBuffer() {
        EBO = glGenBuffers();

        BufferTools.bindElementBuffer(EBO, body.getMesh().getIndexBuffer(), GL_STATIC_DRAW);
        indexCount = body.getMesh().getIndices().size() * 3;
    }

    private void setUpUniforms() {
        shaderProgram.addUniformVec4f("color", world.getBody().getColor());

        int uniformBlockIndex = glGetUniformBlockIndex(shaderProgram.getID(), "Matrices");
        glUniformBlockBinding(shaderProgram.getID(), uniformBlockIndex, 0);

        uboMatrices = glGenBuffers();
        int size = 2 * Float.BYTES * 16;

        BufferTools.bindUniformBufferObject(uboMatrices, size, GL_STATIC_DRAW, 0);

        FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addUniformMat4f("model", body.getTransformMatrix().get(modelMatrixBuffer));
    }

    private void updateUniforms() {
        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        BufferTools.updateUniformBufferFloatData(uboMatrices, 0,
                camera.getProjection().get(projectionMatrixBuffer), 0);

        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        BufferTools.updateUniformBufferFloatData(uboMatrices, Float.BYTES * 16, camera.getView().get(viewMatrixBuffer), 0);
    }

    private void draw() {
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
