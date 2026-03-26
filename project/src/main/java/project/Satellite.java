package project;

import org.joml.Vector3d;

public class Satellite implements Runnable{
    private SatelliteData data;
    private double massOfBody;
    private String latestError = "null";
    private boolean activeError = false;

//---------------------------------------------------------------------------------------------------------------------------  
// constructors;

    // default constructor
    public Satellite(String name,double massOfSatellite,double massOfBody) {
        this.data = new SatelliteData();
        this.data.name = name;
        this.data.mass = massOfSatellite;
        this.massOfBody = massOfBody;
    }

    /**
     * Construct the satellite using an initial position and velocity (both in 3D space)
     * @param name name of the sattelite
     * @param massOfSatellite mass of the satellite in kg
     * @param massOfBody mass of the body in kg
     * @param px position in x in km
     * @param py position in y in km
     * @param pz position in z in km
     * @param vx velocity in x in m/s
     * @param vy velocity in y in m/s
     * @param vz velocity in z in m/s
     */
    public Satellite(String name,double massOfSatellite,double massOfBody,double px,double py,double pz,double vx,double vy,double vz) {
        this.data = new SatelliteData();
        this.massOfBody = massOfBody;
        this.data.name = name;
        this.data.mass = massOfSatellite;
        this.data.initialPosition = new Vector3d(px*1000,py*1000,pz*1000);
        this.data.initialVelocity = new Vector3d(vx,vy,vz);
    }

//---------------------------------------------------------------------------------------------------------------------------  

    public void setInitialPosition(double x,double y, double z){
        this.getData().initialPosition = new Vector3d(x,y,z); // call getData for thread safe 
    }

    public void setInitialVelocity(double x,double y, double z){
        this.getData().initialVelocity = new Vector3d(x,y,z); // call getData for thread safe 
    }

    public void setMassOfBody(double mass){
        this.massOfBody = mass;
    }

    /**
     * @param time in secondes
     */
    public void setInitialTime(double time){
        this.data.time0 = time;
    }

    /**
     * @param time in seconds
     */
    public void setCurrentTime(double time){
        this.getData().currentTime = time;
    }

    public void setLatestError(String error){
        this.latestError = error;
    }

    public String getLatestError(){
        return this.latestError;
    }

    public void setatestErrorActive(boolean active){
        this.activeError = active;
    }

    public boolean isLatestErrorActive(){
        return this.activeError;
    }

    public boolean initialiseSatelliteInfo(){
        MathOrbits.getStaticInfo(this.massOfBody, this);
        return this.activeError;
    }

    public synchronized SatelliteData getData() {
        return this.data;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            MathOrbits.getRelativeInfo(this);
            
            try {
                Thread.sleep(Constant.UPDATE_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
                 
        }
    }
}
