package oms.Physics;

/**
 * A list of physical constants used for physics calculations.
 * 
 * @author Maxime Gauthier
 */
public class Constant {
    /**
     * The gravitational constant (m^3 kg^-1 s^-2).
     */
    public static final double GRAVITATIONAL_CONSTANT = 6.67430e-11;

    /**
     * The precision of the eccentric anomaly.
     */
    public static final double PRECISON_ECCENTRIC_ANOMALY = 0.001;

    /**
     * The maximum number of satellites orbiting a body.
     */
    public static final int MAXIMUM_NUMBER_OF_SATELITE = 10;

    /**
     * The mass of the sun (kg).
     */
    public static final double SUN_DEFAULT_MASS = 1.989e30;

    /**
     * The mass of the Earth (kg).
     */
    public static final double EARTH_DEFAULT_MASS = 5.972e24;

    /**
     * The radius of the Earth at it equator (km).
     */
    public static final double EARTH_DEFAULT_RADIUS = 6371;

    /**
     * The number of kilometers in an astronomical unit.
     */
    public static final double AU = 1.495978707e8;

    /**
     * The Earth's orbit's semi-major axis (km).
     */
    public static final double EARTH_ORBIT_SEMIMAJOR_AXIS = 1.000001018 * AU;

    /**
     * The Earth's orbit's eccentricity.
     */
    public static final double EARTH_ORBIT_ECCENTRICITY = 0.0167086;

    /**
     * Minimum satellite altitude (km) (not applicable to the app itself, used for testing purposes)
     */
    public static final int MINIMUM_ALTITUDE = 160;

    /**
     * The simulation update frequency (ms).
     */
    public static final int UPDATE_TIME = 8;
}
