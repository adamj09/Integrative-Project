package project.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Class that handles the camera's projection and view matrices, as well as its
 * position and direction in world space.
 * 
 * @author Adam Johnston
 */
public class Camera {
    /**
     * Projection and view matrices, initially as identity matrices.
     */
    private Matrix4f projection = new Matrix4f(),
            view = new Matrix4f(),
            inverseView = new Matrix4f();

    /**
     * Camera position, direction, and up vector in world space.
     */
    private Vector3f position = new Vector3f(),
            direction = new Vector3f(0.f, 0.f, -1.f),
            up = new Vector3f(0.f, 1.f, 0.f);

    /**
     * Set the camera's projection matrix to an orthographic projection matrix.
     * @param left The distance from the camera's center to the left plane.
     * @param right The distance from the camera's center to the right plane.
     * @param top The distance from the camera's center to the top plane.
     * @param bottom The distance from the camera's center to the bottom plane.
     * @param near The distance from the camera's center to the near plane
     * @param far The distance from the camera's center to the far plane.
     */
    public void setOrthographicProjection(float left, float right, float top, float bottom, float near, float far) {
        projection.setOrtho(left, right, bottom, top, near, far);
    }

    /**
     * Set the camera's projection matrix to a perspective projection matrix.
     * @param fovy The vertical field of view angle in radians.
     * @param aspect The aspect ratio of the viewport.
     * @param near The distance from the camera's center to the near plane.
     * @param far The distance from the camera's center to the far plane.
     */
    public void setPerspectiveProjection(float fovy, float aspect, float near, float far) {
        projection.setPerspective(fovy, aspect, near, far);
    }

    /**
     * Set the camera's view matrix based on the given position and direction.
     * @param position Position of the camera in world space.
     * @param direction Direction the camera is facing in world space.
     */
    public void setView(Vector3f position, Vector3f target) {
        view.setLookAt(position, target, up);

        target.sub(position, this.direction);
        this.direction.normalize();

        this.position.set(position);

        updateInverseView();
    }


    private void updateInverseView() {
        inverseView.set(view);
        inverseView.invert();
    }

    /**
     * Resets the camera's view to its initial state.
     */
    public void reset() {
        setView(new Vector3f(), new Vector3f(0.f, 0.f, 1.f));
    }

    /**
     * @return The camera's projection matrix.
     */
    public Matrix4f getProjection() {
        return this.projection;
    }

    /**
     * @return The camera's view matrix.
     */
    public Matrix4f getView() {
        return this.view;
    }

    /**
     * @return The camera's inverse view matrix.
     */
    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    /**
     * @return The camera's position in world space.
     */
    public Vector3f getPosition() {
        return this.position;
    }

    /**
     * @return The camera's direction in world space.
     */
    public Vector3f getDirection() {
        return this.direction;
    }

    /**
     * @return The camera's up vector in world space.
     */
    public Vector3f getUp() {
        return this.up;
    }
}
