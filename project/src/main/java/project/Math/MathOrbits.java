package project.Math;

import org.joml.Matrix3d;
import org.joml.Vector3d;

public class MathOrbits {

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double kineticEnergy(Vector3d velocity) {
        return 0.5 * velocity.lengthSquared();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double gravitationalPotentialEnergy(double mu, Vector3d position) {
        return -mu / position.length();
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d angularMomentum(Vector3d position, Vector3d velocity) {
        Vector3d angularMomentumVect = new Vector3d();
        position.cross(velocity, angularMomentumVect);
        return angularMomentumVect;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d eccentricityVect(Vector3d angularMomentum, Vector3d velocity, Vector3d position,
            double mu) {
        Vector3d vct1 = new Vector3d();
        velocity.cross(angularMomentum, vct1);

        Vector3d vct2 = new Vector3d();
        position.normalize(vct2);

        vct2.mul(mu);
        Vector3d eccentricityVect = (vct1.sub(vct2)).mul(1.0 / mu);

        return eccentricityVect;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double eccentricity(Vector3d eccentricityVect) {
        double ece = eccentricityVect.length();
        if (ece >= 1 || ece <= 0) {
            return Double.NaN;
        }
        return ece;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    /**
     * Calculate the hill radius of the celestial body around which the satellites
     * are orbiting.
     * 
     * @param semiMajorAxis of the celestial body's orbit around the larger body in
     *                      meters (e.g. the sun).
     * @param eccentricity  of the celestial body's orbit around the larger body
     *                      (e.g. the sun).
     * @param lesserMass    mass of the celestial body around which the satellites
     *                      are orbiting in kilograms.
     * @param largerMass    mass of the larger body (e.g. the sun) in kilograms.
     * @return the hill radius in meters. If the larger mass is 0, returns -1.
     */
    public static double hillRadius(double semiMajorAxis, double eccentricity, double lesserMass,
            double largerMass) {
        if (largerMass == 0) {
            return -1;
        }

        return semiMajorAxis * (1 - eccentricity) * Math.cbrt(lesserMass / (3 * largerMass));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double semiLatusRectum(double angularMomentum, double mu) {
        return Math.pow(angularMomentum, 2) / mu;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double apoapsis(double semiLatusRectum, double eccentricity, double hillRadius) {
        double res = semiLatusRectum / (1 - eccentricity);

        if (res > hillRadius) {
            return Double.NaN;
        }
        return res;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    // radius of body in km
    private static double periapsis(double semiLatusRectum, double eccentricity, double radiusOfBody) {
        double res = semiLatusRectum / (1 + eccentricity);

        if (res < (radiusOfBody) * 1000) {
            return Double.NaN;
        }
        return res;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double semiMajorAxis(double semiLatusRectum, double eccentricity) {
        return semiLatusRectum / (1 - Math.pow(eccentricity, 2));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double period(double semiMajorAxis, double mu) {
        return 2 * Math.PI * Math.sqrt(Math.pow(semiMajorAxis, 3) / mu);
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double meanMotion(double semiMajorAxis, double mu) {
        return 2 * Math.PI / period(semiMajorAxis, mu);
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d lineOfNodes(Vector3d angularMomentum) {
        Vector3d lineOfNodesVect = new Vector3d();
        new Vector3d(0, 0, 1).cross(angularMomentum, lineOfNodesVect);
        return lineOfNodesVect;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double longitudeOfAscendingNode(Vector3d lineOfNodes) {
        if (lineOfNodes.length() < 1e-10) {
            // Equatorial orbit, longitude of ascending node is undefined, set to 0
            return 0.0;
        }

        double arg = lineOfNodes.x / lineOfNodes.length();
        arg = Math.max(-1.0, Math.min(1.0, arg));
        double longitudeOfAscendingNode = Math.acos(arg);

        if (lineOfNodes.y < 0) {
            longitudeOfAscendingNode = 2 * Math.PI - longitudeOfAscendingNode;
        }

        return longitudeOfAscendingNode;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double inclination(Vector3d angularMomentum) {
        return Math.acos(Math.max(-1.0, Math.min(1.0, angularMomentum.z / angularMomentum.length())));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double argumentOfPeriapsis(Vector3d lineOfNodes, Vector3d eccentricity) {
        // argument of periapsis (ω)
        double argumentOfPeriapsis;
        if (lineOfNodes.length() < 1e-10) {
            // Equatorial orbit, argument of periapsis is the angle of eccentricity vector
            // in xy plane
            argumentOfPeriapsis = Math.atan2(eccentricity.y, eccentricity.x);
            if (argumentOfPeriapsis < 0) {
                return argumentOfPeriapsis + (2 * Math.PI);
            }
            return argumentOfPeriapsis;
        }

        double arg = lineOfNodes.dot(eccentricity) / (lineOfNodes.length() * eccentricity.length());
        argumentOfPeriapsis = Math.acos(Math.max(-1.0, Math.min(1.0, arg)));

        if (eccentricity.z < 0) {
            return (2 * Math.PI) - argumentOfPeriapsis;
        }

        return argumentOfPeriapsis;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double initialTrueAnomaly(Vector3d position, Vector3d velocity, double eccentricity,
            double semiLatusRectum) {
        double arg = (semiLatusRectum - position.length()) / (eccentricity * position.length());

        double trueAnomaly = Math.acos(Math.clamp(arg, -1.0, 1.0));
        if (position.dot(velocity) < 0) {
            return 2 * Math.PI - trueAnomaly;
        }

        return trueAnomaly;
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double trueAnomaly(double eccentricity, double eccentricAnomaly) {
        return 2 * Math.atan(Math.sqrt((1 + eccentricity) / (1 - eccentricity)) * Math.tan(eccentricAnomaly / 2));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double initialEccentricAnomaly(double eccentricity, double trueAnomaly) {
        return 2 * Math.atan(Math.sqrt((1 - eccentricity) / (1 + eccentricity)) * Math.tan(trueAnomaly / 2));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double initialMeanAnomaly(double eccentricAnomaly, double eccentricity) {
        return eccentricAnomaly - (eccentricity * Math.sin(eccentricAnomaly));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double initialDistance(double semiLatusRectum, double eccentricity, double trueAnomaly) {
        return semiLatusRectum / (1 + (eccentricity * Math.cos(trueAnomaly)));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double distance(double semiMajorAxis, double eccentricity, double trueAnomaly) {
        return (semiMajorAxis * (1 - Math.pow(eccentricity, 2)))
                / (1 + (eccentricity * Math.cos(trueAnomaly)));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double speed(double mu, double distance, double semiMajorAxis) {
        return Math.sqrt(mu * ((2 / distance) - (1 / semiMajorAxis)));
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    //
    private static double excessSpeed(double mu, double distance) {
        return Math.sqrt((2 * mu) / distance);
    }

    /**
     * Need to have the initial position and velocity vector for the satelite to
     * call this method
     * 
     * Adds all constant propreties of the satellite to the satellite data object
     * 
     * @param celestialBody
     * @param satellite
     */
    public static boolean getStaticInfo(Body celestialBody, Satellite satellite) {

        Vector3d initialPosition = satellite.readData(data -> data.initialPosition);
        Vector3d initialVelocity = satellite.readData(data -> data.initialVelocity);

        // Gravitational parameter of the central body.
        double mu = Constant.GRAVITATIONAL_CONSTANT * celestialBody.getMass();
        
        // Initialize energy.
        double kineticEnergy = kineticEnergy(initialVelocity);
        double gravitationalPotentialEnergy = gravitationalPotentialEnergy(mu, initialPosition);
        double initialTotalEnergy = kineticEnergy + gravitationalPotentialEnergy;

        // Angular momentum
        Vector3d angularMomentumVect = angularMomentum(initialPosition, initialVelocity);
        double angularMomentum = angularMomentumVect.length();

        // Eccentricity
        Vector3d eccentricityVect = eccentricityVect(angularMomentumVect, initialVelocity, initialPosition, mu);
        double eccentricity = eccentricity(eccentricityVect);

        if (Double.isNaN(eccentricity)) {
            satellite.setLatestError(
                    "Eccentricity of the satellite is invalid. Need to be bigger than 0 and smaller than 1. Current value: "
                            + eccentricityVect.length());
            return false;
        }

        // Batch update energy and angular momentum together
        satellite.updateData(data -> {
            data.mu = mu;
            data.kineticEnergy = kineticEnergy;
            data.gravitationalPotentialEnergy = gravitationalPotentialEnergy;
            data.initialTotalEnergy = initialTotalEnergy;
            data.angularMomentumVect = angularMomentumVect;
            data.angularMomentum = angularMomentum;
            data.eccentricityVect = eccentricityVect;
            data.eccentricity = eccentricity;
        });

        //hill radius
        celestialBody.updateHillRadius();
        double hillRadius = celestialBody.getHillRadius() * 1000; // in meters

        double p = semiLatusRectum(angularMomentum, mu);

        double radiusOfBody = celestialBody.getRadius(); // in km
        double radiusOfPeriapsis = periapsis(p, eccentricity, radiusOfBody);
        if (Double.isNaN(radiusOfPeriapsis)) {
            satellite.setLatestError(
                    "Radius of periapsis of the satellite is smaller than the minimum distance to the body." +
                            " Need to be bigger than: " + radiusOfBody + "km.");
            return false;
        }

        double radiusOfApoapsis = apoapsis(p, eccentricity, hillRadius);
        if (Double.isNaN(radiusOfApoapsis)) {
            satellite.setLatestError(
                    "Radius of apoapsis of the satellite is larger than the hill radius. The orbit is unstable. Sun is intefering the orbit"
                            +
                            " Need to be smaller than: " + (hillRadius / 1000) + "km.");
            return false;
        }

        // Semi major axis
        double a = semiMajorAxis(p, eccentricity);
        double period = period(a, mu);
        double meanMotion = meanMotion(a, mu);

        Vector3d lineOfNodesVect = lineOfNodes(angularMomentumVect);

        // Batch update orbital elements
        satellite.updateData(data -> {
            data.hillRadius = hillRadius;
            data.p = p;
            data.radiusOfPeriapsis = radiusOfPeriapsis;
            data.radiusOfApoapsis = radiusOfApoapsis;
            data.a = a;
            data.period = period;
            data.meanMotion = meanMotion;
            data.lineOfNodesVect = lineOfNodesVect;
        });

        // Anomalies
        double trueAnomaly = initialTrueAnomaly(initialPosition, initialVelocity, eccentricity, p);
        double eccentricAnomaly = initialEccentricAnomaly(eccentricity, trueAnomaly);
        double meanAnomaly = initialMeanAnomaly(eccentricAnomaly, eccentricity);

        double distance = initialDistance(p, eccentricity, trueAnomaly);

        double speed = speed(mu, distance, a);
        double excessSpeed = excessSpeed(mu, distance);

        // Batch update anomalies, distance, and speed together
        satellite.updateData(data -> {
            data.trueAnomaly = trueAnomaly;
            data.eccentricAnomaly = eccentricAnomaly;
            data.meanAnomaly = meanAnomaly;
            data.initialTrueAnomaly = trueAnomaly;
            data.initialEccentricAnomaly = eccentricAnomaly;
            data.initialMeanAnomaly = meanAnomaly;
            data.distance = distance;
            data.speed = speed;
            data.excessSpeed = excessSpeed;
        });

        //data.longitudeOfAscendingNode = longitudeOfAscendingNode(data.lineOfNodesVect);
        //data.inclination = inclination(data.angularMomentumVect);
        //data.argumentOfPeriapsis = argumentOfPeriapsis(data.lineOfNodesVect, data.eccentricityVect);

        return true;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    public static boolean getRelativeInfo(Satellite satellite ,boolean forceCalculation) {
        
        double currentTime = satellite.readData(data -> data.currentTime);
        double lastTime = satellite.readData(data -> data.lastTime);

        if(!forceCalculation){
            if (currentTime == lastTime) {
                return true;
            }
        }
        
        double eccentricity = satellite.readData(data -> data.eccentricity);
        double a = satellite.readData(data -> data.a);
        double mu = satellite.readData(data -> data.mu);
        double longitudeOfAscendingNode = satellite.readData(data -> data.longitudeOfAscendingNode);
        double inclination = satellite.readData(data -> data.inclination);
        double argumentOfPeriapsis = satellite.readData(data -> data.argumentOfPeriapsis);


        // mean anomaly at t2
        double meanAnomaly = satellite.readData(data -> data.initialMeanAnomaly) + (satellite.readData(data -> data.meanMotion) * (currentTime - satellite.readData(data -> data.time0)));

        // eccentric anomaly at t2
        double eccentricAnomaly = eccentricAnomaly(meanAnomaly, eccentricity);
        
        // true anomaly at t2
        double trueAnomaly = trueAnomaly(eccentricity, eccentricAnomaly);
        
        // distance
        double distance = distance(a, eccentricity, trueAnomaly);
        
        // Batch update anomalies and distance together to maintain consistency
        satellite.updateData(data -> {
            data.eccentricAnomaly = eccentricAnomaly;
            data.trueAnomaly = trueAnomaly;
            data.distance = distance;
        });
        Vector3d currentPosition = rotationPQWtoECI(longitudeOfAscendingNode, inclination,
                argumentOfPeriapsis)
                .transform(constructDistancePQWvect(distance, trueAnomaly));

        // speed
        double speed = speed(mu, distance, a);

        // velocity in 3D space
        Vector3d currentVelocity = rotationPQWtoECI(longitudeOfAscendingNode, inclination,
                argumentOfPeriapsis)
                .transform(constructVelocityPQWvect(mu, satellite.readData(data -> data.p), eccentricity, trueAnomaly));

        // excessSpeed
        double excessSpeed = excessSpeed(mu, distance);

        // kinetic energy
        double kineticEnergy = kineticEnergy(currentVelocity);
        // gravitational potential energy
        double gravitationalPotentialEnergy = gravitationalPotentialEnergy(mu, currentPosition);
        // total energy
        double totalEnergy = kineticEnergy + gravitationalPotentialEnergy;

        // Batch update position, velocity, and energy together for consistency
        satellite.updateData(data -> {
            data.currentPosition = currentPosition;
            data.speed = speed;
            data.currentVelocity = currentVelocity;
            data.excessSpeed = excessSpeed;
            data.kineticEnergy = kineticEnergy;
            data.gravitationalPotentialEnergy = gravitationalPotentialEnergy;
            data.totalEnergy = totalEnergy;
            data.lastTime = currentTime;
        });

        return true;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    public static void constructSatelliteUsingAngle(Satellite satellite, double massOfCelestialBody,
            double distance, double eccentricity, double trueAnomaly,
            double longitudeAscendingNode, double inclination, double argumentOfPeriapsis) {

        double p = distance * (1 + eccentricity * Math.cos(trueAnomaly));
        double mu = Constant.GRAVITATIONAL_CONSTANT * massOfCelestialBody;

        double longitudeOfAscendingNodeRad = Math.toRadians(longitudeAscendingNode);
        double inclinationRad = Math.toRadians(inclination);
        double argumentOfPeriapsisRad = Math.toRadians(argumentOfPeriapsis);

        satellite.updateData(data -> data.longitudeOfAscendingNode = longitudeOfAscendingNodeRad);
        satellite.updateData(data -> data.inclination = inclinationRad);
        satellite.updateData(data -> data.argumentOfPeriapsis = argumentOfPeriapsisRad);

        Vector3d distanceECI = rotationPQWtoECI(longitudeOfAscendingNodeRad, inclinationRad,
                argumentOfPeriapsisRad).transform(constructDistancePQWvect(distance, trueAnomaly));
        Vector3d velocityECI = rotationPQWtoECI(longitudeOfAscendingNodeRad, inclinationRad,
                argumentOfPeriapsisRad)
                .transform(constructVelocityPQWvect(mu, p, eccentricity, trueAnomaly));

        satellite.updateData(data -> data.initialPosition = new Vector3d(distanceECI.x, distanceECI.y, distanceECI.z));
        satellite.updateData(data -> data.initialVelocity = new Vector3d(velocityECI.x, velocityECI.y, velocityECI.z));
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Newton-Raphson Method to solve for the eccentric anomaly
    private static double eccentricAnomaly(double meanAnomaly, double eccentricity) {
        double finalEccentricAnomaly = 0.0;
        double eccentricAnomaly = meanAnomaly;
        double res = 1.0;
        int maxIterations = 1000;
        int iteration = 0;

        do {
            finalEccentricAnomaly = eccentricAnomaly
                    - ((meanAnomaly - eccentricAnomaly + (eccentricity * Math.sin(eccentricAnomaly)))
                            / ((eccentricity * Math.cos(eccentricAnomaly)) - 1));

            res = meanAnomaly - finalEccentricAnomaly + (eccentricity * Math.sin(finalEccentricAnomaly));

            eccentricAnomaly = finalEccentricAnomaly;
            iteration++;
        } while (Math.abs(res) >= Constant.PRECISON_ECCENTRIC_ANOMALY && iteration < maxIterations);

        if (iteration >= maxIterations) {
            System.err.println("Warning: getEccentricAnomaly did not converge after " + maxIterations + " iterations");
        } else {
            // System.out.println("iteration: "+iteration+" res "+res);
        }

        return finalEccentricAnomaly;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    public static Matrix3d rotationPQWtoECI(double longitudeOfAscendingNode, double inclination,
            double argumentOfPeriapsis) {

        double cosLongitudeOfAscendingNode = Math.cos(longitudeOfAscendingNode);
        double sinLongitudeOfAscendingNode = Math.sin(longitudeOfAscendingNode);
        double cosInclination = Math.cos(inclination);
        double sinInclination = Math.sin(inclination);
        double cosArgumentOfPeriapsis = Math.cos(argumentOfPeriapsis);
        double sinArgumentOfPeriapsis = Math.sin(argumentOfPeriapsis);

        double m00 = cosLongitudeOfAscendingNode * cosArgumentOfPeriapsis
                - sinLongitudeOfAscendingNode * cosInclination * sinArgumentOfPeriapsis;
        double m01 = -cosLongitudeOfAscendingNode * sinArgumentOfPeriapsis
                - sinLongitudeOfAscendingNode * cosInclination * cosArgumentOfPeriapsis;
        double m02 = sinLongitudeOfAscendingNode * sinInclination;

        double m10 = sinLongitudeOfAscendingNode * cosArgumentOfPeriapsis
                + cosLongitudeOfAscendingNode * cosInclination * sinArgumentOfPeriapsis;
        double m11 = -sinLongitudeOfAscendingNode * sinArgumentOfPeriapsis
                + cosLongitudeOfAscendingNode * cosInclination * cosArgumentOfPeriapsis;
        double m12 = -cosLongitudeOfAscendingNode * sinInclination;

        double m20 = sinInclination * sinArgumentOfPeriapsis;
        double m21 = sinInclination * cosArgumentOfPeriapsis;
        double m22 = cosInclination;

        Matrix3d rotation = new Matrix3d(
                m00, m01, m02,
                m10, m11, m12,
                m20, m21, m22);

        return rotation;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d constructDistancePQWvect(double radius, double trueAnomaly) {
        double x = radius * Math.cos(trueAnomaly);
        double y = radius * Math.sin(trueAnomaly);

        return new Vector3d(x, y, 0);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d constructVelocityPQWvect(double mu, double p, double eccentricity, double trueAnomaly) {
        double x = -Math.sqrt(mu / p) * Math.sin(trueAnomaly);
        double y = Math.sqrt(mu / p) * (eccentricity + Math.cos(trueAnomaly));

        return new Vector3d(x, y, 0);
    }
}
