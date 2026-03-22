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
        
        updateTransform();
    }

    public WorldObject(String name, Mesh mesh, Vector3f color) {
        this.mesh = mesh;
        this.name = name;
        this.color = color;

        updateTransform();
    }

    public void updateTransform() {
        transformMatrix.setTranslation(translation);
        transformMatrix.scale(scale);
    }

    public Vector3f getTranslation() {
        return this.translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
        transformMatrix.setTranslation(this.translation);
    }

    public Vector3f getScale() {
        return this.scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
        transformMatrix.scale(this.scale);
    }

    public Matrix4f getTransformMatrix() {
        return this.transformMatrix;
    }

    public void setTransformMatrix(Matrix4f transformMatrix) {
        this.transformMatrix = transformMatrix;
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
