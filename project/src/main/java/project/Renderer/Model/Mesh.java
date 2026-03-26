package project.Renderer.Model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL41.*;

public class Mesh {
    private ArrayList<Vector3f> vertices = new ArrayList<>();
    private ArrayList<Vector3i> indices = new ArrayList<>();
    private ArrayList<Vector3f> normals = new ArrayList<>();
    private ArrayList<Vector2f> texCoords = new ArrayList<>();

    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer texCoordBuffer;

    private int VAO, VBO, NBO, TBO, EBO;

    public Mesh(ArrayList<Vector3f> vertices, ArrayList<Vector3i> indices, ArrayList<Vector3f> normals, ArrayList<Vector2f> texCoords) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        if(!vertices.isEmpty()) {
            setUpVertexBuffer();
        }

        if(!indices.isEmpty()) {
            setUpIndexBuffer();
        }

        if(!normals.isEmpty()) {
            setUpNormalBuffer();
        }

        if(!texCoords.isEmpty()) {
            setUpTexCoordBuffer();
        }
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

    private void setUpTexCoordBuffer() {
        packTexCoordsIntoBuffer();

        TBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, TBO);
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);
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

    // TODO: should probably make a more elegant, all-in-on function for packing data into a buffer
    private void packTexCoordsIntoBuffer() {
        texCoordBuffer = BufferUtils.createFloatBuffer(this.texCoords.size() * 2);

        float[] texCoords = new float[this.texCoords.size() * 3];

        for (int i = 0, j = 0; i < this.texCoords.size(); i++, j += 2) {
            texCoords[j] = this.texCoords.get(i).x;
            texCoords[j + 1] = this.texCoords.get(i).y;
        }

        texCoordBuffer.put(texCoords).flip();
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

    public ArrayList<Vector2f> getTexCoords() {
        return texCoords;
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
