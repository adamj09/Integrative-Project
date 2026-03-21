package project.Renderer.Model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL41.*;

public class Mesh {
    private ArrayList<Vector3f> vertices = new ArrayList<>();
    private ArrayList<Vector3i> indices = new ArrayList<>();

    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;

    private int VAO;
    private int VBO;
    private int EBO;

    public Mesh(ArrayList<Vector3f> vertices, ArrayList<Vector3i> indices) {
        this.vertices = vertices;
        this.indices = indices;

        setUpVertexBuffer();
        setUpIndexBuffer();
    }

    private void setUpVertexBuffer() {
        packVerticesIntoBuffer();

        VAO = glGenVertexArrays();
        VBO = glGenBuffers();

        glBindVertexArray(VAO);

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    private void setUpIndexBuffer () {
        packIndicesIntoBuffer();

        EBO = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    }

    private void packVerticesIntoBuffer() {
        vertexBuffer = BufferUtils.createFloatBuffer(this.vertices.size() * 3);

        float[] vertices = new float[this.vertices.size() * 3];

        for (int i = 0, j = 0; i < this.vertices.size(); i++, j += 3) {
            vertices[j] = this.vertices.get(i).x;
            vertices[j + 1] = this.vertices.get(i).y;
            vertices[j + 2] = this.vertices.get(i).z;
        }

        vertexBuffer.put(vertices).flip();
    }

    private void packIndicesIntoBuffer() {
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

    public int getVAO() {
        return this.VAO;
    }

    public int getVBO() {
        return this.VBO;
    }

    public int getEBO() {
        return this.EBO;
    }
}
