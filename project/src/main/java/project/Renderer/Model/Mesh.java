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
    private ArrayList<Vector3f> normals = new ArrayList<>();

    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;
    private FloatBuffer normalBuffer;

    private int VAO;
    private int VBO;
    private int NBO;
    private int EBO;

    public Mesh(ArrayList<Vector3f> vertices, ArrayList<Vector3i> indices, ArrayList<Vector3f> normals) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        setUpVertexBuffer();
        setUpIndexBuffer();
        setUpNormalBuffer();
    }

    private void setUpVertexBuffer() {
        packVerticesIntoBuffer();

        VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    private void setUpIndexBuffer() {
        packIndicesIntoBuffer();

        EBO = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    }

    private void setUpNormalBuffer() {
        packNormalsIntoBuffer();

        NBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, NBO);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
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

    private void packNormalsIntoBuffer() {
        normalBuffer = BufferUtils.createFloatBuffer(this.normals.size() * 3);

        float[] normals = new float[this.normals.size() * 3];

        for (int i = 0, j = 0; i < this.normals.size(); i++, j += 3) {
            normals[j] = this.normals.get(i).x;
            normals[j + 1] = this.normals.get(i).y;
            normals[j + 2] = this.normals.get(i).z;
        }

        normalBuffer.put(normals).flip();
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

    public ArrayList<Vector3f> getNormals() {
        return normals;
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
