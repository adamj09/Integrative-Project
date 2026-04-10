package project.Renderer.World;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Math.Body;
import project.Math.MathOrbits;
import project.Math.Satellite;
import project.Math.SatelliteData;
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
    private final double AU = 1.496e+8d; // 1 AU in kilometers.
    private final float SATELLITE_RADIUS = (float) (1000.d / AU * UNIT_SCALE);

    private Mesh bodyMesh;
    private Mesh orbitMesh;

    public World(String name) {
        this.name = name;

        camera.setView(new Vector3f(10.f, 10.f, 10.f), camera.getDirection());
    }

    public void updateBodyMatrixBuffer() {
        if (bodyMatrixBuffer.capacity() != bodies.size() * 16) {
            bodyMatrixBuffer = BufferUtils.createFloatBuffer(16 * bodies.size());
        }

        bodyMatrixBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : bodies.entrySet()) {
            item.getValue().getTransformMatrix().get(i * 16, bodyMatrixBuffer);
            i++;
        }
    }

    public void updateOrbitMatrixBuffer() {
        if (orbitMatrixBuffer.capacity() != orbits.size() * 16) {
            orbitMatrixBuffer = BufferUtils.createFloatBuffer(16 * orbits.size());
        }

        orbitMatrixBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : orbits.entrySet()) {
            item.getValue().getTransformMatrix().get(i * 16, orbitMatrixBuffer);
            i++;
        }
    }

    public void updateColorBuffer() {
        if (colorsBuffer.capacity() != bodies.size() * 3) {
            colorsBuffer = BufferUtils.createFloatBuffer(3 * bodies.size());
        }

        colorsBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : bodies.entrySet()) {
            item.getValue().getColor().get(i * 3, colorsBuffer);
            i++;
        }
    }

    public void setBody(Body body) {
        this.body = body;

        loadLightSource();
        loadCentralBody();
        loadSatellites();
        loadOrbits();

        updateBodyMatrixBuffer();
        updateOrbitMatrixBuffer();
        updateColorBuffer();
    }

    private void loadLightSource() {
        float lightSourceScale = (float) (696_340d / AU * UNIT_SCALE); // Sun's radius in AUs times scale

        lightSource = new WorldObject("light", new SphereGenerator().create(4));

        lightSource.translate(new Vector3f((float) (body.getDistanceToSun() / AU * UNIT_SCALE), 0.f, 0.f));
        lightSource.scale(new Vector3f(lightSourceScale, lightSourceScale, lightSourceScale));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));
    }

    private void loadCentralBody() {
        bodyMesh = new SphereGenerator().create(4);
        WorldObject bodyObject = new WorldObject(name, bodyMesh, new Vector3f(1.0f, 1.0f, 1.0f));

        float planetScale = (float) (body.getRadius() / AU * UNIT_SCALE);
        bodyObject.scale(new Vector3f(planetScale, planetScale, planetScale));

        // Add objects to world.
        bodies.put(bodyObject.getName(), bodyObject);
    }

    private void loadSatellites() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();

            // If the satellite does not already have a WorldObject representation, add it.
            if (!bodies.containsKey(item.getKey())) {
                WorldObject newObject = new WorldObject(satellite.getData().name, bodyMesh,
                        new Vector3f(1.f, 0.f, 0.f));

                newObject.translate(new Vector3f(
                    (float) (satellite.getData().currentPosition.y / 1000.d / AU * UNIT_SCALE),
                    (float) (satellite.getData().currentPosition.z / 1000.d / AU * UNIT_SCALE),
                    (float) (satellite.getData().currentPosition.x / 1000.d / AU * UNIT_SCALE)));

                newObject.scale(new Vector3f(SATELLITE_RADIUS, SATELLITE_RADIUS, SATELLITE_RADIUS));

                bodies.put(newObject.getName(), newObject);
            }
        }
    }

    private void loadOrbits() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        orbitMesh = new RingGenerator().create(2);

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            // If the orbit does not already have a WorldObject representation, add it.
            if (!orbits.containsKey(item.getKey())) {
                WorldObject orbit = new WorldObject(item.getKey(), orbitMesh);
                SatelliteData data = item.getValue().getData();

                double semiMajorAxis = data.a / 1000.d / AU * UNIT_SCALE;
                double semiMinorAxis = (data.a / 1000.d / AU * UNIT_SCALE)
                        * Math.sqrt(1.0 - Math.pow(data.eccentricity, 2));

                // Translate orbit so that the planet is at a focal point.
                float focalDistance = (float) Math.sqrt(Math.pow(semiMajorAxis, 2) - Math.pow(semiMinorAxis, 2));
                orbit.translate(new Vector3f(0.f, 0.f, -focalDistance));

                orbit.rotate(
                        (float) 0,
                        new Vector3f(
                                (float) 0.f,
                                (float) 1.f,
                                (float) 0.f).normalize());

                // // Rotate orbits according to orbital parameters.
                orbit.rotate(
                        (float) (-data.inclination),
                        new Vector3f(
                                0.f,
                                0.f,
                                1.f));

                orbit.rotate(
                        (float) 0,
                        new Vector3f(
                                (float) data.angularMomentumVect.y,
                                (float) data.angularMomentumVect.z,
                                (float) data.angularMomentumVect.x).normalize());

                // Scale orbit according to orbital parameters.
                orbit.scale(new Vector3f((float) semiMinorAxis, 1.f, (float) semiMajorAxis));

                orbits.put(orbit.getName(), orbit);
            }
        }
    }

    public void updateSatellites() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();
            WorldObject object = bodies.get(item.getKey());

            object.resetTransforms();

            object.translate(new Vector3f(
                    (float) (satellite.getData().currentPosition.y / 1000.d / AU * UNIT_SCALE),
                    (float) (satellite.getData().currentPosition.z / 1000.d / AU * UNIT_SCALE),
                    (float) (satellite.getData().currentPosition.x / 1000.d / AU * UNIT_SCALE)));
            
            object.scale(new Vector3f(SATELLITE_RADIUS, SATELLITE_RADIUS, SATELLITE_RADIUS));
        }

        updateBodyMatrixBuffer();
    }

    public void addSatellite(WorldObject satellite) {
        if (bodies.containsKey(satellite.getName())) {
            // TODO: handle case of name already taken
        }

        bodies.put(satellite.getName(), satellite);
        // TODO: also add associated orbit when adding body

        // updateMatrices();
    }

    public void removeSatellite(String name) {
        if (name != this.name) {
            bodies.remove(name);
        }

        // updateMatrices();
    }

    public void setLightSourceDistance(double distance) {// Distance in km
        this.lightSource.translate(new Vector3f((float) (distance / AU * UNIT_SCALE) - this.lightSource.getTranslation().x, 0.f, 0.f));
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
