package project.Renderer.World;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;
import project.Renderer.Model.RingGenerator;
import project.Renderer.Model.SphereGenerator;

public class World {
    private String name;

    private WorldObject lightSource;
    private HashMap<String, WorldObject> bodies = new HashMap<>();
    private HashMap<String, WorldObject> orbits = new HashMap<>();

    private Camera camera = new Camera();

    private FloatBuffer colorsBuffer;
    private FloatBuffer bodyMatrixBuffer;
    private FloatBuffer orbitMatrixBuffer;

    private Mesh bodyMesh;
    private Mesh orbitMesh;

    public World(String name) {
        this.name = name;

        loadLightSource();
        loadBodies();
        loadOrbits();

        colorsBuffer = BufferUtils.createFloatBuffer(3 * bodies.size());
        bodyMatrixBuffer = BufferUtils.createFloatBuffer(16 * bodies.size());
        orbitMatrixBuffer = BufferUtils.createFloatBuffer(16 * orbits.size());

        updateBodyMatrixBuffer();
        updateColorBuffer();
        updateOrbitMatrixBuffer();
    }

    private void loadLightSource() {
        lightSource = new WorldObject("light", new SphereGenerator().create(4));
        lightSource.setTranslation(new Vector3f(990.f, 0.f, 0.f));
        lightSource.setScale(new Vector3f(10.f, 10.f, 10.f));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));
    }

    private void loadBodies() {
        camera.setView(new Vector3f(-1.f, 0.f, 0.f), camera.getDirection());
        bodyMesh = new SphereGenerator().create(4);

        // --- Central Celestial Body ---
        WorldObject body = new WorldObject(name, bodyMesh, new Vector3f(1.0f, 1.0f, 1.0f));

        // --- Satellites ---
        WorldObject satellite = new WorldObject("test", bodyMesh, new Vector3f(1.0f, 0.0f, 0.0f));
        satellite.setTranslation(new Vector3f(-10.f, 0.f, 0.f));
        satellite.setScale(new Vector3f(0.5f, 0.5f, 0.5f));

        // Add objects to world.
        bodies.put(body.getName(), body);
        bodies.put(satellite.getName(), satellite);
    }

    private void loadOrbits() {
        orbitMesh = new RingGenerator().create(2);

        for (Map.Entry<String, WorldObject> body : bodies.entrySet()) {
            WorldObject orbit = new WorldObject(body.getKey(), orbitMesh);
            // Test transforms
            orbit.setScale(new Vector3f(15.f, 1.f, 10.f));
            orbit.setTranslation(new Vector3f(5.f, 0.f, 0.f));

            // TODO: transform orbit here according to the info associated with the given
            // celestial body.

            orbits.put(orbit.getName(), orbit);
        }
    }

    public void updateBodyMatrixBuffer() {
        bodyMatrixBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : bodies.entrySet()) {
            item.getValue().getTransformMatrix().get(i * 16, bodyMatrixBuffer);
            i++;
        }
    }

    public void updateColorBuffer() {
        colorsBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : bodies.entrySet()) {
            item.getValue().getColor().get(i * 3, colorsBuffer);
            i++;
        }
    }

    public void updateOrbitMatrixBuffer() {
        orbitMatrixBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : orbits.entrySet()) {
            item.getValue().getTransformMatrix().get(i * 16, orbitMatrixBuffer);
            i++;
        }
    }

    public void addBody(WorldObject body) {
        if (bodies.containsKey(body.getName())) {
            // TODO: handle this case by notifying user a body with this name already exists
            return;
        }

        bodies.put(body.getName(), body);
        //TODO: also add associated orbit when adding body

        updateBodyMatrixBuffer();
        updateColorBuffer();
        updateOrbitMatrixBuffer();
    }

    public void removeBody(String name) {
        if (name != this.name) {
            bodies.remove(name);
            // body.removeSatellite(name);
        }

        updateBodyMatrixBuffer();
        updateColorBuffer();
        updateOrbitMatrixBuffer();
    }

    public HashMap<String, WorldObject> getBodies() {
        return this.bodies;
    }

    public HashMap<String, WorldObject> getOrbits() {
        return this.orbits;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public FloatBuffer getColorsBuffer() {
        return this.colorsBuffer;
    }

    public FloatBuffer getBodyMatrixBuffer() {
        return this.bodyMatrixBuffer;
    }

    public FloatBuffer getOrbitMatrixBuffer() {
        return this.orbitMatrixBuffer;
    }

    public Mesh getBodyMesh() {
        return this.bodyMesh;
    }

    public Mesh getOrbitMesh() {
        return this.orbitMesh;
    }

    public WorldObject getLightSource() {
        return this.lightSource;
    }
}
