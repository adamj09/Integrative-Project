package project.Renderer.RenderSystems;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import project.Renderer.Renderer;
import project.Renderer.Camera.Camera;

import static org.lwjgl.opengl.GL41.*;

public class CameraRenderSystem {
    private int uboCameraMatrices;

    private Camera camera;
    private FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16),
            viewMatrixBuffer = BufferUtils.createFloatBuffer(16),
            inverseViewMatrixBuffer = BufferUtils.createFloatBuffer(16);

    public CameraRenderSystem(Camera camera) {
        this.camera = camera;

        init();
    }

    private void init() {
        uboCameraMatrices = glGenBuffers();
        int size = 3 * Renderer.MAT4F_SIZE;

        glBindBuffer(GL_UNIFORM_BUFFER, uboCameraMatrices);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_STATIC_DRAW);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uboCameraMatrices, 0, size);
    }

    public void loop() {
        projectionMatrixBuffer.clear();
        glBindBuffer(GL_UNIFORM_BUFFER, uboCameraMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, camera.getProjection().get(projectionMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        viewMatrixBuffer.clear();
        glBindBuffer(GL_UNIFORM_BUFFER, uboCameraMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, Renderer.MAT4F_SIZE, camera.getView().get(viewMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        inverseViewMatrixBuffer.clear();
        glBindBuffer(GL_UNIFORM_BUFFER, uboCameraMatrices);
        glBufferSubData(GL_UNIFORM_BUFFER, 2 * Renderer.MAT4F_SIZE, camera.getInverseView().get(inverseViewMatrixBuffer));
        glBindBuffer(GL_UNIFORM_BUFFER, 0);
    }
}
