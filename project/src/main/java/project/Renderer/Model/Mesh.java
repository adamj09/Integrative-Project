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

    public Mesh(ArrayList<Vector3f> vertices, ArrayList<Vector3i> indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    public static Mesh icosphere(float radius, int depth) {
        float a = (float) Math.sqrt(Math.pow(radius, 2) / (1 + Math.pow(GOLDEN_RATIO, 2)));
        float b = a * GOLDEN_RATIO;
        // Note: for radius = 1, a = 0.525731112119134; b = 0.85065080835157

        // Vertices
        ArrayList<Vector3f> vertices = new ArrayList<>();
        vertices.add(new Vector3f(b, a, 0)); // 0
        vertices.add(new Vector3f(0, b, -a)); // 1
        vertices.add(new Vector3f(0, b, a)); // 2
        vertices.add(new Vector3f(a, 0, -b)); // 3
        vertices.add(new Vector3f(a, 0, b)); // 4
        vertices.add(new Vector3f(b, -a, 0)); // 5
        vertices.add(new Vector3f(-a, 0, -b)); // 6
        vertices.add(new Vector3f(-b, a, 0)); // 7
        vertices.add(new Vector3f(-a, 0, b)); // 8
        vertices.add(new Vector3f(0, -b, -a)); // 9
        vertices.add(new Vector3f(0, -b, a)); // 10
        vertices.add(new Vector3f(-b, -a, 0)); // 11

        // Indices (or faces)
        ArrayList<Vector3i> indices = new ArrayList<>();
        indices.add(new Vector3i(0, 1, 2)); // 0
        indices.add(new Vector3i(0, 3, 1)); // 1
        indices.add(new Vector3i(0, 2, 4)); // 2
        indices.add(new Vector3i(3, 0, 5)); // 3
        indices.add(new Vector3i(0, 4, 5)); // 4
        indices.add(new Vector3i(1, 3, 6)); // 5
        indices.add(new Vector3i(1, 7, 2)); // 6
        indices.add(new Vector3i(7, 1, 6)); // 7
        indices.add(new Vector3i(4, 2, 8)); // 8
        indices.add(new Vector3i(7, 8, 2)); // 9
        indices.add(new Vector3i(9, 3, 5)); // 10
        indices.add(new Vector3i(6, 3, 9)); // 11
        indices.add(new Vector3i(5, 4, 10)); // 12
        indices.add(new Vector3i(4, 8, 10)); // 13
        indices.add(new Vector3i(9, 5, 10)); // 14
        indices.add(new Vector3i(7, 6, 11)); // 15
        indices.add(new Vector3i(7, 11, 8)); // 16
        indices.add(new Vector3i(11, 6, 9)); // 17
        indices.add(new Vector3i(8, 11, 10)); // 18
        indices.add(new Vector3i(10, 11, 9)); // 19

        for(int i = 0; i < depth; i++) {
            ArrayList<Vector3i> newIndices = new ArrayList<>();
        
        }


        return new Mesh(vertices, indices);
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
