package project.Renderer.World;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import project.Renderer.Model.Mesh;

public class WorldObject {
    private Vector3f translation = new Vector3f(), scale = new Vector3f(1.0f, 1.0f, 1.0f);
    private Matrix4f transformMatrix = new Matrix4f();
    private Mesh mesh;
    private String name;
    private Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);
    private Vector3f lightColor = new Vector3f();

    public WorldObject(String name, Mesh mesh) {
        this.mesh = mesh;
        this.name = name;
    }

    public WorldObject(String name, Mesh mesh, Vector3f color) {
        this.mesh = mesh;
        this.name = name;
        this.color = color;
    }

    public void translate(Vector3f translation) {
        Matrix4f translationMatrix = new Matrix4f();
        translationMatrix.translate(translation);

        this.translation.add(translation);

        transformMatrix.mul(translationMatrix);
    }

    public void rotate(float angle, Vector3f axis) {
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate(angle, axis);

        transformMatrix.mul(rotationMatrix);
    }

    public void scale(Vector3f scale) {
        Matrix4f scaleMatrix = new Matrix4f();
        scaleMatrix.scale(scale);

        this.scale.add(scale);

        transformMatrix.mul(scaleMatrix);
    }

    public void resetTransforms() {
        translation.zero();
        scale = new Vector3f(1.0f, 1.0f, 1.0f);

        transformMatrix.identity();
    }

    public Matrix4f getTransformMatrix() {
        return this.transformMatrix;
    }

    public Vector3f getScale() {
        return this.scale;
    }

    public Vector3f getTranslation() {
        return this.translation;
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getLightColor() {
        return this.lightColor;
    }

    public void setLightColor(Vector3f lightColor) {
        this.lightColor = lightColor;
    }
}
