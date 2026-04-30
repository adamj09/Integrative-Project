package oms.Math;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a physical celestial body with a great enough mass to have a
 * considerable gravitational pull.
 * 
 * @author Maxime Gauthier
 */
public class Body extends OrbitsTime {
    /**
     * Body data.
     */
    private String name = "earth";
    private double mass = Constant.EARTH_DEFAULT_MASS; // in kg
    private double radius = Constant.EARTH_DEFAULT_RADIUS; // in km
    private double semiMajorAxis = Constant.EARTH_ORBIT_SEMIMAJOR_AXIS; // in km (distance to the sun, used for the
                                                                        // calculation of the solar radiation
                                                                        // pressure)
    private double hillRadius = 1.4714e7; // in km
    private double eccentricity = Constant.EARTH_ORBIT_ECCENTRICITY;
    private double massOfSun = Constant.SUN_DEFAULT_MASS; // in kg (mass of the sun, used for the calculation of )
    private String latestError = "null";
    private boolean simulationRunning = false;
    private ConcurrentHashMap<String, Satellite> satellites;
    private ConcurrentHashMap<String, Thread> satelliteThreads;

    /**
     * constructor for the body object with earth specifcations
     */
    public Body() {
        satellites = new ConcurrentHashMap<>();
        satelliteThreads = new ConcurrentHashMap<>();
    }

    /**
     * constructor for the body object. The mass of the sun is the one for the solar
     * system
     * and distance to the sun is the distance between earth and the sun
     * 
     * @param name   name (and id) of the body
     * @param mass   mass of the body in kg
     * @param radius radius of the body in km
     */
    public Body(String name, double mass, double radius) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;

        this.hillRadius = MathOrbits.hillRadius(this.semiMajorAxis * 1000d, this.eccentricity, mass, this.massOfSun);

        satellites = new ConcurrentHashMap<>();
        satelliteThreads = new ConcurrentHashMap<>();
    }

    /**
     * Constructor for the body object. The mass of the sun is the one for the solar
     * system.
     * 
     * @param name          name (and id) of the body.
     * @param mass          mass of the body in kg.
     * @param radius        radius of the body in km.
     * @param distanceToSun distance of the body to the sun in km.
     * @param eccentricity the eccentricity of the body's orbit around its star.
     */
    public Body(String name, double mass, double radius, double semiMajorAxis, double eccentricity) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;

        this.hillRadius = MathOrbits.hillRadius(semiMajorAxis * 1000d, eccentricity, mass, this.massOfSun);

        satellites = new ConcurrentHashMap<>();
        satelliteThreads = new ConcurrentHashMap<>();
    }

    /**
     * Constructor for the body object.
     * 
     * @param name          name (and id) of the body.
     * @param mass          mass of the body in kg.
     * @param radius        radius of the body in km.
     * @param semiMajorAxis distance of the body to the sun in km.
     * @param eccentricity  the eccentricity of the body's orbit around its star.
     * @param massOfSun     mass of the sun in kg.
     */
    public Body(String name, double mass, double radius, double semiMajorAxis, double eccentricity, double massOfSun) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.massOfSun = massOfSun;

        this.hillRadius = MathOrbits.hillRadius(semiMajorAxis * 1000d, eccentricity, mass, massOfSun);

        satellites = new ConcurrentHashMap<>();
        satelliteThreads = new ConcurrentHashMap<>();
    }

    /**
     * Add the satelitte to the central body and initialise the static info of the
     * satellite object.
     * 
     * @param sat the satellite objet that will be added to the list
     * @return true is the operation was succesful and false if it failed (becasue
     *         the limit of satellite for the body was reached)
     */
    public boolean addSatellite(Satellite sat) {
        String satName = sat.readData(data -> data.name);

        // Check size limit first
        if (satellites.size() >= Constant.MAXIMUM_NUMBER_OF_SATELITE) {
            this.setLatestError("Maximum number of satelite reached!");
            return false;
        }

        // Atomically add if not already present
        Satellite previous = satellites.putIfAbsent(satName, sat);
        return previous == null; // Returns true if added, false if already existed
    }

    /**
     * return value of null does not necessarily indicate that the map contains no
     * mapping for the key; it's also possible that the map explicitly maps the key
     * to null
     * 
     * @param name name of the satellite
     * @return the satelite object
     */
    public Satellite getSatellite(String name) {
        return this.satellites.get(name);
    }

    /**
     * Adds a satellite to this celestial body.
     * 
     * @param satellite satellite to add.
     */
    public void setSatellite(Satellite satellite) {
        this.satellites.put(satellite.getData().name, satellite);
    }

    /**
     * @return all satellites belonging to this celestial body.
     */
    public HashMap<String, Satellite> getSatellites() {
        return new HashMap<>(this.satellites);
    }

    /**
     * Remove a satellite from the body
     * 
     * @param name name of the satellite
     * @return true if the satellite is found in the list and revoved. false if the
     *         satellite is not found in the list
     */
    public boolean removeSatellite(String name) {
        // Stop the thread if it's running (atomically removes from map)
        Thread thread = satelliteThreads.remove(name);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        // Remove satellite atomically
        return satellites.remove(name) != null;
    }

    /**
     * @return name of the body.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the body.
     * 
     * @param name name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return mass of the body in kilograms.
     */
    public double getMass() {
        return mass;
    }

    /**
     * Sets the mass of the body.
     * 
     * @param mass mass of the body in kilograms.
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * @return radius of the body in kilometers.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the body.
     * 
     * @param radius radius of the body in kilometers.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * @return the semi-major axis of the body's orbit in kilometers.
     */
    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    /**
     * Sets the semi-major axis of the body's orbit.
     * 
     * @param semiMajorAxis the semi-major axis of the body's orbit in kilometers.
     */
    public void setSemiMajorAxis(double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
    }

    /**
     * @return the eccentricity of the body's orbit.
     */
    public double getEccentricity() {
        return eccentricity;
    }

    /**
     * Sets the eccentricity of the body's orbit.
     * 
     * @param eccentricity the eccentricity of the body's orbit.
     */
    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    /**
     * @return the mass of the star around which the body orbits in kilograms.
     */
    public double getMassOfSun() {
        return massOfSun;
    }

    /**
     * Sets the mass of the star around which the body orbits.
     * 
     * @param massOfSun the mass of the star around which the body orbits in
     *                  kilograms.
     */
    public void setMassOfSun(double massOfSun) {
        this.massOfSun = massOfSun;
    }

    /**
     * @return the hill radius of the body in kilometers.
     */
    public double getHillRadius() {
        return this.hillRadius;
    }

    /**
     * @return the number of satellites orbiting this body.
     */
    public int getNumberOfSatellites() {
        return this.satellites.size();
    }

    /**
     * Update the hill radius for this object using current values for
     * semi-major axis, eccentricity, mass of sun, and mass of this object
     * 
     */
    public void updateHillRadius() {
        this.hillRadius = MathOrbits.hillRadius(this.semiMajorAxis, this.eccentricity, this.mass, this.mass);
    }

    /**
     * Starts a thread for each satellite in the hash map.
     * Each satellite runs its own simulation calculations.
     */
    public void startSatellites() {
        for (Satellite sat : satellites.values()) {
            String satName = sat.getData().name;
            Thread existingThread = satelliteThreads.get(satName);

            // Only start if no thread exists or the existing one is dead
            if (existingThread == null || !existingThread.isAlive()) {
                Thread thread = new Thread(sat);
                satelliteThreads.put(satName, thread);
                thread.start();
            }
        }
        this.setThreadState(true);
    }

    /**
     * Stops all satellite threads and remove them all
     */
    public void stopSatellites() {
        for (Thread thread : satelliteThreads.values()) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        satelliteThreads.clear();
        this.setThreadState(false);
    }

    /**
     * Stops a specific satellite thread and remove it
     */
    public void stopSatellite(String name) {
        Thread thread = satelliteThreads.remove(name);
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * Start a specific satellite thread
     */
    public void startSatellite(String name) {
        Satellite sat = satellites.get(name);
        if (sat == null) {
            return; // Satellite doesn't exist
        }

        Thread existingThread = satelliteThreads.get(name);
        if (existingThread != null && existingThread.isAlive()) {
            return; // Already running
        }

        // Create and start new thread
        Thread thread = new Thread(sat);
        Thread previous = satelliteThreads.putIfAbsent(name, thread);

        // Only start if we successfully added it (or if the existing thread is dead,
        // replace it)
        if (previous == null || !previous.isAlive()) {
            thread.start();
        }
    }

    /**
     * Updates all satellites' data.
     */
    public void sateliteUpdateInfo() {
        for (Satellite sat : satellites.values()) {
            sat.relativeInfoUpdate();
        }
    }

    /**
     * @return true if all satellites are running, false otherwise.
     */
    public boolean isAllSatelliteSimulationRunning() {
        for (Satellite sat : satellites.values()) {
            if (!sat.getSimulationRunning()) {
                this.setLatestError("Maximum number of satelite reached!");
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether a given satellite is running.
     * 
     * @param name name of the satellite to check.
     * @return true if it is running, false otherwise.
     */
    public boolean getSatelliteSimulationRunning(String name) {
        Satellite sat = satellites.get(name);
        if (sat != null) {
            return sat.getSimulationRunning();
        }
        this.setLatestError("Satellite not found! Name: " + name);
        return false;
    }

    /**
     * Gets the latest error with the satellite's math.
     * 
     * @param name name of the satellite to get error from.
     * @return the latest error with the satellite's math.
     */
    public String getSatelliteLatestError(String name) {
        Satellite sat = satellites.get(name);
        if (sat != null) {
            return sat.getLatestError();
        }
        return "Satellite not found! Name: " + name;
    }

    /**
     * @return whether this body's simulation thread is running.
     */
    public synchronized boolean getThreadState() {
        return this.simulationRunning;
    }

    /**
     * Sets whether this body's simulation is running.
     * 
     * @param state set this to true if we'd like the body's simulation to run,
     *              false otherwise.
     */
    public synchronized void setThreadState(boolean state) {
        this.simulationRunning = state;
    }

    /**
     * Sets the latest error message for this body.
     * 
     * @param error error text.
     */
    private synchronized void setLatestError(String error) {
        this.latestError = error;
    }

    /**
     * @return the latest error message associated with this body.
     */
    public synchronized String getLatestError() {
        return this.latestError;
    }

    /**
     * Override to update all satellites with the current simulation time.
     */
    @Override
    protected void updateSatellitesTime() {
        double currentTime = this.getTimeSeconds();
        for (Satellite sat : satellites.values()) {
            sat.setCurrentTime(currentTime);
        }
    }
}
