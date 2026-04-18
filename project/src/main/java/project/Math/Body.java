package project.Math;

import java.util.HashMap;

public class Body extends OrbitsTime {
    private String name = "earth";
    private double mass = Constant.EARTH_DEFAULT_MASS; // in kg
    private double radius = Constant.EARTH_DEFAULT_RADIUS; // in km
    private double semiMajorAxis = Constant.EARTH_ORBIT_SEMIMAJOR_AXIS; // in km (distance to the sun, used for the
                                                                           // calculation of the solar radiation
                                                                           // pressure)
    private double eccentricity = Constant.EARTH_ORBIT_ECCENTRICITY;
    private double massOfSun = Constant.SUN_DEFAULT_MASS; // in kg (mass of the sun, used for the calculation of )
    private String latestError = "null";
    private boolean simulationRunning = false;
    private HashMap<String, Satellite> satellites;
    private HashMap<String, Thread> satelliteThreads;

    /**
     * constructor for the body object with earth specifcations
     */
    public Body() {
        satellites = new HashMap<>();
        satelliteThreads = new HashMap<>();
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
        satellites = new HashMap<>();
        satelliteThreads = new HashMap<>();
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
        
        satellites = new HashMap<>();
        satelliteThreads = new HashMap<>();
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
        satellites = new HashMap<>();
        satelliteThreads = new HashMap<>();
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
        // If satellite already exists, return false.
        if(satellites.containsKey(sat.getData().name)) {
            return false;
        }
 
        if (satellites.size() + 1 > Constant.MAXIMUM_NUMBER_OF_SATELITE) {
            this.latestError = "Maximum number of satelite reached!";
            return false;
        } else {
            String name = sat.getData().name;
            this.satellites.put(name, sat);
            return true;
        }
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
        return this.satellites;
    }

    /**
     * remove a satellite of the body
     * 
     * @param name name of the satellite
     * @return true if the satellite is found in the list and revoved. false if the
     *         satellite is not found in the list
     */
    public boolean removeSatellite(String name) {
        if (satellites.containsKey(name)) {
            // Stop the thread if it's running
            if (satelliteThreads.containsKey(name)) {
                Thread thread = satelliteThreads.get(name);
                if (thread.isAlive()) {
                    thread.interrupt();
                }
                satelliteThreads.remove(name);
            }
            satellites.remove(name);
            return true;
        } else {
            return false;
        }
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

    public String getLatestError() {
        return this.latestError;
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

    /**
     * Starts a thread for each satellite in the hash map.
     * Each satellite runs its own simulation calculations.
     */
    public void startSatellites() {
        for (Satellite sat : satellites.values()) {
            String satName = sat.getData().name;
            if (!satelliteThreads.containsKey(satName) || !satelliteThreads.get(satName).isAlive()) {
                Thread thread = new Thread(sat);
                satelliteThreads.put(satName, thread);
                thread.start();
            }
        }
        this.simulationRunning = true;
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
        this.simulationRunning = false;
    }

    /**
     * Stops a specific satellite thread and remove it
     */
    public void stopSatellite(String name) {
        if (satelliteThreads.containsKey(name)) {
            Thread thread = satelliteThreads.get(name);
            if (thread.isAlive()) {
                thread.interrupt();
            }
            satelliteThreads.remove(name);
        }
    }

    /**
     * Start a specific satellite thread
     */
    public void startSatellite(String name) {
        if (satelliteThreads.containsKey(name)) {
            Thread thread = satelliteThreads.get(name);
            if (!thread.isAlive()) {
                thread.start();
            }
        }else {
            Satellite sat = satellites.get(name);
            if (sat != null) {
                Thread thread = new Thread(sat);
                satelliteThreads.put(name, thread);
                thread.start();
            }
        }
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
