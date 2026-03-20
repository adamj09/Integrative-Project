package project.Renderer;

import project.Renderer.Camera.Camera;
import project.Renderer.World.World;
import project.Renderer.World.WorldObject;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.util.Map;

import org.lwjgl.BufferUtils;

public class RenderSystem {
    private Camera camera;
    private World world;
    private WorldObject body;

    private int VAO, EBO, vboVertices, vboColors, vboModelMatrices;
    private int uboMatrices;
    private int indexCount;

    private static ShaderProgram shaderProgram;
    private static String vertexShaderPath = "project/shaders/main.vert",
            fragmentShaderPath = "project/shaders/main.frag";

    public RenderSystem(World world) {
        this.world = world;
        this.camera = world.getCamera();
        this.body = world.getBody();

        this.world.packColorsIntoBuffer();

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
        setUpVertexBuffers();
        setUpIndexBuffer();
        setUpUniforms();
    }

    private void setUpVertexBuffers() {
        body.getMesh().packVerticesIntoBuffer();
        body.getMesh().packIndicesIntoBuffer();

        VAO = glGenVertexArrays();
        vboVertices = glGenBuffers();
        vboColors = glGenBuffers();

        glBindVertexArray(VAO);

        // Vertices
        glBindBuffer(GL_ARRAY_BUFFER, vboVertices);
        glBufferData(GL_ARRAY_BUFFER, body.getMesh().getVertexBuffer(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        // Colors
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, world.getColorsBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glVertexAttribDivisor(1, 1);

        // Model Matrices (for satellites and body)
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, (world.getSatellites().size() + 1) * 16 * Float.BYTES, GL_STATIC_DRAW);

        for(Map.Entry<String, WorldObject> set : world.getSatellites().entrySet()) {
            
        }
    }

    private void setUpIndexBuffer() {
        EBO = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, body.getMesh().getIndexBuffer(), GL_STATIC_DRAW);

        indexCount = body.getMesh().getIndices().size() * 3;
    }

    private void setUpUniforms() {
        shaderProgram.addUniformVec4f("color", world.getBody().getColor());

        int uniformBlockIndex = glGetUniformBlockIndex(shaderProgram.getID(), "Matrices");
        glUniformBlockBinding(shaderProgram.getID(), uniformBlockIndex, 0);

        uboMatrices = glGenBuffers();
        int size = 2 * Float.BYTES * 16;

        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uboMatrices, 0, size);

        FloatBuffer modelMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addUniformMat4f("model", body.getTransformMatrix().get(modelMatrixBuffer));
    }

    private void updateUniforms() {
        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, camera.getProjection().get(projectionMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, Float.BYTES * 16, camera.getView().get(viewMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }

    private void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, 1);
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
        vboVertices = 0;
        vboColors = 0;
        EBO = 0;

        setUpBuffers();
    }
}
