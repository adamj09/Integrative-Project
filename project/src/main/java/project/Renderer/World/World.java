package project.Renderer.World;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import project.Math.Body;
import project.Math.Satellite;
import project.Math.SatelliteData;
import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;
import project.Renderer.Model.RingGenerator;
import project.Renderer.Model.SphereGenerator;

// TODO: saving and loading of worlds
public class World {
    private Body body;

    private WorldObject lightSource;
    private HashMap<String, WorldObject> bodyObjects = new HashMap<>();
    private HashMap<String, WorldObject> orbits = new HashMap<>();

    private Camera camera = new Camera();

    private FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(0);
    private FloatBuffer bodyMatrixBuffer = BufferUtils.createFloatBuffer(0);
    private FloatBuffer orbitMatrixBuffer = BufferUtils.createFloatBuffer(0);

    private transient final float UNIT_SCALE = 10_000.f; // 1 AU is equal to this many renderer units.
    private transient final double AU = 1.496e+8d; // 1 AU in kilometers.
    private transient final float SATELLITE_RADIUS = (float) (1000.d / AU * UNIT_SCALE);

    private Mesh bodyMesh;
    private Mesh orbitMesh;

    public World(Body body) {
        this.body = body;

        bodyMesh = new SphereGenerator().create(4);
        orbitMesh = new RingGenerator().create(2);

        camera.setView(new Vector3f(10.f, 10.f, 10.f), camera.getDirection());

        createLightSource();
        createCentralBody();

        updateBodyMatrixBuffer();
        updateOrbitMatrixBuffer();
        updateColorBuffer();
    }

    public void updateBodyMatrixBuffer() {
        if (bodyMatrixBuffer.capacity() != bodyObjects.size() * 16) {
            bodyMatrixBuffer = BufferUtils.createFloatBuffer(16 * bodyObjects.size());
        }

        bodyMatrixBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : bodyObjects.entrySet()) {
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
        if (colorsBuffer.capacity() != bodyObjects.size() * 3) {
            colorsBuffer = BufferUtils.createFloatBuffer(3 * bodyObjects.size());
        }

        colorsBuffer.clear();
        int i = 0;
        for (Map.Entry<String, WorldObject> item : bodyObjects.entrySet()) {
            item.getValue().getColor().get(i * 3, colorsBuffer);
            i++;
        }
    }

    private void createLightSource() {
        float lightSourceScale = (float) (696_340d / AU * UNIT_SCALE); // Sun's radius in AUs times scale

        lightSource = new WorldObject("light", new SphereGenerator().create(4));

        float distance = (float) (body.getDistanceToSun() / AU * UNIT_SCALE);

        lightSource.translate(new Vector3f(-distance, 0.f, 0.f));
        lightSource.scale(new Vector3f(lightSourceScale, lightSourceScale, lightSourceScale));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));
    }

    private void createCentralBody() {
        WorldObject bodyObject = new WorldObject(body.getName(), bodyMesh, new Vector3f(1.0f, 1.0f, 1.0f));

        float planetScale = (float) (body.getRadius() / AU * UNIT_SCALE);
        bodyObject.scale(new Vector3f(planetScale, planetScale, planetScale));

        // Add objects to world.
        bodyObjects.put(bodyObject.getName(), bodyObject);
    }

    public void updateSatellites() {
        HashMap<String, Satellite> satellites = body.getSatellites();

        for (Map.Entry<String, Satellite> item : satellites.entrySet()) {
            Satellite satellite = item.getValue();
            WorldObject object = bodyObjects.get(item.getKey());

            object.resetTransforms();

            object.translate(new Vector3f(
                    (float) (satellite.getData().currentPosition.y / 1000.d / AU * UNIT_SCALE),
                    (float) (satellite.getData().currentPosition.z / 1000.d / AU * UNIT_SCALE),
                    (float) (satellite.getData().currentPosition.x / 1000.d / AU * UNIT_SCALE)));

            object.scale(new Vector3f(SATELLITE_RADIUS, SATELLITE_RADIUS, SATELLITE_RADIUS));
        }

        // TODO: update orbits too

        updateBodyMatrixBuffer();
    }

    public void addSatellite(Satellite satellite, Vector3f color) {
        body.addSatellite(satellite);

        SatelliteData data = satellite.getData();

        // Satellite has already been added, do nothing.
        if (bodyObjects.containsKey(data.name)) {
            return;
        }

        // Add satellite object.
        WorldObject satelliteObject = new WorldObject(data.name, bodyMesh, color);

        // Y -> X, Z -> Y, X -> Z due to different coordinate systems used in the
        // simulation math and rendering.
        satelliteObject.translate(new Vector3f(
                (float) (data.initialPosition.y / 1000.d / AU * UNIT_SCALE),
                (float) (data.initialPosition.z / 1000.d / AU * UNIT_SCALE),
                (float) (data.initialPosition.x / 1000.d / AU * UNIT_SCALE)));

        satelliteObject.scale(new Vector3f(SATELLITE_RADIUS, SATELLITE_RADIUS, SATELLITE_RADIUS));

        bodyObjects.put(satelliteObject.getName(), satelliteObject);

        // Add satellite orbit object.
        WorldObject orbit = new WorldObject(data.name, orbitMesh);

        double semiMajorAxis = data.a / 1000.d / AU * UNIT_SCALE;
        double semiMinorAxis = (data.a / 1000.d / AU * UNIT_SCALE)
                * Math.sqrt(1.0 - Math.pow(data.eccentricity, 2));

        // Translate orbit so that the planet is at a focal point.
        float focalDistance = (float) Math.sqrt(Math.pow(semiMajorAxis, 2) - Math.pow(semiMinorAxis, 2));
        
        Vector3d eccentricityVect = new Vector3d();
        data.eccentricityVect.normalize(eccentricityVect);

        Vector3d displacement = new Vector3d();
        eccentricityVect.mul(focalDistance, displacement);
        displacement.negate();

        orbit.translate(new Vector3f((float) displacement.y, (float) displacement.z, (float) displacement.x));

        // Rotate orbits according to orbital parameters.
        orbit.rotate((float) data.argumentOfPeriapsis,
            new Vector3f((float) data.angularMomentumVect.y, (float)  data.angularMomentumVect.z, (float) data.angularMomentumVect.x).normalize());

        orbit.rotate((float) (data.inclination), 
            new Vector3f((float) data.lineOfNodesVect.y, (float) data.lineOfNodesVect.z, (float) data.lineOfNodesVect.x).normalize());

        orbit.rotate((float) data.longitudeOfAscendingNode,
                new Vector3f((float) 0.f, (float) 1.f, (float) 0.f).normalize());

        // Scale orbit according to orbital parameters.
        orbit.scale(new Vector3f((float) semiMinorAxis, 1.f, (float) semiMajorAxis));

        orbits.put(orbit.getName(), orbit);

        updateBodyMatrixBuffer();
        updateOrbitMatrixBuffer();
        updateColorBuffer();
    }

    public void removeSatellite(String name) {
        if (name != body.getName()) {
            bodyObjects.remove(name);
            body.removeSatellite(name);
        }

        // updateMatrices();
    }

    public void setLightSourceDistance(double distance) {// Distance in km
        this.lightSource.translate(
                new Vector3f((float) (distance / AU * UNIT_SCALE) - this.lightSource.getTranslation().x, 0.f, 0.f));
    }

    public HashMap<String, WorldObject> getBodies() {
        return this.bodyObjects;
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

    public Body getBody() {
        return this.body;
    }
}
