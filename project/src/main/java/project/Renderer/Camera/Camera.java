package project.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projection = new Matrix4f(),
            view = new Matrix4f(),
            inverseView = new Matrix4f();
    private Vector3f position = new Vector3f(),
        direction = new Vector3f(0.f, 0.f, -1.f),
        up = new Vector3f(0.f, 1.f, 0.f);

    public void setOrthographicProjection(float left, float right, float top, float bottom, float near, float far) {
        projection.setOrtho(left, right, bottom, top, near, far);
    }

    public void setPerspectiveProjection(float fovy, float aspect, float near, float far) {
        projection.setPerspective(fovy, aspect, near, far);
    }

    public void setView(Vector3f position, Vector3f direction) {
        Vector3f directionDifference = new Vector3f();
        direction.sub(this.direction, directionDifference);

        // Get the target vector for camera's view direction.
        Vector3f target = new Vector3f();
        position.add(direction, target);

        view.setLookAt(position, target, up);

        this.direction.set(direction);
        this.position.set(position);
    }

    public Matrix4f getProjection() {
        return this.projection;
    }

    public Matrix4f getView() {
        return this.view;
    }

    public Matrix4f getInverseView() {
        return this.inverseView;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public Vector3f getDirection() {
        return this.direction;
    }

    public Vector3f getUp() {
        return this.up;
    }
}
