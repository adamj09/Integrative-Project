package project.Presets;

import java.util.List;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class WorldConfiguration {
    private String worldName;
    private BodyConfig body;
    private CameraConfig camera;
    private List<SatelliteConfig> satellites;
    private UIConfig ui;
    private List<SidebarBody> sidebarBodies;
    private List<SidebarSatellite> sidebarSatellites;

    public WorldConfiguration(String worldName, BodyConfig body, CameraConfig camera,
                              List<SatelliteConfig> satellites, UIConfig ui) {
        this.worldName = worldName;
        this.body = body;
        this.camera = camera;
        this.satellites = satellites;
        this.ui = ui;
    }

    public String getWorldName() { return worldName; }
    public BodyConfig getBody() { return body; }
    public CameraConfig getCamera() { return camera; }
    public List<SatelliteConfig> getSatellites() { return satellites; }
    public UIConfig getUi() { return ui; }
    public List<SidebarBody> getSidebarBodies() { return sidebarBodies; }
    public void setSidebarBodies(List<SidebarBody> sidebarBodies) { this.sidebarBodies = sidebarBodies; }
    public List<SidebarSatellite> getSidebarSatellites() { return sidebarSatellites; }
    public void setSidebarSatellites(List<SidebarSatellite> sidebarSatellites) { this.sidebarSatellites = sidebarSatellites; }

    public static class BodyConfig {
        public String name;
        public double mass;
        public double radius;
        public double distanceToSun;
        public double massOfSun;
        public Vector3f color;

        public BodyConfig(String name, double mass, double radius, double distanceToSun,
                          double massOfSun, Vector3f color) {
            this.name = name;
            this.mass = mass;
            this.radius = radius;
            this.distanceToSun = distanceToSun;
            this.massOfSun = massOfSun;
            this.color = color;
        }
    }

    public static class CameraConfig {
        public Vector3f position;
        public Vector3f direction;

        public CameraConfig(Vector3f position, Vector3f direction) {
            this.position = position;
            this.direction = direction;
        }
    }

    public static class SatelliteConfig {
        public String name;
        public double mass;
        public Vector3f color;

        // Orbital elements
        public double distance;
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
        public double maximumDistanceToBody;
    }

    public static class UIConfig {
        public String specificTime;
        public String timescale;
        public boolean running;

        public UIConfig(String specificTime, String timescale, boolean running) {
            this.specificTime = specificTime;
            this.timescale = timescale;
            this.running = running;
        }
    }

    public static class SidebarBody {
        public String name;
        public String colorHex;
        public boolean preset;
        public double mass;
        public double radius;

        public SidebarBody(String name, String colorHex, boolean preset, double mass, double radius) {
            this.name = name;
            this.colorHex = colorHex;
            this.preset = preset;
            this.mass = mass;
            this.radius = radius;
        }
    }

    public static class SidebarSatellite {
        public String name;
        public String colorHex;

        public SidebarSatellite(String name, String colorHex) {
            this.name = name;
            this.colorHex = colorHex;
        }
    }
}
