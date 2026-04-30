package project.Math;

/**
 * A list of physical constants used for physics calculations.
 * 
 * @author Maxime Gauthier
 */
public class Constant {
    // math orbits constant
    public static final double GRAVITATIONAL_CONSTANT = 6.67430e-11; // m^3 kg^-1 s^-2
    public static final double PRECISON_ECCENTRIC_ANOMALY = 0.001;

    // main body
    public static final int MAXIMUM_NUMBER_OF_SATELITE = 10;
    public static final double SUN_DEFAULT_MASS = 1.989e30; // in kg
    public static final double EARTH_DEFAULT_MASS = 5.972e24; // in kg
    public static final double EARTH_DEFAULT_RADIUS = 6371; // in km
    public static final double AU = 1.495978707e8; // in km
    public static final double EARTH_ORBIT_SEMIMAJOR_AXIS = 1.000001018 * AU; // in km
    public static final double EARTH_ORBIT_ECCENTRICITY = 0.0167086;

    //satellite
    public static final int MINIMUM_ALTITUDE = 160; //in km (minimum distance of the satellite to the body)

    //time
    public static final int UPDATE_TIME = 8; //in miliseconds (120FPS) (time between each calculation of the sitelittes info)
}
