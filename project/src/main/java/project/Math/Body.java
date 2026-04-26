package project.Math;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maxime Gauthier
 */
public class Body extends OrbitsTime {
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
     * constructor for the body object. The mass of the sun is the one for the solar
     * system
     * 
     * @param name          name (and id) of the body
     * @param mass          mass of the body in kg
     * @param radius        radius of the body in km
     * @param distanceToSun distance of the body to the sun in km
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
     * constructor for the body object
     * 
     * @param name          name (and id) of the body
     * @param mass          mass of the body in kg
     * @param radius        radius of the body in km
     * @param distanceToSun distance of the body to the sun in km
     * @param massOfSun     mass of the sun in kg
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
        return previous == null;  // Returns true if added, false if already existed
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

    public void setSatellite(Satellite satellite) {
        this.satellites.put(satellite.getData().name, satellite);
    }

    public HashMap<String, Satellite> getSatellites() {
        return new HashMap<>(this.satellites);
    }

    /**
     * remove a satellite of the body
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

    // getters and setters for name, mass, and radius
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public void setSemiMajorAxis(double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    public double getMassOfSun() {
        return massOfSun;
    }

    public void setMassOfSun(double massOfSun) {
        this.massOfSun = massOfSun;
    }

    public double getHillRadius() {
        return this.hillRadius;
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
            return;  // Satellite doesn't exist
        }
        
        Thread existingThread = satelliteThreads.get(name);
        if (existingThread != null && existingThread.isAlive()) {
            return;  // Already running
        }
        
        // Create and start new thread
        Thread thread = new Thread(sat);
        Thread previous = satelliteThreads.putIfAbsent(name, thread);
        
        // Only start if we successfully added it (or if the existing thread is dead, replace it)
        if (previous == null || !previous.isAlive()) {
            thread.start();
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    public boolean isAllSatelliteSimulationRunnig(){
        for (Satellite sat : satellites.values()) {
            if (!sat.getSimulationRunning()) {
                this.setLatestError("Maximum number of satelite reached!");
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    public boolean getSatelliteSimulationRunning(String name) {
        Satellite sat = satellites.get(name);
        if(sat != null) {
            return sat.getSimulationRunning();
        }
        this.setLatestError("Satellite not found! Name: " + name);
        return false;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    public String getSatelliteLatestError(String name) {
        Satellite sat = satellites.get(name);
        if(sat != null) {
            return sat.getLatestError();
        }
        return "Satellite not found! Name: " + name;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    public synchronized boolean getThreadState() {
        return this.simulationRunning;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    public synchronized void setThreadState(boolean state) {
        this.simulationRunning = state;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private synchronized void setLatestError(String error) {
        this.latestError = error;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    public synchronized String getLatestError() {
        return this.latestError;
    }

    /**
     * Override to update all satellites with the current simulation time
     */
    @Override
    protected void updateSatellitesTime() {
        double currentTime = this.getTimeSeconds();
        for (Satellite sat : satellites.values()) {
            sat.setCurrentTime(currentTime);
        }
    }
}
