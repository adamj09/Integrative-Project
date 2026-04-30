package oms.Renderer.Mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL41.*;

/**
 * Class representing a 3D mesh, containing vertex data and OpenGL buffer
 * objects.
 * 
 * @author Adam Johnston
 */
public class Mesh {
    /**
     * Vertex data lists for this mesh.
     */
    private ArrayList<Vector3f> vertices = new ArrayList<>();
    private ArrayList<Vector3i> indices = new ArrayList<>();
    private ArrayList<Vector3f> normals = new ArrayList<>();
    private ArrayList<Vector2f> texCoords = new ArrayList<>();

    /**
     * Buffers for vertex data to be sent to the GPU.
     */
    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer texCoordBuffer;

    /**
     * OpenGL buffer object IDs for this mesh.
     */
    private int VAO, VBO, NBO, TBO, EBO;

    /**
     * Constructs a Mesh object with the provided vertex data, and sets up OpenGL buffers for rendering.
     * @param vertices The list of vertex positions for this mesh.
     * @param indices The list of vertex indices for this mesh.
     * @param normals The list of vertex normals for this mesh.
     * @param texCoords The list of texture coordinates for this mesh.
     */
    public Mesh(ArrayList<Vector3f> vertices, ArrayList<Vector3i> indices, ArrayList<Vector3f> normals,
            ArrayList<Vector2f> texCoords) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.texCoords = texCoords;
    }

    /**
     * Sets up the OpenGL buffers for this mesh.
     */
    public void setUpBuffers() {
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        // Set up buffers for all provided vertex data.
        if (!vertices.isEmpty()) {
            setUpVertexBuffer();
        }

        if (!indices.isEmpty()) {
            setUpIndexBuffer();
        }

        if (!normals.isEmpty()) {
            setUpNormalBuffer();
        }

        if (!texCoords.isEmpty()) {
            setUpTexCoordBuffer();
        }
    }

    /**
     * Sets up the vertex buffer object (VBO) for this mesh, and packs vertex data
     * into a FloatBuffer to be sent to the GPU.
     */
    private void setUpVertexBuffer() {
        vertexBuffer = packVector3fIntoBuffer(vertices);

        VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
    }

    /**
     * Sets up the element buffer object (EBO) for this mesh, and packs index data
     * into an IntBuffer to be sent to the GPU.
     */
    private void setUpIndexBuffer() {
        indexBuffer = packVector3iIntoBuffer(indices);

        EBO = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    }

    /**
     * Sets up the normal buffer object (NBO) for this mesh, and packs normal data
     * into a FloatBuffer to be sent to the GPU.
     */
    private void setUpNormalBuffer() {
        normalBuffer = packVector3fIntoBuffer(normals);

        NBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, NBO);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(1);
    }

    /**
     * Sets up the texture coordinate buffer object (TBO) for this mesh, and packs
     * texture coordinate data into a FloatBuffer to be sent to the GPU.
     */
    private void setUpTexCoordBuffer() {
        texCoordBuffer = packVector2fIntoBuffer(texCoords);

        TBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, TBO);
        glBufferData(GL_ARRAY_BUFFER, texCoordBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(2);
    }

    /**
     * Packs an ArrayList of Vector3f data into a FloatBuffer for sending to the GPU.
     * @param data The ArrayList of Vector3f data to be packed.
     * 
     * @return The FloatBuffer containing the packed data.
     */
    private FloatBuffer packVector3fIntoBuffer(ArrayList<Vector3f> data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.size() * 3);

        float[] array = new float[data.size() * 3];

        for (int i = 0, j = 0; i < data.size(); i++, j += 3) {
            array[j] = data.get(i).x;
            array[j + 1] = data.get(i).y;
            array[j + 2] = data.get(i).z;
        }

        return buffer.put(array).flip();
    }

    /**
     * Packs an ArrayList of Vector2f data into a FloatBuffer for sending to the GPU.
     * @param data The ArrayList of Vector2f data to be packed.
     * 
     * @return The FloatBuffer containing the packed data.
     */
    private FloatBuffer packVector2fIntoBuffer(ArrayList<Vector2f> data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.size() * 2);

        float[] array = new float[data.size() * 2];

        for (int i = 0, j = 0; i < data.size(); i++, j += 2) {
            array[j] = data.get(i).x;
            array[j + 1] = data.get(i).y;
        }

        return buffer.put(array).flip();
    }

    /**
     * Packs an ArrayList of Vector3i data into an IntBuffer for sending to the GPU.
     * @param data The ArrayList of Vector3i data to be packed.
     * 
     * @return The IntBuffer containing the packed data.
     */
    private IntBuffer packVector3iIntoBuffer(ArrayList<Vector3i> data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.size() * 3);

        int[] array = new int[data.size() * 3];

        for (int i = 0, j = 0; i < data.size(); i++, j += 3) {
            array[j] = data.get(i).x;
            array[j + 1] = data.get(i).y;
            array[j + 2] = data.get(i).z;
        }

        return buffer.put(array).flip();
    }

    /**
     * @return The FloatBuffer containing the vertex data for this mesh.
     */
    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    /**
     * @return The FloatBuffer containing the normal data for this mesh.
     */
    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    /**
     * @return The FloatBuffer containing the normal data for this mesh.
     */
    public ArrayList<Vector3f> getVertices() {
        return vertices;
    }

    /**
     * @return The ArrayList containing the vertex indices for this mesh.
     */
    public ArrayList<Vector3i> getIndices() {
        return indices;
    }

    /**
     * @return The ArrayList containing the vertex normals for this mesh.
     */
    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    /**
     * @return The ArrayList containing the texture coordinates for this mesh.
     */
    public ArrayList<Vector2f> getTexCoords() {
        return texCoords;
    }

    /**
     * @return The VAO ID.
     */
    public int getVAO() {
        return this.VAO;
    }

    /**
     * @return The VBO ID.
     */
    public int getVBO() {
        return this.VBO;
    }

    /**
     * @return The EBO ID.
     */
    public int getEBO() {
        return this.EBO;
    }

    /**
     * Disposes of all OpenGL objects associated with this Mesh.
     */
    public void dispose() {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
        glDeleteBuffers(NBO);
        glDeleteBuffers(TBO);
    }
}
