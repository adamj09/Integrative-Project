package project.Math;

import org.joml.Vector3d;

public class Satellite implements Runnable{
    private SatelliteData data;
    private double massOfBody;
    private String latestError = "null";

//---------------------------------------------------------------------------------------------------------------------------  
// constructors;

    // default constructor
    public Satellite() {
        this.data = new SatelliteData();
    }

//---------------------------------------------------------------------------------------------------------------------------  
// initialisation of the satellite

    /**
     * initialise the satellite using a 3d initial position vector and a 3d initial velocity vector
     * @param satName name (and id) of the satellite
     * @param massOfSatellite mass of the the satellite in kg
     * @param bodyName name (and id) of the body that the satellite is orbiting
     * @param massOfBody mass of the body in kg
     * @param px position in x in km
     * @param py position in y in km
     * @param pz position in z in km
     * @param vx velocity in x in m/s
     * @param vy velocity in y in m/s
     * @param vz velocity in z in m/s
     * @return false if the initialisation failled / not correct
     */
    public boolean initialiseSatelliteValuesVectors(String satName, double massOfSatellite, String bodyName, double massOfBody,
        double px,double py,double pz,double vx,double vy,double vz
    ){
            
        if(!this.initName(satName, bodyName)){
            return false;
        }

        this.massOfBody = massOfBody;
        this.getData().mass = massOfSatellite;
        this.getData().initialPosition = new Vector3d(px*1000,py*1000,pz*1000);
        this.getData().initialVelocity = new Vector3d(vx,vy,vz);
        
        if(!this.initialiseSatelliteInfo()){
            return false;
        }

        return true;
    }

    /**
     * initialise the satellite using classical orbital elements
     * @param satName name (and id) of the satellite
     * @param massOfSatellite mass of the the satellite in kg
     * @param bodyName name (and id) of the body that the satellite is orbiting
     * @param massOfBody mass of the body in kg
     * @param distance distance of the satellite to the body in km
     * @param ecentricity of the orbit (between 0 and 1 for elliptical orbits)
     * @param trueAnomaly true anomaly at the initial position in degrees
     * @param longitudeAscendingNode longitude of the ascending node in degrees
     * @param inclination of the orbit in degrees
     * @param argumentOfPeriapisis argument of periapsis in degrees
     * @return
     */
    public boolean initialiseSatelliteValuesAngles(String satName, double massOfSatellite, String bodyName, double massOfBody,
        double distance, double ecentricity, double trueAnomaly, double longitudeAscendingNode, double inclination, double argumentOfPeriapisis
    ){
        if(!this.initName(satName, bodyName)){
            return false;
        }
        
        this.massOfBody = massOfBody;
        this.getData().mass = massOfSatellite;

        //normalization of an angle 0 to 360
        trueAnomaly = ((trueAnomaly % 360) + 360) % 360;
        longitudeAscendingNode = ((longitudeAscendingNode % 360) + 360) % 360;
        inclination = ((inclination % 360) + 360) % 360;
        argumentOfPeriapisis = ((argumentOfPeriapisis % 360) + 360) % 360;

        if(0 >= ecentricity || ecentricity >= 1){
            this.latestError = "eccentricity not supported "+ecentricity;
            return false;
        }

        MathOrbits.constructSatelliteUsingAngle(this,massOfBody,distance,ecentricity,trueAnomaly,
            longitudeAscendingNode,inclination,argumentOfPeriapisis);
        
        return MathOrbits.getStaticInfo(massOfBody, this);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    private boolean initName(String satName, String bodyName){
         if(satName.equals(bodyName)){
            latestError = "satellite name is the same has the bodys name. Not allowd";
            return false;
        }else{
            this.getData().name = satName;
            return true;
        }
    }

//---------------------------------------------------------------------------------------------------------------------------  

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

    public boolean initialiseSatelliteInfo(){
        return MathOrbits.getStaticInfo(this.massOfBody, this);
    
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
