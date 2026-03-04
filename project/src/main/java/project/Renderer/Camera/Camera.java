package project.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix = new Matrix4f(), viewMatrix = new Matrix4f(), inverseViewMatrix = new Matrix4f();

    public void setOrthographicProjection(float left, float right, float top, float bottom, float near, float far) {
        projectionMatrix.setOrtho(left, right, bottom, top, near, far);
    }

    public void setPerspectiveProjection(float fovy, float aspect, float near, float far) {
        projectionMatrix.setPerspective(fovy, aspect, near, far);
    }

    public void setViewDirection(Vector3f position, Vector3f direction, Vector3f up) {
        viewMatrix.setLookAt(position, direction, up);

        inverseViewMatrix = viewMatrix.invert();
    }

    public void setViewTarget(Vector3f position, Vector3f target, Vector3f up) {
        setViewDirection(position, target, up);
    }

    public void setViewYXZ(Vector3f position, Vector3f rotation) {
        viewMatrix.setRotationYXZ(rotation.y, rotation.x, rotation.z);

        inverseViewMatrix = viewMatrix.invert();
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return this.inverseViewMatrix;
    }
}
