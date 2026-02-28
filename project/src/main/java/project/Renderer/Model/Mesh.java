package project.Renderer.Model;

public class Mesh {
    private static final float GOLDEN_RATIO = 1.618034f;

    private float[] vertices;
    private int[] indices;

    public Mesh() {

    }

    public static Mesh uvsphere() {

        return new Mesh();
    }

    public static Mesh icosphere(float radius) {
        float a = (float)Math.sqrt(Math.pow(radius, 2) / (1 + Math.pow(GOLDEN_RATIO, 2)));
        float b = a * GOLDEN_RATIO;

        float[] vertices = new float[36];

        // XZ Plane (Y = 0)
        // Vertex 0
        vertices[0] = -b; // x
        vertices[1] = 0; // y
        vertices[2] = a; // z
        // Vextex 1
        vertices[3] = b;
        vertices[4] = 0;
        vertices[5] = a;
        // Vertex 2
        vertices[6] = b;
        vertices[7] = 0;
        vertices[8] = -a;
        // Vertex 3
        vertices[9] = -b;
        vertices[10] = 0;
        vertices[11] = -a;

        // XY Plane (Z = 0)
        // Vertex 0
        vertices[12] = -b; // x
        vertices[13] = a; // y
        vertices[14] = 0; // z
        // Vextex 1
        vertices[15] = b;
        vertices[16] = a;
        vertices[17] = 0;
        // Vertex 2
        vertices[18] = b;
        vertices[19] = -a;
        vertices[20] = 0;
        // Vertex 3
        vertices[21] = -b;
        vertices[22] = -a;
        vertices[23] = 0;

        // YZ Plane (X = 0)
        // Vertex 0
        vertices[24] = 0; // x
        vertices[25] = a; // y
        vertices[26] = -b; // z
        // Vextex 1
        vertices[27] = 0;
        vertices[28] = a;
        vertices[29] = b;
        // Vertex 2
        vertices[30] = 0;
        vertices[31] = -a;
        vertices[32] = b;
        // Vertex 3
        vertices[33] = 0;
        vertices[34] = -a;
        vertices[35] = -b;

        return new Mesh();
    }
}
