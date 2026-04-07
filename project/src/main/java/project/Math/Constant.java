package project.Math;

public class Constant {
    // math orbits constant
    public static final double GRAVITATIONAL_CONSTANT = 6.67430e-11; // m^3 kg^-1 s^-2
    public static final double PRECISON_ECCENTRIC_ANOMALY = 0.001;

    // main body
    public static final int MAXIMUM_NUMBER_OF_SATELITE = 10;

    //satellite
    public static final int MINIMUM_DISTANCE_TO_BODY = 160; //in km (minimum distance of the satellite to the body)

    //time
    public static final int UPDATE_TIME = 8; //in miliseconds (120FPS) (time between each calculation of the sitelittes info)
}
