package project.Renderer.World;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Math.Body;
import project.Math.Satellite;
import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;
import project.Renderer.Model.RingGenerator;
import project.Renderer.Model.SphereGenerator;

//TODO: implement updating data based on simulation
// Notes:
// - One unit of length is equal to the radius of the central body (take data from math and divide by radius of planet to get the right coordinates)
// - The central celestial body will always be at (0, 0)
// 
public class World {
    private String name;

    private WorldObject lightSource;
    private HashMap<String, WorldObject> bodies = new HashMap<>();
    private HashMap<String, WorldObject> orbits = new HashMap<>();

    private Camera camera = new Camera();

    private FloatBuffer colorsBuffer;
    private FloatBuffer bodyMatrixBuffer;
    private FloatBuffer orbitMatrixBuffer;

    private float unitScale = 1000.f; // 1 AU is equal to this many renderer units.
    private double AU = 1.496e+8d; // 1 AU in kilometers.

    private Mesh bodyMesh;
    private Mesh orbitMesh;

    public World(String name) {
        this.name = name;

        loadLightSource();
        loadCentralBody(null);
        loadOrbits();

        colorsBuffer = BufferUtils.createFloatBuffer(3 * bodies.size());
        bodyMatrixBuffer = BufferUtils.createFloatBuffer(16 * bodies.size());
        orbitMatrixBuffer = BufferUtils.createFloatBuffer(16 * orbits.size());

        camera.setView(new Vector3f(10.f, 10.f, 10.f), camera.getDirection());

        updateBodyMatrixBuffer();
        updateColorBuffer();
        updateOrbitMatrixBuffer();
    }

    private void loadLightSource() {
        lightSource = new WorldObject("light", new SphereGenerator().create(4));
        lightSource.setTranslation(new Vector3f(1000.f, 0.f, 0.f));

        float lightSourceScale = (float) (696_340d / AU * unitScale); // Sun's radius in AUs times scale

        lightSource.setScale(new Vector3f(lightSourceScale, lightSourceScale, lightSourceScale));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));
    }

    private void loadCentralBody(Body body) {
        bodyMesh = new SphereGenerator().create(4);
        WorldObject bodyObject = new WorldObject(name, bodyMesh, new Vector3f(1.0f, 1.0f, 1.0f));

        float planetScale = (float) (body.getRadius() / AU * unitScale);
        bodyObject.setScale(new Vector3f(planetScale, planetScale, planetScale));

        // Add objects to world.
        bodies.put(bodyObject.getName(), bodyObject);
    }

    private void loadSatellites(Body body) {
        HashMap<String, Satellite> satellites = body.getSatellites();

        float satelliteRadius = 160.f / unitScale;

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();

            // If the satellite does not already have a WorldObject representation, add it.
            if (!bodies.containsKey(item.getKey())) {
                WorldObject newObject = new WorldObject(satellite.getData().name, bodyMesh);
                newObject.setScale(new Vector3f(satelliteRadius, satelliteRadius, satelliteRadius));

                bodies.put(item.getKey(), new WorldObject(item.getKey(), bodyMesh));
            }
        }
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

    public void updateWorld(Body body) {
        // TODO: focus camera onto newly created satellites
        // TODO: create algorithm to find ideal camera position

        updateBodyMatrixBuffer();
        updateColorBuffer();
        updateOrbitMatrixBuffer();
    }

    public void addBody(WorldObject body) {
        if (bodies.containsKey(body.getName())) {
            // TODO: handle case of name already taken
        }

        bodies.put(body.getName(), body);
        // TODO: also add associated orbit when adding body

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
