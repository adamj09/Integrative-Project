package project.Renderer.Model;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * Class used to generate an icosphere. Based on Andreas Kahler's C#
 * implementation
 * (http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html)
 * and modified for use in this project.
 * 
 * @author Adam Johnston
 */
public class SphereGenerator {
    private ArrayList<Vector3f> vertices = new ArrayList<>();
    private ArrayList<Vector3i> indices = new ArrayList<>();

    private int index = 0;
    private HashMap<String, Integer> middlePointCache = new HashMap<>();

    /**
     * Adds a vertex to the list of vertices and returns its index. The position is
     * normalized to ensure that all vertices lie on the surface of the sphere.
     * 
     * @param position The position of the vertex to be added.
     * @return The index of the added vertex.
     */
    private int addVertex(Vector3f position) {
        float length = (float) Math.sqrt(position.x * position.x + position.y * position.y + position.z * position.z);
        vertices.add(new Vector3f(position.x / length, position.y / length, position.z / length));
        return index++;
    }

    /**
     * Calculates the middle point between two vertices and adds it to the list of
     * vertices.
     * 
     * @param index1 The index of the first vertex.
     * @param index2 The index of the second vertex.
     * @return The index of the middle point.
     */
    private int getMiddlePoint(int index1, int index2) {
        boolean isFirstLesser = index1 < index2;

        int lesserIndex = isFirstLesser ? index1 : index2;
        int greaterIndex = isFirstLesser ? index2 : index1;
        String key = (lesserIndex << 32) + greaterIndex + "";

        // If value was already calculated, retrieve from cache and return.
        if (middlePointCache.containsKey(key)) {
            return middlePointCache.get(key);
        }

        Vector3f point1 = vertices.get(index1);
        Vector3f point2 = vertices.get(index2);

        Vector3f middle = new Vector3f((point1.x + point2.x) / 2.0f, (point1.y + point2.y) / 2.0f,
                (point1.z + point2.z) / 2.0f);

        int index = addVertex(middle);
        //middlePointCache.put(key, index);
        return index;
    }

    /**
     * Creates an icosphere mesh with the specified depth. The depth determines how
     * many times the initial icosahedron is subdivided.
     * 
     * @param depth The number of subdivisions of the icosphere.
     * @return The created mesh.
     */
    public Mesh create(int depth) {
        // Reset all values before creating a new sphere.
        vertices.clear();
        indices.clear();
        middlePointCache.clear();

        index = 0;

        float t = (1.0f + (float) Math.sqrt(5.0)) / 2.0f;

        // Manually create the initial icosahedron vertices.
        addVertex(new Vector3f(-1, t, 0));
        addVertex(new Vector3f(1, t, 0));
        addVertex(new Vector3f(-1, -t, 0));
        addVertex(new Vector3f(1, -t, 0));

        addVertex(new Vector3f(0, -1, t));
        addVertex(new Vector3f(0, 1, t));
        addVertex(new Vector3f(0, -1, -t));
        addVertex(new Vector3f(0, 1, -t));

        addVertex(new Vector3f(t, 0, -1));
        addVertex(new Vector3f(t, 0, 1));
        addVertex(new Vector3f(-t, 0, -1));
        addVertex(new Vector3f(-t, 0, 1));

        // Manually create the initial icosahedron indices.
        ArrayList<Vector3i> indices = new ArrayList<>();
        indices.add(new Vector3i(0, 11, 5));
        indices.add(new Vector3i(0, 5, 1));
        indices.add(new Vector3i(0, 1, 7));
        indices.add(new Vector3i(0, 7, 10));
        indices.add(new Vector3i(0, 10, 11));

        indices.add(new Vector3i(1, 5, 9));
        indices.add(new Vector3i(5, 11, 4));
        indices.add(new Vector3i(11, 10, 2));
        indices.add(new Vector3i(10, 7, 6));
        indices.add(new Vector3i(7, 1, 8));

        indices.add(new Vector3i(3, 9, 4));
        indices.add(new Vector3i(3, 4, 2));
        indices.add(new Vector3i(3, 2, 6));
        indices.add(new Vector3i(3, 6, 8));
        indices.add(new Vector3i(3, 8, 9));

        indices.add(new Vector3i(4, 9, 5));
        indices.add(new Vector3i(2, 4, 11));
        indices.add(new Vector3i(6, 2, 10));
        indices.add(new Vector3i(8, 6, 7));
        indices.add(new Vector3i(9, 8, 1));

        // Subdivide the icosahedron to create the icosphere.
        for (int i = 0; i < depth; i++) {
            ArrayList<Vector3i> newIndices = new ArrayList<>();

            // Split each triangle into 4 smaller triangles.
            for (Vector3i triangle : indices) {
                int x = getMiddlePoint(triangle.x, triangle.y);
                int y = getMiddlePoint(triangle.y, triangle.z);
                int z = getMiddlePoint(triangle.z, triangle.x);

                newIndices.add(new Vector3i(triangle.x, x, z));
                newIndices.add(new Vector3i(triangle.y, y, x));
                newIndices.add(new Vector3i(triangle.z, z, y));
                newIndices.add(new Vector3i(x, y, z));
            }
            indices = newIndices;
        }

        for (Vector3i triangle : indices) {
            this.indices.add(triangle);
        }

        // Note that for a sphere, normals are just the sphere's vertices divided by the
        // radius (radius here is 1 so just pass in vertices). Texture coordinates are
        // not calculated. Maybe this should be implemented in the future.
        return new Mesh(this.vertices, this.indices, this.vertices, new ArrayList<>());
    }
}
