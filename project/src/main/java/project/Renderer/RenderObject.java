package project.Renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderObject {
    public TransformComponent transform;

    public RenderObject() {

    }



    private class TransformComponent {
        Vector3f translation, scale, rotation;
        Matrix4f matrix, normalMatrix;

        public TransformComponent() {
            matrix.translate(translation);
            matrix.scale(scale);

 
        }
    }
}
