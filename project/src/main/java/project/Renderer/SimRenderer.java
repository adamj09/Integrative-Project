package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.Camera.Camera;
import project.Renderer.Camera.FirstPersonCameraController;
import project.Renderer.Model.Mesh;
import project.Renderer.Model.SphereGenerator;

public class SimRenderer {
    private ShaderProgram shaderProgram;

    private Camera camera;
    private FirstPersonCameraController cameraController;

    private int VAO, EBO;
    private int[] VBO = new int[2];

    private Mesh mesh;

    public SimRenderer(Camera camera, ControlManager controlManager) {
        this.camera = camera;
        cameraController = new FirstPersonCameraController(camera, controlManager);
    }

    public void init() {
        shaderProgram = new ShaderProgram(
                "project/shaders/main.vert",
                "project/shaders/main.frag"
            );

        SphereGenerator generator = new SphereGenerator();

        mesh = generator.create(1);
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
        updateCamera(deltaTime);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glDrawElements(GL_TRIANGLES, mesh.getIndices().size() * 3, GL_UNSIGNED_INT, 0);
    }

    private void updateCamera(float deltaTime) {
        cameraController.updateCameraTransform(deltaTime);

        FloatBuffer viewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniform("view", camera.getView().get(viewMatrixBuffer));

        FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
        shaderProgram.addFloatUniform("projection", camera.getProjection().get(projectionMatrixBuffer));
    }
}
