package oms.Physics;

import org.joml.Vector3d;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Physical representation of a satellite with negligeable mass compared to the
 * celestial object it is orbiting.
 * 
 * @author Maxime Gauthier
 */
public class Satellite implements Runnable {
    private final SatelliteData data;
    private double massOfBody;
    private String latestError = "null";
    private boolean simulationRunning = false;

    // ---------------------------------------------------------------------------------------------------------------------------
    // constructors;

    // default constructor
    public Satellite() {
        this.data = new SatelliteData();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    // initialisation of the satellite

    /**
     * Initialises the satellite using a 3d initial position vector and a 3d initial
     * velocity vector.
     * 
     * @param body            the central body that the satellite is orbiting
     * @param satName         name (and id) of the satellite
     * @param massOfSatellite mass of the the satellite in kg
     * @param px              position in x in km
     * @param py              position in y in km
     * @param pz              position in z in km
     * @param vx              velocity in x in m/s
     * @param vy              velocity in y in m/s
     * @param vz              velocity in z in m/s
     * @return false if the initialisation failled / not correct
     */
    public boolean initialiseSatelliteValuesVectors(Body body, String satName, double massOfSatellite,
            double px, double py, double pz, double vx, double vy, double vz) {

        if (!this.initName(satName, body.getName())) {
            return false;
        }

        if (px < Constant.MINIMUM_ALTITUDE) {
            this.setLatestError("initial position in x is too close to the body: " + px + " km. Minimum distance is "
                    + Constant.MINIMUM_ALTITUDE + " km");
            return false;
        }
        if (py < Constant.MINIMUM_ALTITUDE) {
            this.setLatestError("initial position in y is too close to the body: " + py + " km. Minimum distance is "
                    + Constant.MINIMUM_ALTITUDE + " km");
            return false;
        }
        if (pz < Constant.MINIMUM_ALTITUDE) {
            this.setLatestError("initial position in z is too close to the body: " + pz + " km. Minimum distance is "
                    + Constant.MINIMUM_ALTITUDE + " km");
            return false;
        }

        this.massOfBody = body.getMass();
        this.updateData(data -> {
            data.mass = massOfSatellite;
            data.initialPosition = new Vector3d(px * 1000, py * 1000, pz * 1000);
            data.initialVelocity = new Vector3d(vx, vy, vz);
        });

        return this.initialiseSatelliteInfo(body);
    }

    /**
     * Initialise the satellite using classical orbital elements.
     * 
     * @param body                   the central body that the satellite is orbiting
     * @param satName                name (and id) of the satellite
     * @param massOfSatellite        mass of the the satellite in kg
     * @param altitude               distance of the satellite to the body's surface
     *                               in km
     * @param ecentricity            of the orbit (between 0 and 1 for elliptical
     *                               orbits) 0 >= ecentricity || ecentricity >= 1
     * @param trueAnomaly            true anomaly at the initial position in degrees
     *                               between 0 to 360
     * @param longitudeAscendingNode longitude of the ascending node in degrees
     *                               between 0 to 360
     * @param inclination            of the orbit in degrees 0 to 360
     * @param argumentOfPeriapisis   argument of periapsis in degrees between 0 to
     *                               360
     * @return
     */
    public boolean initialiseSatelliteValuesAngles(Body body, String satName, double massOfSatellite,
            double altitude, double ecentricity, double trueAnomaly, double longitudeAscendingNode, double inclination,
            double argumentOfPeriapisis) {
        if (!this.initName(satName, body.getName())) {
            return false;
        }

        this.massOfBody = body.getMass();
        this.updateData(data -> {
            data.mass = massOfSatellite;
            data.altitude = altitude;
        });

        if (0 >= ecentricity || ecentricity >= 1) {
            this.setLatestError("eccentricity not supported " + ecentricity);
            return false;
        }

        double minimumAltitude = Float.MIN_VALUE;

        if (altitude < minimumAltitude) {
            this.setLatestError("initial position is too close to the body: " + altitude);
            return false;
        }

        double distance = (altitude + body.getRadius()) * 1000; // convert distance to center of body from km to m

        MathOrbits.constructSatelliteUsingAngle(this, massOfBody, distance, ecentricity, trueAnomaly,
                longitudeAscendingNode, inclination, argumentOfPeriapisis);

        return MathOrbits.getStaticInfo(body, this);
    }

    /**
     * Sets the satellite's name while accounting for duplicate naming.
     * 
     * @param satName  the satellite's name.
     * @param bodyName the central celestial body's name.
     * @return true if the name is not taken by the central celestial body, false
     *         otherwise.
     */
    private boolean initName(String satName, String bodyName) {
        if (satName.equals(bodyName)) {
            this.setLatestError("Satellite name is the same has the body's name. Not allowed");
            return false;
        } else {
            this.updateData(data -> data.name = satName);
            return true;
        }
    }

    /**
     * Sets the mass of the central celestial body.
     * 
     * @param mass mass in kilograms.
     */
    public synchronized void setMassOfBody(double mass) {
        this.massOfBody = mass;
    }

    /**
     * Sets the time at which to start simulating this satellite's orbit.
     * 
     * @param time in secondes
     */
    public void setInitialTime(double time) {
        this.updateData(data -> data.time0 = time);
    }

    /**
     * Sets the time at which to simulate this satellite's orbit.
     * 
     * @param time in seconds
     */
    public void setCurrentTime(double time) {
        this.updateData(data -> data.currentTime = time);
    }

    /**
     * Sets the latest error this satellite encountered while calculating values.
     * 
     * @param error the error text.
     */
    public synchronized void setLatestError(String error) {
        this.latestError = error;
    }

    /**
     * @return the latest error this satellite encountered while calculating values.
     */
    public synchronized String getLatestError() {
        return this.latestError;
    }

    /**
     * Initialises satellite data that is not time-dependent.
     * 
     * @param body the celestial body around which this satellite orbits.
     * @return true if initialisation is successful, false otherwise.
     */
    public boolean initialiseSatelliteInfo(Body body) {
        return MathOrbits.getStaticInfo(body, this);

    }

    /**
     * Use only if a lot of data feilds are needed. Otherwise, use readData() to
     * prevent making deep copies!
     * Returns a thread-safe copy of all satellite data fields.
     * All Vector3d objects are deep-copied to prevent external modifications.
     * 
     * @return a complete, deep copy of the satellite data.
     */
    public synchronized SatelliteData getData() {
        SatelliteData copy = new SatelliteData();

        // Copy primitive types and strings
        copy.name = this.data.name;
        copy.mass = this.data.mass;
        copy.altitude = this.data.altitude;
        copy.distance = this.data.distance;
        copy.hillRadius = this.data.hillRadius;
        copy.speed = this.data.speed;
        copy.mu = this.data.mu;
        copy.period = this.data.period;
        copy.gravitationalPotentialEnergy = this.data.gravitationalPotentialEnergy;
        copy.kineticEnergy = this.data.kineticEnergy;
        copy.initialTotalEnergy = this.data.initialTotalEnergy;
        copy.totalEnergy = this.data.totalEnergy;
        copy.angularMomentum = this.data.angularMomentum;
        copy.eccentricity = this.data.eccentricity;
        copy.p = this.data.p;
        copy.a = this.data.a;
        copy.radiusOfPeriapsis = this.data.radiusOfPeriapsis;
        copy.radiusOfApoapsis = this.data.radiusOfApoapsis;
        copy.excessSpeed = this.data.excessSpeed;
        copy.meanMotion = this.data.meanMotion;
        copy.meanAnomaly = this.data.meanAnomaly;
        copy.initialMeanAnomaly = this.data.initialMeanAnomaly;
        copy.eccentricAnomaly = this.data.eccentricAnomaly;
        copy.initialEccentricAnomaly = this.data.initialEccentricAnomaly;
        copy.trueAnomaly = this.data.trueAnomaly;
        copy.initialTrueAnomaly = this.data.initialTrueAnomaly;
        copy.inclination = this.data.inclination;
        copy.longitudeOfAscendingNode = this.data.longitudeOfAscendingNode;
        copy.argumentOfPeriapsis = this.data.argumentOfPeriapsis;
        copy.timeSincePeriapsis = this.data.timeSincePeriapsis;
        copy.time0 = this.data.time0;
        copy.currentTime = this.data.currentTime;
        copy.lastTime = this.data.lastTime;

        // Deep copy Vector3d objects to prevent external modifications
        copy.angularMomentumVect = this.data.angularMomentumVect != null ? new Vector3d(this.data.angularMomentumVect)
                : null;
        copy.eccentricityVect = this.data.eccentricityVect != null ? new Vector3d(this.data.eccentricityVect) : null;
        copy.initialVelocity = this.data.initialVelocity != null ? new Vector3d(this.data.initialVelocity) : null;
        copy.currentVelocity = this.data.currentVelocity != null ? new Vector3d(this.data.currentVelocity) : null;
        copy.initialPosition = this.data.initialPosition != null ? new Vector3d(this.data.initialPosition) : null;
        copy.currentPosition = this.data.currentPosition != null ? new Vector3d(this.data.currentPosition) : null;
        copy.lineOfNodesVect = this.data.lineOfNodesVect != null ? new Vector3d(this.data.lineOfNodesVect) : null;

        return copy;
    }

    /**
     * Safely update satellite data within a synchronized block.
     * Use this to avoid multiple lock acquisitions for batch updates.
     * 
     * @param updater function that modifies the data
     */
    public synchronized void updateData(Consumer<SatelliteData> updater) {
        updater.accept(this.data);
    }

    /**
     * Safely read satellite data and compute a result within a synchronized block.
     * Prevents inconsistent reads across multiple fields.
     * 
     * @param reader function that reads from data and returns a result
     * @return the result from the reader function
     */
    public synchronized <T> T readData(Function<SatelliteData, T> reader) {
        return reader.apply(this.data);
    }

    /**
     * @return true if this satellite's simulation is currently running, false
     *         otherwise.
     */
    public synchronized boolean getSimulationRunning() {
        return this.simulationRunning;
    }

    /**
     * Set the simulation's run state.
     * 
     * @param state the simulation's run state.
     */
    public synchronized void setSimulationRunning(boolean state) {
        this.simulationRunning = state;
    }

    /**
     * Updates (only do this once for proper simulation behaviour) the relative info
     * of the satellite (position, velocity, energy, etc) using the current time and
     * the initial conditions.
     * 
     * @return true if calculations were successful, false otherwise.
     */
    public boolean relativeInfoUpdate() {
        boolean res = MathOrbits.getRelativeInfo(this, true);

        if (!res) {
            this.setLatestError(this.getLatestError());
        }

        return res;
    }

    /**
     * Runs this satellite's simulation thread.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {

            if (!this.getSimulationRunning()) {
                this.setSimulationRunning(true);
            }

            boolean res = MathOrbits.getRelativeInfo(this, false);

            if (!res) {
                this.setLatestError(this.getLatestError() + " Simulation stopped for this satellite.");
                this.setSimulationRunning(false);
                Thread.currentThread().interrupt();
            }

            try {
                Thread.sleep(Constant.UPDATE_TIME);
            } catch (InterruptedException e) {
                this.setSimulationRunning(false);
                Thread.currentThread().interrupt();
                break;
            }

        }
        this.setSimulationRunning(false);
    }
}
