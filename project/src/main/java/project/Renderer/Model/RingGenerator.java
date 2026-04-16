package project.Renderer.Model;

import java.util.ArrayList;

import org.joml.Vector3f;

/**
 * Class that generates a ring of points in the XZ plane around the origin, at
 * unit distance from the center.
 * 
 * @author Adam Johnston
 */
public class RingGenerator {
    /**
     * Points that make up the ring, stored as a Vector3f for compatibility with the
     * Mesh class.
     */
    private ArrayList<Vector3f> points = new ArrayList<>();

    /**
     * Generates a ring of points with the specified step size in degrees.
     * @param step the angular distance in degrees between each point on the ring.
     * @return A Mesh object containing the generated points as vertices, with empty lists for indices, normals, and texture coordinates.
     */
    public Mesh create(double step) {
        int pointCount = (int) Math.ceil(360 / step);
        double radianStep = Math.toRadians(step);

        for (int i = 0; i < pointCount; i++) {
            points.add(new Vector3f((float) Math.sin(i * radianStep), 0.0f, (float) Math.cos(i * radianStep)));
        }

        return new Mesh(points, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
