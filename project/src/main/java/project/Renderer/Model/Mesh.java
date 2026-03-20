package project.Renderer.Model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

public class Mesh {
    private ArrayList<Vector3f> vertices = new ArrayList<>();
    private ArrayList<Vector3i> indices = new ArrayList<>();

    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;

    //TODO: add per-mesh VAO

    public Mesh(ArrayList<Vector3f> vertices, ArrayList<Vector3i> indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public void packVerticesIntoBuffer() {
        vertexBuffer = BufferUtils.createFloatBuffer(this.vertices.size() * 3);

        float[] vertices = new float[this.vertices.size() * 3];

        for (int i = 0, j = 0; i < this.vertices.size(); i++, j += 3) {
            vertices[j] = this.vertices.get(i).x;
            vertices[j + 1] = this.vertices.get(i).y;
            vertices[j + 2] = this.vertices.get(i).z;
        }

        vertexBuffer.put(vertices).flip();
    }

    public void packIndicesIntoBuffer() {
        indexBuffer = BufferUtils.createIntBuffer(this.indices.size() * 3);

        int[] indices = new int[this.indices.size() * 3];

        for (int i = 0, j = 0; i < this.indices.size(); i++, j += 3) {
            indices[j] = this.indices.get(i).x;
            indices[j + 1] = this.indices.get(i).y;
            indices[j + 2] = this.indices.get(i).z;
        }

        indexBuffer.put(indices).flip();
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public ArrayList<Vector3f> getVertices() {
        return vertices;
    }

    public ArrayList<Vector3i> getIndices() {
        return indices;
    }
}
