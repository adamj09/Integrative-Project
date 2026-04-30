package oms.Renderer.World;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import oms.Math.Body;
import oms.Math.Constant;
import oms.Math.Satellite;
import oms.Math.SatelliteData;
import oms.Presets.WorldConfiguration;
import oms.Renderer.Camera.Camera;
import oms.Renderer.Mesh.Mesh;
import oms.Renderer.Mesh.RingGenerator;
import oms.Renderer.Mesh.SphereGenerator;

/**
 * Represents a scene to be rendered.
 * 
 * @author Adam Johnston
 */
public class World {
    /**
     * The central celestial body of the world.
     */
    private Body body;

    /**
     * The colour of the body.
     */
    private Vector3f colour;

    /**
     * The light source in the world (e.g., a star).
     */
    private WorldObject lightSource;

    /**
     * A map of all celestial bodies in the world.
     */
    private HashMap<String, WorldObject> bodyObjects = new HashMap<>();

    /**
     * A map of all orbits in the world (Note that this will always contain
     * bodyObjects.size() - 1 entries, since the central body's orbit is not
     * rendered).
     */
    private HashMap<String, WorldObject> orbits = new HashMap<>();

    /**
     * The camera used to view the world.
     */
    private Camera camera = new Camera();

    /**
     * The name of this world.
     */
    private String name;

    /**
     * A WorldConfiguration yet to be applied to this world.
     */
    private WorldConfiguration pendingConfig;

    /**
     * Radius of satellites (in kilometers)
     */
    public static final float SATELLITE_RADIUS = 100.0f;

    /**
     * True if the world is currently being simulated, false otherwise.
     */
    public boolean worldRunning = false;

    /**
     * Buffers for storing transformation matrices and colours of the bodies and
     * orbits.
     */
    private FloatBuffer colorsBuffer = BufferUtils.createFloatBuffer(0),
            bodyMatrixBuffer = BufferUtils.createFloatBuffer(0),
            orbitMatrixBuffer = BufferUtils.createFloatBuffer(0);

    /**
     * The scale factor for converting astronomical units (AU) to renderer units.
     * I.e. 1 AU is equal to this many renderer units.
     */
    private final float UNIT_SCALE = 10_000.f;

    /**
     * Meshes for rendering the bodies and orbits.
     */
    private Mesh bodyMesh, orbitMesh, lightSourceMesh;

    /**
     * Creates a new world with the given central body. The world is initialized
     * with
     * the light source, the central body, and its satellites and their
     * corresponding orbits.
     * 
     * @param body   the central body of the world.
     * @param colour the colour of the central body.
     */
    public World(Body body, Vector3f colour) {
        this.body = body;
        this.colour = colour;
        this.name = body.getName();

        // Generate the meshes upon world creation.
        bodyMesh = new SphereGenerator().create(4);
        orbitMesh = new RingGenerator().create(1);
        lightSourceMesh = new SphereGenerator().create(4);

        // Light source object.
        lightSource = new WorldObject("light", lightSourceMesh);

        // Set initial camera position (this only affects the free look camera).
        camera.setView(new Vector3f(10.f, 10.f, 10.f), camera.getDirection());

        // Create the light source and central body objects.
        initLightSource();
        createCentralBody();

        // Update the buffers with the initial data.
        bodyMatrixBuffer = updateMatrixBuffer(bodyObjects);
        orbitMatrixBuffer = updateMatrixBuffer(orbits);
        colorsBuffer = updateColorBuffer(bodyObjects);
    }

    /**
     * Updates the given matrix buffer with the transformation matrices of the given
     * world objects.
     * 
     * @param objects the world objects whose transformation matrices will be packed
     *                into the buffer.
     * @return The FloatBuffer containing the packed transformation matrices of the
     *         world objects.
     */
    public FloatBuffer updateMatrixBuffer(HashMap<String, WorldObject> objects) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16 * objects.size());

        int i = 0;
        for (Map.Entry<String, WorldObject> item : objects.entrySet()) {
            item.getValue().getTransformMatrix().get(i * 16, buffer);
            i++;
        }

        return buffer;
    }

    /**
     * Updates the color buffer with the colors of the given world objects.
     * 
     * @param objects the world objects whose colors will be packed into the buffer.
     * @return The FloatBuffer containing the packed colors of the world objects.
     */
    public FloatBuffer updateColorBuffer(HashMap<String, WorldObject> objects) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3 * objects.size());

        int i = 0;
        for (Map.Entry<String, WorldObject> item : objects.entrySet()) {
            item.getValue().getColor().get(i * 3, buffer);
            i++;
        }

        return buffer;
    }

    /**
     * Creates the light source for the world.
     */
    private void initLightSource() {
        float lightSourceScale = (float) (696_340d / Constant.AU * UNIT_SCALE); // Sun's radius in AUs times scale

        lightSource.resetTransforms();
        lightSource.translate(new Vector3f((float) (body.getSemiMajorAxis() / Constant.AU * UNIT_SCALE), 0.f, 0.f));
        lightSource.scale(new Vector3f(lightSourceScale, lightSourceScale, lightSourceScale));
        lightSource.setLightColor(new Vector3f(1.f, 1.f, 1.f));
    }

    /**
     * Creates the central body for the world.
     */
    private void createCentralBody() {
        WorldObject bodyObject = new WorldObject(body.getName(), bodyMesh, colour);

        float planetScale = (float) (body.getRadius() / Constant.AU * UNIT_SCALE);
        bodyObject.scale(new Vector3f(planetScale, planetScale, planetScale));

        // Add objects to world.
        bodyObjects.put(bodyObject.getName(), bodyObject);
    }

    /**
     * Updates the positions of all satellites in the world.
     */
    public void updateSatellitePositions() {
        for (Map.Entry<String, Satellite> item : body.getSatellites().entrySet()) {
            SatelliteData data = item.getValue().getData();

            if (data.currentPosition == null) {
                System.err.println("current position null");
                return;
            }

            WorldObject object = bodyObjects.get(item.getKey());

            Vector3f scale = object.getScale();

            object.resetTransforms();

            object.translate(new Vector3f(
                    (float) (data.currentPosition.y / 1000.d / Constant.AU * UNIT_SCALE),
                    (float) (data.currentPosition.z / 1000.d / Constant.AU * UNIT_SCALE),
                    (float) (data.currentPosition.x / 1000.d / Constant.AU * UNIT_SCALE)));

            object.scale(new Vector3f(scale.x, scale.y, scale.z));
        }

        bodyMatrixBuffer = updateMatrixBuffer(bodyObjects);
        orbitMatrixBuffer = updateMatrixBuffer(orbits);
    }

    /**
     * Updates the color of the world object with the given name.
     * 
     * @param objectName the name of the world object whose color will be updated.
     * @param color      the new color to set for the world object.
     */
    public void updateColor(String objectName, Vector3f color) {
        if (bodyObjects.containsKey(objectName)) {
            bodyObjects.get(objectName).setColor(color);
            colorsBuffer = updateColorBuffer(bodyObjects);
        }
    }

    /**
     * Updates the radius of the world object with the given name.
     * 
     * @param objectName the name of the world object whose radius will be updated.
     * @param radius     the new radius to set for the world object, in kilometers.
     */
    public void updateRadius(String objectName, float radius) {
        if (bodyObjects.containsKey(objectName)) {
            WorldObject object = bodyObjects.get(objectName);
            object.resetTransforms();
            object.scale(new Vector3f((float) (radius / Constant.AU * UNIT_SCALE),
                    (float) (radius / Constant.AU * UNIT_SCALE),
                    (float) (radius / Constant.AU * UNIT_SCALE)));

            if (objectName.equals(body.getName())) {
                body.setRadius(radius);

                updateSatellitePositions();
            }

            bodyMatrixBuffer = updateMatrixBuffer(bodyObjects);
        }
    }

    /**
     * Updates the orbital elements of the satellite with the given name.
     * 
     * @param satelliteName          the name of the satellite whose orbital
     *                               elements will be updated.
     * @param massOfSatellite        the mass of the satellite, in kilograms.
     * @param altitude               the altitude of the satellite, in kilometers.
     * @param ecentricity            the eccentricity of the satellite's orbit
     *                               (unitless).
     * @param trueAnomaly            the true anomaly of the satellite's orbit, in
     *                               degrees.
     * @param longitudeAscendingNode the longitude of the ascending node of the
     *                               satellite's orbit, in degrees.
     * @param inclination            the inclination of the satellite's orbit, in
     *                               degrees.
     * @param argumentOfPeriapisis   the argument of periapsis of the satellite's
     *                               orbit, in degrees.
     */
    public void updateOrbitalElements(String satelliteName, double massOfSatellite, double altitude, double ecentricity,
            double trueAnomaly, double longitudeAscendingNode, double inclination, double argumentOfPeriapisis) {
        if (!body.getSatellites().containsKey(satelliteName)) {
            return;
        }

        Satellite satellite = body.getSatellite(satelliteName);

        if (!satellite.initialiseSatelliteValuesAngles(body, satelliteName, massOfSatellite, altitude, ecentricity,
                trueAnomaly, longitudeAscendingNode, inclination, argumentOfPeriapisis)) {
            System.err.println(satellite.getLatestError());
            System.err.println("Failed to update orbital elements for satellite: " + satelliteName);
            return;
        }

        transformOrbit(satelliteName);
    }

    /**
     * Adds a satellite to the world, along with its corresponding orbit.
     * 
     * @param satellite the satellite to be added to the world.
     * @param color     the color to render the satellite with.
     */
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
                (float) (data.initialPosition.y / 1000.d / Constant.AU * UNIT_SCALE),
                (float) (data.initialPosition.z / 1000.d / Constant.AU * UNIT_SCALE),
                (float) (data.initialPosition.x / 1000.d / Constant.AU * UNIT_SCALE)));

        float satelliteRadius = (float) (body.getRadius() / Constant.AU * UNIT_SCALE / 50.f);

        satelliteObject.scale(new Vector3f(satelliteRadius, satelliteRadius, satelliteRadius));

        bodyObjects.put(satelliteObject.getName(), satelliteObject);

        addOrbit(satellite);

        bodyMatrixBuffer = updateMatrixBuffer(bodyObjects);
        colorsBuffer = updateColorBuffer(bodyObjects);
    }

    /**
     * Adds an orbit object for the given satellite. The orbit is oriented and
     * scaled
     * according to the satellite's orbital parameters.
     * 
     * @param satellite the satellite whose orbit will be added.
     */
    private void addOrbit(Satellite satellite) {
        // Add satellite orbit object.
        WorldObject orbit = new WorldObject(satellite.getData().name, orbitMesh);
        orbits.put(orbit.getName(), orbit);

        transformOrbit(orbit.getName());
    }

    private void transformOrbit(String orbitName) {
        if (!orbits.containsKey(orbitName) || !body.getSatellites().containsKey(orbitName)) {
            System.err.println("Failed to transform orbit: " + orbitName + ". Orbit or satellite not found.");
            return;
        }

        WorldObject orbit = orbits.get(orbitName);
        SatelliteData data = body.getSatellite(orbitName).getData();

        orbit.resetTransforms();

        // Notes:
        // Y -> X, Z -> Y, X -> Z due to different coordinate systems used in the
        // simulation math and rendering.
        // The following operations are applied in reverse order. Therefore, what comes
        // first is the scaling, then the translation, and finally the rotations. This
        // is due to the non-commutative nature of matrix multiplication, which is used
        // to apply the transformations to the orbit object.

        // --- Rotate orbit to correct orientation based on orbital parameters ---

        // Rotate by argument of periapsis around angular momentum vector (which is a
        // vector normal to the orbital plane).
        orbit.rotate((float) (-data.argumentOfPeriapsis), new Vector3f((float) data.angularMomentumVect.y,
                (float) data.angularMomentumVect.z, (float) data.angularMomentumVect.x).normalize());

        // Rotate by inclination around line of nodes vector (which is the intersection
        // of the orbital plane and the reference plane).

        if (data.inclination != 0) {
            orbit.rotate((float) (data.inclination), new Vector3f((float) data.lineOfNodesVect.y,
                    (float) data.lineOfNodesVect.z, (float) data.lineOfNodesVect.x).normalize());
        }

        // Rotate by longitude of ascending node around reference plane normal vector
        // (Y-axis).
        orbit.rotate((float) (-data.longitudeOfAscendingNode), new Vector3f(0.f, 1.f, 0.f));

        // --- Calculate the semi-major and semi-minor axes of the orbit in renderer
        // units ---
        double semiMajorAxis = data.a / 1000.d / Constant.AU * UNIT_SCALE;
        double semiMinorAxis = semiMajorAxis * Math.sqrt(1.0 - Math.pow(data.eccentricity, 2));

        // --- Translate orbit so that the central body is at a focal point ---
        float focalDistance = (float) Math.sqrt(Math.pow(semiMajorAxis, 2) - Math.pow(semiMinorAxis, 2));
        orbit.translate(new Vector3f((float) 0, 0, -focalDistance));

        // --- Scale orbit according to orbital parameters ---
        orbit.scale(new Vector3f((float) semiMinorAxis, 1.f, (float) semiMajorAxis));

        orbitMatrixBuffer = updateMatrixBuffer(orbits);
    }

    /**
     * Removes the satellite with the given name from the world, along with its
     * associated orbit.
     * 
     * @param name the name of the satellite to be removed.
     */
    public void removeSatellite(String name) {
        // Remove the satellite from the central body and from the World.
        if (name != body.getName()) {
            bodyObjects.remove(name);
            body.removeSatellite(name);
        }

        // Remove orbit associated with the satellite.
        orbits.remove(name);

        // Update buffers after removal.
        bodyMatrixBuffer = updateMatrixBuffer(bodyObjects);
        orbitMatrixBuffer = updateMatrixBuffer(orbits);
        colorsBuffer = updateColorBuffer(bodyObjects);
    }

    /**
     * Runs this world's orbital simulation.
     */
    public void runWorld() {
        if (body.getNumberOfSatellites() == 1) {
            body.startTimeThread();
            body.startSatellites();
            this.worldRunning = true;
            body.start();
            body.resetTime();
        } else {
            body.sateliteUpdateInfo();
        }

    }

    /**
     * Starts this world's orbital simulation without resetting simulation time to
     * 0.
     */
    public void startWorld() {
        body.startTimeThread();
        body.startSatellites();
        this.worldRunning = true;
        body.start();
    }

    /**
     * Stops this world's orbital simulation.
     */
    public void stopWorld() {
        body.stop();
        body.stopSatellites();
        body.stopTimeThread();
        this.worldRunning = false;
    }

    /**
     * @return true if this world's orbital simulation is running, false otherwise.
     */
    public boolean isWorldRunning() {
        return this.worldRunning;
    }

    /**
     * Disposes of all OpenGL objects associated with this world.
     */
    public void dispose() {
        bodyMesh.dispose();
        orbitMesh.dispose();
        lightSourceMesh.dispose();
    }

    /**
     * @return The map of all celestial bodies in the world.
     */
    public HashMap<String, WorldObject> getBodyObjects() {
        return this.bodyObjects;
    }

    /**
     * @return The map of all orbits in the world.
     */
    public HashMap<String, WorldObject> getOrbitObjects() {
        return this.orbits;
    }

    /**
     * @return The camera used to view the world.
     */
    public Camera getCamera() {
        return this.camera;
    }

    /**
     * @return The FloatBuffer containing the packed colors of the world objects.
     */
    public FloatBuffer getColorsBuffer() {
        return this.colorsBuffer;
    }

    /**
     * @return The FloatBuffer containing the packed transformation matrices of the
     *         body objects.
     */
    public FloatBuffer getBodyMatrixBuffer() {
        return this.bodyMatrixBuffer;
    }

    /**
     * @return The FloatBuffer containing the packed transformation matrices of the
     *         orbit objects.
     */
    public FloatBuffer getOrbitMatrixBuffer() {
        return this.orbitMatrixBuffer;
    }

    /**
     * @return The Mesh used to render the body objects.
     */
    public Mesh getBodyMesh() {
        return this.bodyMesh;
    }

    /**
     * @return The Mesh used to render the orbit objects.
     */
    public Mesh getOrbitMesh() {
        return this.orbitMesh;
    }

    /**
     * @return The Mesh used to render the light source.
     */
    public Mesh getLightSourceMesh() {
        return this.lightSourceMesh;
    }

    /**
     * @return The light source of the world.
     */
    public WorldObject getLightSource() {
        return this.lightSource;
    }

    /**
     * @return The central celestial body of the world.
     */
    public Body getBody() {
        return this.body;
    }

    /**
     * @return The color of the central celestial body of the world.
     */
    public Vector3f getColour() {
        return this.colour;
    }

    /**
     * @return the world's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Packs world data in a new WorldConfiguration object for saving.
     * 
     * @param uiConfig the UI configuration to use in the new WorldConfiguration.
     * @return a new WorldConfiguration containing this world's data.
     */
    public WorldConfiguration toWorldConfiguration(WorldConfiguration.UIConfig uiConfig) {
        Vector3f bodyColor = new Vector3f(1.f, 1.f, 1.f);
        WorldObject bodyObj = bodyObjects.get(body.getName());

        if (bodyObj != null) {
            bodyColor = new Vector3f(bodyObj.getColor());
        }

        WorldConfiguration.BodyConfig bodyConfig = new WorldConfiguration.BodyConfig(
                body.getName(), body.getMass(), body.getRadius(),
                body.getSemiMajorAxis(), body.getEccentricity(), body.getMassOfSun(), bodyColor);

        WorldConfiguration.CameraConfig cameraConfig = new WorldConfiguration.CameraConfig(
                new Vector3f(camera.getPosition()), new Vector3f(camera.getDirection()));

        List<WorldConfiguration.SatelliteConfig> satConfigs = new ArrayList<>();

        for (Map.Entry<String, Satellite> entry : body.getSatellites().entrySet()) {
            SatelliteData data = entry.getValue().getData();

            Vector3f satColor = new Vector3f(1.f, 0.f, 0.f);

            WorldObject satObj = bodyObjects.get(entry.getKey());
            if (satObj != null) {
                satColor = new Vector3f(satObj.getColor());
            }
            satConfigs.add(WorldConfiguration.fromSatelliteData(data, satColor));
        }

        return new WorldConfiguration(name, bodyConfig, cameraConfig, satConfigs, uiConfig);
    }

    /**
     * Sets the pending WorldConfiguration.
     * 
     * @param config pending configuration to be set.
     */
    public void setPendingWorldConfiguration(WorldConfiguration config) {
        this.pendingConfig = config;
    }

    /**
     * Applies the pending world configuration to this world.
     */
    public void applyPendingConfiguration() {
        WorldConfiguration config = this.pendingConfig;
        if (config == null) {
            return;
        }
        this.pendingConfig = null;

        if (body.getRunningSatus()) {
            body.stop();
            body.stopSatellites();
            body.stopTimeThread();
        }

        if (config.getCamera() != null) {
            camera.setView(config.getCamera().position, config.getCamera().direction);
        }

        initLightSource();

        // Add satellites to world.
        if (config.getSatellites() != null) {
            for (WorldConfiguration.SatelliteConfig satConfig : config.getSatellites()) {
                // Always load ALL satellites into simulation,
                // active flag only controls UI visibility later
                Satellite sat = new Satellite();

                if (!sat.initialiseSatelliteValuesAngles(body, satConfig.name,
                        satConfig.mass,
                        satConfig.altitude, satConfig.eccentricity, satConfig.trueAnomaly,
                        Math.toDegrees(satConfig.longitudeOfAscendingNode), Math.toDegrees(satConfig.inclination),
                        Math.toDegrees(satConfig.argumentOfPeriapsis))) {
                    System.err.println(sat.getLatestError());
                }

                // Use the EXACT same working logic that normally adds satellites
                Vector3f satColor = satConfig.color != null ? satConfig.color : new Vector3f(1.f, 0.f, 0.f);
                addSatellite(sat, satColor);
            }
        }
    }
}
