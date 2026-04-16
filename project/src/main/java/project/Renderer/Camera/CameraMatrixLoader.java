package project.Renderer.Camera;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import project.Renderer.Renderer;

import static org.lwjgl.opengl.GL41.*;

/**
 * Class responsible for sending camera matrices to shaders as a uniform buffer
 * object.
 * 
 * @author Adam Johnston
 */
public class CameraMatrixLoader {
    /**
     * Uniform buffer object for the camera matrices (projection, view, inverse
     * view).
     */
    private int uboCameraMatrices;

    /**
     * The camera for which to send matrices.
     */
    private Camera camera;

    /**
     * Buffers for storing the camera matrices.
     */
    private FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16),
            viewMatrixBuffer = BufferUtils.createFloatBuffer(16),
            inverseViewMatrixBuffer = BufferUtils.createFloatBuffer(16);

    /**
     * Constructor for the CameraRenderSystem class.
     * 
     * @param camera The camera for which to send matrices.
     */
    public CameraMatrixLoader(Camera camera) {
        this.camera = camera;

        init();
    }

    /**
     * Initializes the uniform buffer object for the camera matrices.
     */
    private void init() {
        uboCameraMatrices = glGenBuffers();
        int size = 3 * Renderer.MAT4F_SIZE;

        glBindBuffer(GL_UNIFORM_BUFFER, uboCameraMatrices);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uboCameraMatrices, 0, size);
    }

    /**
     * Updates the camera matrices in the uniform buffer object. This should be
     * called once per frame.
     */
    public void update() {
        glBindBuffer(GL_UNIFORM_BUFFER, uboCameraMatrices);

        projectionMatrixBuffer.clear();
        glBufferSubData(GL_UNIFORM_BUFFER, 0, camera.getProjection().get(projectionMatrixBuffer));

        viewMatrixBuffer.clear();
        glBufferSubData(GL_UNIFORM_BUFFER, Renderer.MAT4F_SIZE, camera.getView().get(viewMatrixBuffer));

        inverseViewMatrixBuffer.clear();
        glBufferSubData(GL_UNIFORM_BUFFER, 2 * Renderer.MAT4F_SIZE,
                camera.getInverseView().get(inverseViewMatrixBuffer));

        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
