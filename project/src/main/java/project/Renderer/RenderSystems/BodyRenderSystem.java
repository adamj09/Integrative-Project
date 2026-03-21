package project.Renderer.RenderSystems;

import project.Renderer.ShaderProgram;
import project.Renderer.Camera.Camera;
import project.Renderer.World.World;
import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class BodyRenderSystem {
    private Camera camera;
    private World world;

    private int vboColors, vboModelMatrices;
    private int uboMatrices;

    private static ShaderProgram shaderProgram;
    private static String vertexShaderPath = "project/shaders/body.vert",
            fragmentShaderPath = "project/shaders/body.frag";

    private final int MAT4F_SIZE = 16 * Float.BYTES, VEC4F_SIZE = 4 * Float.BYTES;

    public BodyRenderSystem(World world) {
        this.world = world;
        this.camera = world.getCamera();

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
        update();
        draw();
    }

    private void setUpBuffers() {
        setUpVertexBuffers();
        setUpUniforms();
    }

    private void setUpVertexBuffers() {
        vboColors = glGenBuffers();

        // Colors
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, world.getColorsBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, VEC4F_SIZE, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glVertexAttribDivisor(1, 1);

        // Model Matrices (for satellites and body)
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getMatricesBuffer(), GL_STATIC_DRAW);

        glBindVertexArray(world.getSphere().getVAO());

        // Model matrix attribute pointers. Note that we need to do this four times,
        // since the maximum size of an attribute is equivalent to a Vector4f. I.e.
        // setting up 4 Vector4fs is equivalent to setting up the Matrix4f. which is the
        // data structure we're trying to send over to our shader.
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_FLOAT, false, MAT4F_SIZE, 0);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, MAT4F_SIZE, VEC4F_SIZE);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, MAT4F_SIZE, 2 * VEC4F_SIZE);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, MAT4F_SIZE, 3 * VEC4F_SIZE);

        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);

        glBindVertexArray(0);
    }

    private void setUpUniforms() {
        int uniformBlockIndex = glGetUniformBlockIndex(shaderProgram.getID(), "Matrices");
        glUniformBlockBinding(shaderProgram.getID(), uniformBlockIndex, 0);

        uboMatrices = glGenBuffers();
        int size = 2 * MAT4F_SIZE;

        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uboMatrices, 0, size);
    }

    private void update() {
        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, camera.getProjection().get(projectionMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        glBindBuffer(GL_UNIFORM_BUFFER, uboMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, MAT4F_SIZE, camera.getView().get(viewMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        // Update model transformation matrices.
        world.updateMatrixBuffer();

        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getMatricesBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void draw() {
        glBindVertexArray(world.getSphere().getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, world.getSphere().getIndices().size() * 3, GL_UNSIGNED_INT, 0, world.getBodies().size() + 1);
    }

    public void setWorld(World world) {
        this.world = world;
        this.camera = world.getCamera();
        vboColors = 0;

        setUpBuffers();
    }
}
