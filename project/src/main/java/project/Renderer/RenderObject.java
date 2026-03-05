package project.Renderer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import project.Renderer.Model.Mesh;

public class RenderObject {
    public TransformComponent transform = new TransformComponent();
    private Mesh mesh;

    public RenderObject(Mesh mesh, TransformComponent transform) {
        this.mesh = mesh;
        this.transform = transform;
    }

    private class TransformComponent {
        Vector3f translation, scale;
        Quaternionf rotation;
        Matrix4f transformMatrix;

        public TransformComponent() {
            transformMatrix.translate(translation);
            transformMatrix.scale(scale);
            transformMatrix.rotate(rotation);
        }
    }
}
