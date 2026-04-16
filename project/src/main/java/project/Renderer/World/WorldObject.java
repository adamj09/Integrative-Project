package project.Renderer.World;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import project.Renderer.Model.Mesh;

/**
 * Represents an object and its properties in the world, such as its
 * transformation, mesh, color, and light color.
 * 
 * @author Adam Johnston
 */
public class WorldObject {
    /**
     * The translation and scale of the object.
     */
    private Vector3f translation = new Vector3f(), scale = new Vector3f(1.0f, 1.0f, 1.0f);

    /**
     * The transformation matrix of the object, which is updated whenever the object
     * is translated, rotated, or scaled.
     */
    private Matrix4f transformMatrix = new Matrix4f();

    /**
     * The mesh used to render the object.
     */
    private Mesh mesh;

    /**
     * The name of the object.
     */
    private String name;

    /**
     * The color of the object.
     */
    private Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);

    /**
     * The colour of light emitted by the object, if it acts as a light source.
     */
    private Vector3f lightColor = new Vector3f();

    /**
     * Creates a new WorldObject with the given name and mesh.
     * The colour remains its default, white.
     * 
     * @param name The name of the object.
     * @param mesh The mesh used to render the object.
     */
    public WorldObject(String name, Mesh mesh) {
        this.mesh = mesh;
        this.name = name;
    }

    /**
     * Creates a new WorldObject with the given name, mesh, and color.
     * 
     * @param name  The name of the object.
     * @param mesh  The mesh used to render the object.
     * @param color The color of the object.
     */
    public WorldObject(String name, Mesh mesh, Vector3f color) {
        this.mesh = mesh;
        this.name = name;
        this.color = color;
    }

    /**
     * Translates the object by the given translation vector, and updates the
     * transformation matrix accordingly.
     * Note that this adds the translation vector to the current translation of the
     * object, so multiple calls to this method will accumulate the translation.
     * 
     * @param translation The translation vector to apply to the object.
     */
    public void translate(Vector3f translation) {
        Matrix4f translationMatrix = new Matrix4f();
        translationMatrix.translate(translation);

        this.translation.add(translation);

        transformMatrix.mul(translationMatrix);
    }

    /**
     * Rotates the object by the given angle around the given axis, and updates the
     * transformation matrix accordingly. Note that this applies the rotation on top
     * of the current transformation of the object, so multiple calls to this method
     * will accumulate the rotation.
     * 
     * @param angle The angle to rotate by in radians.
     * @param axis The axis to rotate around.
     */
    public void rotate(float angle, Vector3f axis) {
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.rotate(angle, axis);

        transformMatrix.mul(rotationMatrix);
    }

    /**
     * Scales the object by the given scale vector, and updates the transformation
     * matrix accordingly. Note that this multiplies the current scale of the object
     * by the given scale vector, so multiple calls to this method will accumulate the scale.
     * 
     * @param scale The scale vector to apply to the object.
     */
    public void scale(Vector3f scale) {
        Matrix4f scaleMatrix = new Matrix4f();
        scaleMatrix.scale(scale);

        this.scale.mul(scale);

        transformMatrix.mul(scaleMatrix);
    }

    /**
     * Resets the transformation matrices of the object to their default values.
     */
    public void resetTransforms() {
        translation.zero();
        scale = new Vector3f(1.0f, 1.0f, 1.0f);

        transformMatrix.identity();
    }

    /**
     * @return The transformation matrix of the object.
     */
    public Matrix4f getTransformMatrix() {
        return this.transformMatrix;
    }

    /**
     * @return The scale of the object.
     */
    public Vector3f getScale() {
        return this.scale;
    }

    /**
     * @return The translation of the object.
     */
    public Vector3f getTranslation() {
        return this.translation;
    }

    /**
     * @return The mesh used to render the object.
     */
    public Mesh getMesh() {
        return this.mesh;
    }

    /**
     * Sets the mesh used to render the object.
     * @param mesh The new mesh to use for rendering the object.
     */
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    /**
     * @return the name of the object.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the object.
     * @param name The new name to assign to the object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The color of the object.
     */
    public Vector3f getColor() {
        return this.color;
    }

    /**
     * Sets the color of the object.
     * @param color The new color to assign to the object.
     */
    public void setColor(Vector3f color) {
        this.color = color;
    }

    /**
     * @return The light color emitted by the object.
     */
    public Vector3f getLightColor() {
        return this.lightColor;
    }

    /**
     * Sets the light color emitted by the object.
     * @param lightColor The new light color to assign to the object.
     */
    public void setLightColor(Vector3f lightColor) {
        this.lightColor = lightColor;
    }
}
