package project;

import org.joml.Vector3d;

public class SatelliteData {
    public String name;
    public double mass;
    public double distance;
    public double speed;
    public double mu; //gravitational parameter of the CENTRAL BODY
    public double period;
    public double gravitationalPotentialEnergy;
    public double kineticEnergy;
    public double initialTotalEnergy;
    public double totalEnergy; //current
    public Vector3d angularMomentumVect; 
    public double angularMomentum;
    public Vector3d eccentricityVect;
    public double eccentricity;
    public double p; //semi-latus rectum
    public double a; //semi-major axis
    public double radiusOfPeriapsis; //TODO find a way to calculate the point in 3d
    public double radiusOfApoapsis; //TODO find a way to calculate the point in 3d
    public double excessSpeed; 
    public double meanMotion;
    public Vector3d initialVelocity;
    public Vector3d currentVelocity;
    public Vector3d initialPosition;
    public Vector3d currentPosition;
    public double meanAnomaly;
    public double initialMeanAnomaly;
    public double eccentricAnomaly;
    public double initialEccentricAnomaly;
    public double trueAnomaly;
    public double initialTrueAnomaly;
    public double inclination;
    public double longitudeOfAscendingNode;
    public double argumentOfPeriapsis;
    public double timeSincePeriapsis;
    public Vector3d lineOfNodesVect;
    public double time0 = 0; //time at which the satellite is at the initial position and velocity
    public double currentTime; //current time in the simulation
    public double lastTime = -1;
}
