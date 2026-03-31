package project.Renderer.Model;

import java.util.ArrayList;

import org.joml.Vector3f;

public class RingGenerator {
    private ArrayList<Vector3f> vertices = new ArrayList<>();

    public Mesh create(double step) {
        int pointCount = (int)Math.ceil(360 / step);
        double radianStep = Math.toRadians(step);

        for(int i = 0; i < pointCount; i++) {
            vertices.add(new Vector3f((float)Math.sin(i * radianStep), 0.0f, (float)Math.cos(i * radianStep)));
        }

        return new Mesh(vertices, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
