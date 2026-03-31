package project.Math;

import java.util.HashMap;

public class Body extends OrbitsTime{
    private String name;
    private double mass;
    private double radius;
    private HashMap<String, Satellite> satellites;
    private HashMap<String, Thread> satelliteThreads;

    public Body(String name, double mass, double radius){
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        satellites = new HashMap<>();
        satelliteThreads = new HashMap<>();
    }

    /**
     * add the satelitte to the central body and initialise the static info of the satellite object
     * @param sat the satellite objet that will be added to the list
     * @return true is the operation was succesful and false if it failed (becasue the limit of satellite for the body was reached)
     */
    public boolean addStellite(Satellite sat){
        if(satellites.size()+1 > Constant.MAXIMUM_NUMBER_OF_SATELITE){
            return false;
        }else{
            String name = sat.getData().name;
            boolean prob = sat.initialiseSatelliteInfo(); // initialise info
            if(prob){
                System.out.println("Problemo!!: "+sat.getLatestError());
                return false;
            }
            this.satellites.put(name, sat);
            return true;
        }
    }

    /**
     * return value of null does not necessarily indicate that the map contains no mapping for the key; it's also possible that the map explicitly maps the key to null
     * @param name name of the satellite
     * @return the satelite object
     */
    public Satellite getSatellite(String name){
       return this.satellites.get(name);
    }

    /**
     * remove a satellite of the body
     * @param name name of the satellite
     * @return true if the satellite is found in the list and revoved. false if the satellite is not found in the list
     */
    public boolean removeSatelitte(String name){
        if(satellites.containsKey(name)){
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
        }else{
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


    /**
     * Starts a thread for each satellite in the hash map.
     * Each satellite runs its own simulation calculations.
     */
    public void startSatellites(){
        for (Satellite sat : satellites.values()) {
            String satName = sat.getData().name;
            if (!satelliteThreads.containsKey(satName) || !satelliteThreads.get(satName).isAlive()) {
                Thread thread = new Thread(sat);
                satelliteThreads.put(satName, thread);
                thread.start();
            }
        }
    }

    /**
     * Stops all satellite threads
     */
    public void stopSatellites(){
        for (Thread thread : satelliteThreads.values()) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        satelliteThreads.clear();
    }

    /**
     * Override to update all satellites with the current simulation time
     */
    @Override
    protected void updateSatellitesTime(){
        double currentTime = this.getTimeSeconds();
        for (Satellite sat : satellites.values()) {
            sat.setCurrentTime(currentTime);
        }
    }
}
