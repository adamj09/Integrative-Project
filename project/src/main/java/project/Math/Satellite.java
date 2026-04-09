package project.Math;

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

    /**
     * Initialise the satellite using Keplerian orbital elements.
     * @param satName name (and id) of the satellite
     * @param massOfSatellite mass of the satellite in kg
     * @param bodyName name (and id) of the body that the satellite is orbiting
     * @param massOfBody mass of the body in kg
     * @param distance distance of the satellite to the body in km
     * @param eccentricity eccentricity of the orbit (0 &lt; eccentricity &lt; 1 for elliptical orbits)
     * @param trueAnomaly true anomaly at the initial position in degrees (0 to 360)
     * @param longitudeAscendingNode longitude of the ascending node in degrees (0 to 360)
     * @param inclination inclination of the orbit in degrees (0 to 360)
     * @param argumentOfPeriapsis argument of periapsis in degrees (0 to 360)
     * @return true if an error occurred, false otherwise
     */
    public boolean initialiseSatelliteValuesAngles(
            String satName, double massOfSatellite, String bodyName, double massOfBody,
            double distance, double eccentricity, double trueAnomaly,
            double longitudeAscendingNode, double inclination, double argumentOfPeriapsis) {

        this.data.name = satName;
        this.data.mass = massOfSatellite;
        this.massOfBody = massOfBody;

        if (eccentricity <= 0 || eccentricity >= 1) {
            this.setLatestError("Eccentricity must be between 0 and 1 (exclusive). Got: " + eccentricity);
            this.setatestErrorActive(true);
            return this.activeError;
        }

        // Convert angles from degrees to radians
        double nu       = Math.toRadians(trueAnomaly);
        double bigOmega = Math.toRadians(longitudeAscendingNode);
        double inc      = Math.toRadians(inclination);
        double omega    = Math.toRadians(argumentOfPeriapsis);

        // Convert distance from km to m
        double r = distance * 1000.0;

        // Gravitational parameter of the central body
        double mu = Constant.GRAVITATIONAL_CONSTANT * massOfBody;

        // Semi-latus rectum: p = r * (1 + e * cos(nu))
        double p = r * (1.0 + eccentricity * Math.cos(nu));

        // Angular momentum: h = sqrt(mu * p)
        double h = Math.sqrt(mu * p);

        // Position in PQW (perifocal) frame (meters)
        double rx_PQW = r * Math.cos(nu);
        double ry_PQW = r * Math.sin(nu);

        // Velocity in PQW frame (m/s): v = (mu/h) * [-sin(nu), e + cos(nu), 0]
        double vx_PQW = -(mu / h) * Math.sin(nu);
        double vy_PQW =  (mu / h) * (eccentricity + Math.cos(nu));

        // Standard PQW-to-ECI rotation: R = Rz(Omega) x Rx(inc) x Rz(omega)
        double cosO = Math.cos(bigOmega), sinO = Math.sin(bigOmega);
        double cosi = Math.cos(inc),      sini = Math.sin(inc);
        double coso = Math.cos(omega),    sino = Math.sin(omega);

        double r11 =  cosO * coso - sinO * cosi * sino;
        double r12 = -cosO * sino - sinO * cosi * coso;
        double r21 =  sinO * coso + cosO * cosi * sino;
        double r22 = -sinO * sino + cosO * cosi * coso;
        double r31 =  sini * sino;
        double r32 =  sini * coso;

        double px_ECI = r11 * rx_PQW + r12 * ry_PQW;
        double py_ECI = r21 * rx_PQW + r22 * ry_PQW;
        double pz_ECI = r31 * rx_PQW + r32 * ry_PQW;

        double vx_ECI = r11 * vx_PQW + r12 * vy_PQW;
        double vy_ECI = r21 * vx_PQW + r22 * vy_PQW;
        double vz_ECI = r31 * vx_PQW + r32 * vy_PQW;

        this.data.initialPosition = new Vector3d(px_ECI, py_ECI, pz_ECI);
        this.data.initialVelocity = new Vector3d(vx_ECI, vy_ECI, vz_ECI);

        MathOrbits.getStaticInfo(massOfBody, this);
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
