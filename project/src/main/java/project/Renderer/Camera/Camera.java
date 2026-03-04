package project.Renderer.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix, inverseViewMatrix;

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

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return this.inverseViewMatrix;
    }

    public void setInverseViewMatrix(Matrix4f inverseViewMatrix) {
        this.inverseViewMatrix = inverseViewMatrix;
    }

}
