package project.Renderer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderObject {
    public TransformComponent transform = new TransformComponent();

    public RenderObject() {

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
