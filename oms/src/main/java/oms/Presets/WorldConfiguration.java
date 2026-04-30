package oms.Presets;

import java.util.List;

import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * Class used for saving all data that is world-relative.
 * 
 * @author Ryan Lau
 */
public class WorldConfiguration {
    private String worldName;
    private BodyConfig body;
    private CameraConfig camera;
    private List<SatelliteConfig> satellites;
    private UIConfig ui;
    private SidebarBody sidebarBody;
    private List<SidebarSatellite> sidebarSatellites;
    private String focusedObjectName;

    /**
     * Create an empty WorldConfiguration.
     */
    public WorldConfiguration() {
    }

    /**
     * Create a WorldConfiguration with the provided data.
     * 
     * @param worldName  name of the world.
     * @param body       body's data.
     * @param camera     world's camera.
     * @param satellites list that contains the data for each satellite.
     * @param ui         user-interface data.
     */
    public WorldConfiguration(String worldName, BodyConfig body, CameraConfig camera,
            List<SatelliteConfig> satellites, UIConfig ui) {
        this.worldName = worldName;
        this.body = body;
        this.camera = camera;
        this.satellites = satellites;
        this.ui = ui;
    }

    /**
     * @return the world's name.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * @return the world's body data.
     */
    public BodyConfig getBody() {
        return body;
    }

    /**
     * @return the world's camera data.
     */
    public CameraConfig getCamera() {
        return camera;
    }

    /**
     * @return the world's satellite data.
     */
    public List<SatelliteConfig> getSatellites() {
        return satellites;
    }

    /**
     * Sets the world's satellite data to the given list.
     * 
     * @param satellites the world's satellite data.
     */
    public void setSatellites(List<SatelliteConfig> satellites) {
        this.satellites = satellites;
    }

    /**
     * @return the UI-related data.
     */
    public UIConfig getUi() {
        return ui;
    }

    /**
     * @return data relevant to UI representation of the world's body.
     */
    public SidebarBody getSidebarBody() {
        return sidebarBody;
    }

    /**
     * Sets data relevant to UI representation of the world's body.
     * 
     * @param sidebarBody the data to set.
     */
    public void setSidebarBody(SidebarBody sidebarBody) {
        this.sidebarBody = sidebarBody;
    }

    /**
     * @return data relevant to UI representation of the world's satellites.
     */
    public List<SidebarSatellite> getSidebarSatellites() {
        return sidebarSatellites;
    }

    /**
     * Sets data relevant to UI representation of the world's satellites.
     * 
     * @param sidebarSatellites the data to set.
     */
    public void setSidebarSatellites(List<SidebarSatellite> sidebarSatellites) {
        this.sidebarSatellites = sidebarSatellites;
    }

    /**
     * @return the currently focused object's name.
     */
    public String getFocusedObjectName() {
        return focusedObjectName;
    }

    /**
     * Sets the currently focused object's name.
     * 
     * @param focusedObjectName the desired focused object's name.
     */
    public void setFocusedObjectName(String focusedObjectName) {
        this.focusedObjectName = focusedObjectName;
    }

    /**
     * Contains all data, math and rendering-related, for a body.
     */
    public static class BodyConfig {
        public String name;
        public double mass;
        public double radius;
        public double distanceToSun;
        public double massOfSun;
        public double eccentricity;
        public Vector3f color;

        public BodyConfig() {
        }

        public BodyConfig(String name, double mass, double radius, double distanceToSun,
                double eccentricity, double massOfSun, Vector3f color) {
            this.name = name;
            this.mass = mass;
            this.radius = radius;
            this.distanceToSun = distanceToSun;
            this.eccentricity = eccentricity;
            this.massOfSun = massOfSun;
            this.color = color;
        }
    }

    /**
     * Contains all data relevant to a camera.
     */
    public static class CameraConfig {
        public Vector3f position;
        public Vector3f direction;

        public CameraConfig() {
        }

        public CameraConfig(Vector3f position, Vector3f direction) {
            this.position = position;
            this.direction = direction;
        }
    }

    /**
     * Contains all data, math and rendering-related, for a satellite.
     */
    public static class SatelliteConfig {
        public String name;
        public double mass;
        public Vector3f color;
        public boolean active = true;

        // Orbital elements
        public double altitude;
        public double speed;
        public double mu;
        public double period;
        public double angularMomentum;
        public Vector3d angularMomentumVect;
        public double eccentricity;
        public Vector3d eccentricityVect;
        public double p;
        public double a;
        public double radiusOfPeriapsis;
        public double radiusOfApoapsis;
        public double excessSpeed;
        public double meanMotion;
        public double inclination;
        public double longitudeOfAscendingNode;
        public double argumentOfPeriapsis;
        public double timeSincePeriapsis;
        public Vector3d lineOfNodesVect;

        // Energy
        public double gravitationalPotentialEnergy;
        public double kineticEnergy;
        public double initialTotalEnergy;
        public double totalEnergy;

        // Position and velocity
        public Vector3d initialPosition;
        public Vector3d currentPosition;
        public Vector3d initialVelocity;
        public Vector3d currentVelocity;

        // Anomalies
        public double meanAnomaly;
        public double initialMeanAnomaly;
        public double eccentricAnomaly;
        public double initialEccentricAnomaly;
        public double trueAnomaly;
        public double initialTrueAnomaly;

        // Time
        public double time0;
        public double currentTime;
        public double lastTime;

        public SatelliteConfig() {
        }
    }

    /**
     * Data representation of the simulation's configuration.
     */
    public static class UIConfig {
        public String specificTime;
        public String timescale;
        public boolean running;

        public UIConfig() {
        }

        public UIConfig(String specificTime, String timescale, boolean running) {
            this.specificTime = specificTime;
            this.timescale = timescale;
            this.running = running;
        }
    }

    /**
     * Data representation of a body within the sidebar (only UI-specific data
     * is stored here).
     */
    public static class SidebarBody {
        public String name;
        public String colorHex;
        public boolean preset;
        public double mass;
        public double radius;
        public boolean selected;

        public SidebarBody() {
        }

        public SidebarBody(String name, String colorHex, boolean preset, double mass, double radius) {
            this.name = name;
            this.colorHex = colorHex;
            this.preset = preset;
            this.mass = mass;
            this.radius = radius;
        }
    }

    /**
     * Data representation of a satellite within the sidebar (only UI-specific data
     * is stored here).
     */
    public static class SidebarSatellite {
        public String name;
        public String colorHex;

        public SidebarSatellite() {
        }

        public SidebarSatellite(String name, String colorHex) {
            this.name = name;
            this.colorHex = colorHex;
        }
    }

    /**
     * Takes data from a SatelliteData object and creates a new SatelliteConfig
     * object with it for saving/loading.
     * 
     * @param data  SatelliteData data (input).
     * @param color Color of the satellite.
     * @return a new SatelliteConfig object with the given data.
     */
    public static SatelliteConfig fromSatelliteData(oms.Math.SatelliteData data, Vector3f color) {
        SatelliteConfig config = new SatelliteConfig();
        config.name = data.name;
        config.mass = data.mass;
        config.color = color;
        config.altitude = data.altitude;
        config.speed = data.speed;
        config.mu = data.mu;
        config.period = data.period;
        config.angularMomentum = data.angularMomentum;
        config.angularMomentumVect = data.angularMomentumVect;
        config.eccentricity = data.eccentricity;
        config.eccentricityVect = data.eccentricityVect;
        config.p = data.p;
        config.a = data.a;
        config.radiusOfPeriapsis = data.radiusOfPeriapsis;
        config.radiusOfApoapsis = data.radiusOfApoapsis;
        config.excessSpeed = data.excessSpeed;
        config.meanMotion = data.meanMotion;
        config.inclination = data.inclination;
        config.longitudeOfAscendingNode = data.longitudeOfAscendingNode;
        config.argumentOfPeriapsis = data.argumentOfPeriapsis;
        config.timeSincePeriapsis = data.timeSincePeriapsis;
        config.lineOfNodesVect = data.lineOfNodesVect;
        config.gravitationalPotentialEnergy = data.gravitationalPotentialEnergy;
        config.kineticEnergy = data.kineticEnergy;
        config.initialTotalEnergy = data.initialTotalEnergy;
        config.totalEnergy = data.totalEnergy;
        config.initialPosition = data.initialPosition;
        config.currentPosition = data.currentPosition;
        config.initialVelocity = data.initialVelocity;
        config.currentVelocity = data.currentVelocity;
        config.meanAnomaly = data.meanAnomaly;
        config.initialMeanAnomaly = data.initialMeanAnomaly;
        config.eccentricAnomaly = data.eccentricAnomaly;
        config.initialEccentricAnomaly = data.initialEccentricAnomaly;
        config.trueAnomaly = data.trueAnomaly;
        config.initialTrueAnomaly = data.initialTrueAnomaly;
        config.time0 = data.time0;
        config.currentTime = data.currentTime;
        config.lastTime = data.lastTime;
        return config;
    }

    /**
     * Takes data from a SatelliteConfig object and applies it to a SatelliteData
     * object for use in simulation math.
     * 
     * @param config SatelliteConfig data (input).
     * @param data   SatelliteData data (output).
     */
    public void applyToSatelliteData(SatelliteConfig config, oms.Math.SatelliteData data) {
        data.name = config.name;
        data.mass = config.mass;
        data.altitude = config.altitude;
        data.speed = config.speed;
        data.mu = config.mu;
        data.period = config.period;
        data.angularMomentum = config.angularMomentum;
        data.angularMomentumVect = config.angularMomentumVect;
        data.eccentricity = config.eccentricity;
        data.eccentricityVect = config.eccentricityVect;
        data.p = config.p;
        data.a = config.a;
        data.radiusOfPeriapsis = config.radiusOfPeriapsis;
        data.radiusOfApoapsis = config.radiusOfApoapsis;
        data.excessSpeed = config.excessSpeed;
        data.meanMotion = config.meanMotion;
        data.inclination = config.inclination;
        data.longitudeOfAscendingNode = config.longitudeOfAscendingNode;
        data.argumentOfPeriapsis = config.argumentOfPeriapsis;
        data.timeSincePeriapsis = config.timeSincePeriapsis;
        data.lineOfNodesVect = config.lineOfNodesVect;
        data.gravitationalPotentialEnergy = config.gravitationalPotentialEnergy;
        data.kineticEnergy = config.kineticEnergy;
        data.initialTotalEnergy = config.initialTotalEnergy;
        data.totalEnergy = config.totalEnergy;
        data.initialPosition = config.initialPosition;
        data.currentPosition = config.currentPosition;
        data.initialVelocity = config.initialVelocity;
        data.currentVelocity = config.currentVelocity;
        data.meanAnomaly = config.meanAnomaly;
        data.initialMeanAnomaly = config.initialMeanAnomaly;
        data.eccentricAnomaly = config.eccentricAnomaly;
        data.initialEccentricAnomaly = config.initialEccentricAnomaly;
        data.trueAnomaly = config.trueAnomaly;
        data.initialTrueAnomaly = config.initialTrueAnomaly;
        data.time0 = config.time0;
        data.currentTime = config.currentTime;
        data.lastTime = config.lastTime;
    }
}