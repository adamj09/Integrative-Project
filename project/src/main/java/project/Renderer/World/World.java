package project.Renderer.World;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;
import project.Renderer.Model.SphereGenerator;

public class World {
    private WorldObject lightSource;
    private HashMap<String, WorldObject> bodies = new HashMap<>();
    private Camera camera = new Camera();

    private FloatBuffer colorsBuffer;
    private FloatBuffer matricesBuffer;

    private Mesh bodyMesh;

    public World() {
        loadWorld();

        colorsBuffer = BufferUtils.createFloatBuffer(3 * bodies.size());
        matricesBuffer = BufferUtils.createFloatBuffer(16 * bodies.size());

        updateColorBuffer();
        updateMatrixBuffer();
    }

    private void loadWorld() {
        Mesh sphere = new SphereGenerator().create(4);

        bodyMesh = new Mesh(sphere.getVertices(), sphere.getIndices(), sphere.getNormals());

        lightSource = new WorldObject("light", new Mesh(sphere.getVertices(), sphere.getIndices(), sphere.getNormals()));
        lightSource.setTranslation(new Vector3f(990.f, 0.f, 0.f));
        lightSource.setScale(new Vector3f(10.f, 10.f, 10.f));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));

        WorldObject body = new WorldObject("body", bodyMesh, new Vector3f(1.0f, 1.0f, 1.0f));

        WorldObject satellite = new WorldObject("test", bodyMesh, new Vector3f(1.0f, 0.0f, 0.0f));
        satellite.setTranslation(new Vector3f(-10.f, 0.f, 0.f));
        satellite.setScale(new Vector3f(0.5f, 0.5f, 0.5f));

        bodies.put(body.getName(), body);
        bodies.put(satellite.getName(), satellite);
    }

    public void updateColorBuffer() {
        colorsBuffer.clear();

        float[] colors = new float[colorsBuffer.capacity()];

        int i = 0;
        for (Map.Entry<String, WorldObject> set : bodies.entrySet()) {
            Vector3f color = set.getValue().getColor();
            colors[i] = color.x;
            colors[i + 1] = color.y;
            colors[i + 2] = color.z;
            i += 3;
        }

        colorsBuffer.put(colors).flip();
    }

    public void updateMatrixBuffer() {
        matricesBuffer.clear();

        float[] matrices = new float[matricesBuffer.capacity()];

        int i = 0;
        for(Map.Entry<String, WorldObject> set : bodies.entrySet()) {
            set.getValue().getTransformMatrix().get(matrices, 16 * i);
            i++;
        }

        matricesBuffer.put(matrices).flip();
    }

    public void addBody(WorldObject body) {
        if(bodies.containsKey(body.getName())) {
            // TODO: handle this case by notifying user a body with this name already exists
            return;
        }

        bodies.put(body.getName(), body);

        updateColorBuffer();
        updateMatrixBuffer();
    }

    public void removeBody(String name) {
        bodies.remove(name);

        updateColorBuffer();
        updateMatrixBuffer();
    }

    public HashMap<String, WorldObject> getBodies() {
        return this.bodies;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public FloatBuffer getColorsBuffer() {
        return this.colorsBuffer;
    }

    public FloatBuffer getMatricesBuffer() {
        return this.matricesBuffer;
    }

    public Mesh getBodyMesh() {
        return this.bodyMesh;
    }

    public WorldObject getLightSource() {
        return this.lightSource;
    }
}
