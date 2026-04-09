package project.Math;

import org.joml.Vector3d;

public class Satellite implements Runnable{
    private SatelliteData data;
    private double massOfBody;
    private String latestError = "null";
    private boolean simulationRunning = false;

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
     * @param body the central body that the satellite is orbiting
     * @param satName name (and id) of the satellite
     * @param massOfSatellite mass of the the satellite in kg
     * @param px position in x in km
     * @param py position in y in km
     * @param pz position in z in km
     * @param vx velocity in x in m/s
     * @param vy velocity in y in m/s
     * @param vz velocity in z in m/s
     * @return false if the initialisation failled / not correct
     */
    public boolean initialiseSatelliteValuesVectors(Body body, String satName, double massOfSatellite,
        double px,double py,double pz,double vx,double vy,double vz
    ){
            
        if(!this.initName(satName, body.getName())){
            return false;
        }

        if(px < Constant.MINIMUM_DISTANCE_TO_BODY){
            latestError = "initial position in x is too close to the body: "+px+" km. Minimum distance is "+Constant.MINIMUM_DISTANCE_TO_BODY+" km";
            return false;
        }
        if(py < Constant.MINIMUM_DISTANCE_TO_BODY){
            latestError = "initial position in y is too close to the body: "+py+" km. Minimum distance is "+Constant.MINIMUM_DISTANCE_TO_BODY+" km";
            return false;
        }
        if(pz < Constant.MINIMUM_DISTANCE_TO_BODY){
            latestError = "initial position in z is too close to the body: "+pz+" km. Minimum distance is "+Constant.MINIMUM_DISTANCE_TO_BODY+" km";
            return false;
        }

        this.massOfBody = body.getMass();
        this.getData().mass = massOfSatellite;
        this.getData().initialPosition = new Vector3d(px*1000,py*1000,pz*1000);
        this.getData().initialVelocity = new Vector3d(vx,vy,vz);
        
        if(!this.initialiseSatelliteInfo(body)){
            return false;
        }

        return true;
    }

    /**
     * initialise the satellite using classical orbital elements
     * @param body the central body that the satellite is orbiting
     * @param satName name (and id) of the satellite
     * @param massOfSatellite mass of the the satellite in kg
     * @param distance distance of the satellite to the body in km 
     * @param ecentricity of the orbit (between 0 and 1 for elliptical orbits) 0 >= ecentricity || ecentricity >= 1
     * @param trueAnomaly true anomaly at the initial position in degrees between 0 to 360
     * @param longitudeAscendingNode longitude of the ascending node in degrees between 0 to 360
     * @param inclination of the orbit in degrees 0 to 360
     * @param argumentOfPeriapisis argument of periapsis in degrees between 0 to 360
     * @return
     */
    public boolean initialiseSatelliteValuesAngles(Body body,String satName, double massOfSatellite,
        double distance, double ecentricity, double trueAnomaly, double longitudeAscendingNode, double inclination, double argumentOfPeriapisis
    ){
        if(!this.initName(satName, body.getName())){
            return false;
        }
        
        this.massOfBody = body.getMass();
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

        if(distance < Constant.MINIMUM_DISTANCE_TO_BODY){
            latestError = "initial position is too close to the body: "+distance+" km. Minimum distance is "+Constant.MINIMUM_DISTANCE_TO_BODY+" km";
            return false;
        }

        distance *= 1000; //convert to meters

        MathOrbits.constructSatelliteUsingAngle(this,massOfBody,distance,ecentricity,trueAnomaly,
            longitudeAscendingNode,inclination,argumentOfPeriapisis);        
        
        return MathOrbits.getStaticInfo(body, this);
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

    public boolean initialiseSatelliteInfo(Body body){
        return MathOrbits.getStaticInfo(body, this);
    
    }

    public synchronized SatelliteData getData() {
        return this.data;
    }

    public synchronized boolean getThreadState(){
        return this.simulationRunning;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if(!this.simulationRunning){
                this.simulationRunning = true;
            }
            boolean res = MathOrbits.getRelativeInfo(this);

            if(!res){
                this.latestError = this.latestError+" Simulation stopped for this satellite.";
                this.simulationRunning = false;
                Thread.currentThread().interrupt();
            }
            
            try {
                Thread.sleep(Constant.UPDATE_TIME);
            } catch (InterruptedException e) {
                this.simulationRunning = false;
                Thread.currentThread().interrupt();
                break;
            }
                 
        }
        this.simulationRunning = false;
    }
}
