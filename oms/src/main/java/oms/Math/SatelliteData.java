package oms.Math;

import org.joml.Vector3d;

/**
 * Physical data for a satellite in orbit.
 * 
 * @author Maxime Gauthier
 */
public class SatelliteData {
    public String name;
    public double mass; // in kilograms
    public double altitude; // in kilometers
    public double distance; // in meters
    public double hillRadius; //hill radius
    public double speed; // in m/s
    public double mu; //gravitational parameter of the CENTRAL BODY
    public double period; // in s
    public double gravitationalPotentialEnergy; // in J
    public double kineticEnergy; // in J
    public double initialTotalEnergy; // in J
    public double totalEnergy; // in J
    public Vector3d angularMomentumVect; // in (kg*m^2)/s) 
    public double angularMomentum; // in ((kg*m^2)/s)
    public Vector3d eccentricityVect;
    public double eccentricity;
    public double p; //semi-latus rectum
    public double a; //semi-major axis
    public double radiusOfPeriapsis; // in m
    public double radiusOfApoapsis; // in m
    public double excessSpeed; // in m/s
    public double meanMotion; // in radians
    public Vector3d initialVelocity; // in m/s
    public Vector3d currentVelocity; // in m/s
    public Vector3d initialPosition; // in m
    public Vector3d currentPosition; // in m
    public double meanAnomaly; // in radians
    public double initialMeanAnomaly; // in radians
    public double eccentricAnomaly; // in radians
    public double initialEccentricAnomaly; // in radians
    public double trueAnomaly; // in radians
    public double initialTrueAnomaly; // in radians
    public double inclination; // in radians
    public double longitudeOfAscendingNode; // in radians
    public double argumentOfPeriapsis; // in radians
    public double timeSincePeriapsis; // in seconds
    public Vector3d lineOfNodesVect; // in meters
    public double time0 = 0; // time at which the satellite is at the initial position and velocity in seconds
    public double currentTime; // current time in the simulation in seconds
    public double lastTime = 0; // in seconds
}
