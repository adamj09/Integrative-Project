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

public class World {
    private String name;

    private Body body;

    private WorldObject lightSource;
    private HashMap<String, WorldObject> bodies = new HashMap<>();
    private HashMap<String, WorldObject> orbits = new HashMap<>();

    private Camera camera = new Camera();

    private FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(0);
    private FloatBuffer bodyMatrixBuffer = BufferUtils.createFloatBuffer(0);
    private FloatBuffer orbitMatrixBuffer = BufferUtils.createFloatBuffer(0);

    private final float UNIT_SCALE = 10_000.f; // 1 AU is equal to this many renderer units.
    private double AU = 1.496e+8d; // 1 AU in kilometers.

    private Mesh bodyMesh;
    private Mesh orbitMesh;

    public World(String name) {
        this.name = name;

        camera.setView(new Vector3f(10.f, 10.f, 10.f), camera.getDirection());
    }

    /**
     * Update the given buffer with the transform matrices of the given world
     * objects.
     * 
     * @param buffer  The buffer to update. Will be re-created if the capacity is
     *                not sufficient to hold the new data.
     * @param objects The world objects to get the data from.
     * @param step    The number of floats to write for each world object (e.g., 16
     *                for a 4x4 transform matrix, 3 for a color vector, etc.)
     */
    public void updateMatrixBuffer(FloatBuffer buffer, HashMap<String, WorldObject> objects) {
        if (buffer.capacity() != objects.size() * 16) {
            buffer = BufferUtils.createFloatBuffer(16 * objects.size());
        }

        buffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : objects.entrySet()) {
            item.getValue().getTransformMatrix().get(i * 16, buffer);
            i++;
        }
    }

    /**
     * Update the given buffer with the color vectors of the given world objects.
     * 
     * @param buffer  The buffer to update. Will be re-created if the capacity is
     *                not sufficient to hold the new data.
     * @param objects The world objects to get the data from.
     * @param step    The number of floats to write for each world object (e.g., 16
     *                for a 4x4 transform matrix, 3 for a color vector, etc.)
     */
    public void updateColorBuffer(FloatBuffer buffer, HashMap<String, WorldObject> objects) {
        if (buffer.capacity() != objects.size() * 3) {
            buffer = BufferUtils.createFloatBuffer(3 * objects.size());
        }

        buffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : objects.entrySet()) {
            item.getValue().getColor().get(i * 3, buffer);
            i++;
        }
    }

    public void updateMatrices() {
        // TODO: focus camera onto newly created satellites
        // TODO: create algorithm to find ideal camera position

        updateMatrixBuffer(bodyMatrixBuffer, bodies);
        updateMatrixBuffer(orbitMatrixBuffer, orbits);
    }

    public void updateColors() {
        updateColorBuffer(colorsBuffer, bodies);
    }

    public void setBody(Body body) {
        this.body = body;

        loadLightSource();
        loadCentralBody();
        loadSatellites();
        loadOrbits();

        updateMatrices();
        updateColors();
    }

    private void loadLightSource() {
        float lightSourceScale = (float) (696_340d / AU * UNIT_SCALE); // Sun's radius in AUs times scale

        lightSource = new WorldObject("light", new SphereGenerator().create(4));

        lightSource.setTranslation(new Vector3f((float) (body.getDistanceToSun() / AU * UNIT_SCALE), 0.f, 0.f));
        lightSource.setScale(new Vector3f(lightSourceScale, lightSourceScale, lightSourceScale));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));
    }

    private void loadCentralBody() {
        bodyMesh = new SphereGenerator().create(4);
        WorldObject bodyObject = new WorldObject(name, bodyMesh, new Vector3f(1.0f, 1.0f, 1.0f));

        float planetScale = (float) (body.getRadius() / AU * UNIT_SCALE);
        // float planetScale = (float) (body.getRadius() / AU * unitScale);
        bodyObject.setScale(new Vector3f(planetScale, planetScale, planetScale));

        // Add objects to world.
        bodies.put(bodyObject.getName(), bodyObject);
    }

    private void loadSatellites() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        float satelliteRadius = (float) (160.d / AU * UNIT_SCALE);

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();

            // If the satellite does not already have a WorldObject representation, add it.
            if (!bodies.containsKey(item.getKey())) {
                WorldObject newObject = new WorldObject(satellite.getData().name, bodyMesh);

                newObject.setScale(new Vector3f(satelliteRadius, satelliteRadius, satelliteRadius));
                newObject.setTranslation(new Vector3f(
                        (float) (satellite.getData().currentPosition.x / AU * UNIT_SCALE),
                        (float) (satellite.getData().currentPosition.y / AU * UNIT_SCALE),
                        (float) (satellite.getData().currentPosition.z / AU * UNIT_SCALE)));

                bodies.put(newObject.getName(), newObject);
            }
        }
    }

    private void loadOrbits() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        orbitMesh = new RingGenerator().create(2);

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();

            // If the orbit does not already have a WorldObject representation, add it.
            if (!orbits.containsKey(item.getKey())) {
                WorldObject orbit = new WorldObject(item.getKey(), orbitMesh);

                float semiMajorAxis = (float) (satellite.getData().a / AU * UNIT_SCALE);
                float semiMinorAxis = (float) ((satellite.getData().a / AU * UNIT_SCALE)
                        * Math.sqrt(1.0 - Math.pow(satellite.getData().eccentricity, 2)));

                // Scale orbit according to orbital parameters.
                float scaleFactor = semiMajorAxis / semiMinorAxis;
                orbit.setScale(new Vector3f(scaleFactor, 1.f, 1.f));

                // Translate orbit so that the planet is at a focal point.
                float focalDistance = (float) Math.sqrt(Math.pow(semiMajorAxis, 2) - Math.pow(semiMinorAxis, 2));
                orbit.setTranslation(new Vector3f(focalDistance, 0.f, 0.f));

                // Rotate orbits according to orbital parameters.
                orbit.setRotation(new Vector3f(
                        (float) satellite.getData().inclination,
                        (float) satellite.getData().longitudeOfAscendingNode,
                        (float) satellite.getData().argumentOfPeriapsis));

                orbits.put(orbit.getName(), orbit);
            }
        }
    }

    public void updateSatellites() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();
                bodies.get(item.getKey()).setTranslation(new Vector3f(
                        (float) (satellite.getData().currentPosition.x / AU * UNIT_SCALE),
                        (float) (satellite.getData().currentPosition.y / AU * UNIT_SCALE),
                        (float) (satellite.getData().currentPosition.z / AU * UNIT_SCALE)));
        }
    }

    public void addSatellite(WorldObject satellite) {
        if (bodies.containsKey(satellite.getName())) {
            // TODO: handle case of name already taken
        }

        bodies.put(satellite.getName(), satellite);
        // TODO: also add associated orbit when adding body

        updateMatrices();
    }

    public void removeSatellite(String name) {
        if (name != this.name) {
            bodies.remove(name);
        }

        updateMatrices();
    }

    public void setLightSourceDistance(double distance) {// Distance in km
        this.lightSource.setTranslation(new Vector3f((float) (distance / AU * UNIT_SCALE), 0.f, 0.f));
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
