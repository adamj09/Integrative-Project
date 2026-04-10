package project.Math;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Matrix3d;
import org.joml.Matrix4d;

public class MathOrbits {

    private static double kineticEnergy(Vector3d velocity) {
        return 0.5 * velocity.lengthSquared();
    }

    private static double gravitationalPotentialEnergy(double mu, Vector3d position) {
        return -mu / position.length();
    }

    private static Vector3d angularMomentum(Vector3d position, Vector3d velocity) {
        Vector3d angularMomentumVect = new Vector3d();
        position.cross(velocity, angularMomentumVect);
        return angularMomentumVect;
    }

    private static Vector3d eccentricity(Vector3d angularMomentum, Vector3d velocity, Vector3d position, double mu) {
        Vector3d vct1 = new Vector3d();
        velocity.cross(angularMomentum, vct1);

        Vector3d vct2 = new Vector3d();
        position.normalize(vct2);

        vct2.mul(mu);
        Vector3d eccentricityVect = (vct1.sub(vct2)).mul(1.0 / mu);

        double eccentricity = eccentricityVect.length();

        if (eccentricity >= 1 || eccentricity <= 0) {
            // TODO: handle with exception
            return null;
        }

        return eccentricityVect;
    }

    private static double hillRadius(double distanceToStar, double eccentricity, double centralBodyMass,
            double starMass) {
        if (starMass == 0) {
            return -1;
        }

        return distanceToStar * (1 - eccentricity) * Math.cbrt(centralBodyMass / (3 * starMass)) * 1000.d;
    }

    private static double semiLatusRectum(double angularMomentum, double mu) {
        return Math.pow(angularMomentum, 2) / mu;
    }

    private static double apoapsis(double semiLatusRectum, double eccentricity) {
        return semiLatusRectum / (1 + eccentricity);
    }

    private static double periapsis(double semiLatusRectum, double eccentricity) {
        return semiLatusRectum / (1 - eccentricity);
    }

    private static double semiMajorAxis(double semiLatusRectum, double eccentricity) {
        return semiLatusRectum / (1 - Math.pow(eccentricity, 2));
    }

    private static double period(double semiMajorAxis, double mu) {
        return 2 * Math.PI * Math.sqrt(Math.pow(semiMajorAxis, 3) / mu);
    }

    private static double meanMotion(double semiMajorAxis, double mu) {
        return 2 * Math.PI / period(semiMajorAxis, mu);
    }

    private static Vector3d lineOfNodes(Vector3d angularMomentum) {
        Vector3d lineOfNodesVect = new Vector3d();
        new Vector3d(0, 0, 1).cross(angularMomentum, lineOfNodesVect);
        return lineOfNodesVect;
    }

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

    private static double inclination(Vector3d angularMomentum) {
        return Math.acos(Math.max(-1.0, Math.min(1.0, angularMomentum.z / angularMomentum.length())));
    }

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

    private static double initialTrueAnomaly(Vector3d position, Vector3d velocity, double eccentricity,
            double semiLatusRectum) {
        double arg = (semiLatusRectum - position.length()) / (eccentricity * position.length());

        double trueAnomaly = Math.acos(Math.max(-1.0, Math.min(1.0, arg)));
        if (position.dot(velocity) < 0) {
            return 2 * Math.PI - trueAnomaly;
        }

        return trueAnomaly;
    }

    private static double trueAnomaly(double eccentricity, double eccentricAnomaly) {
        return 2 * Math.atan(Math.sqrt((1 + eccentricity) / (1 - eccentricity)) * Math.tan(eccentricAnomaly / 2));
    }

    private static double initialEccentricAnomaly(double eccentricity, double trueAnomaly) {
        return 2 * Math.atan(Math.sqrt((1 - eccentricity) / (1 + eccentricity)) * Math.tan(trueAnomaly / 2));
    }

    private static double initialMeanAnomaly(double eccentricAnomaly, double eccentricity) {
        return eccentricAnomaly - (eccentricity * Math.sin(eccentricAnomaly));
    }

    private static double initialDistance(double semiLatusRectum, double eccentricity, double trueAnomaly,
            double hillRadius) {
        double distance = semiLatusRectum / (1 + (eccentricity * Math.cos(trueAnomaly)));

        if (distance > hillRadius) {
            // TODO: exception if distance is larger than hill radius
            return -1;
        }

        return distance;
    }

    private static double distance(double semiMajorAxis, double eccentricity, double trueAnomaly, double hillRadius) {
        double distance = (semiMajorAxis * (1 - Math.pow(eccentricity, 2)))
                / (1 + (eccentricity * Math.cos(trueAnomaly)));

        if (distance > hillRadius) {
            // TODO: exception if distance is larger than hill radius
            return -1;
        }

        return distance;
    }

    private static double speed(double mu, double distance, double semiMajorAxis) {
        return Math.sqrt(mu * ((2 / distance) - (1 / semiMajorAxis)));
    }

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
        SatelliteData data = satellite.getData();

        Vector3d initialPosition = data.initialPosition;
        Vector3d initialVelocity = data.initialVelocity;

        // Gravitational parameter of the central body.
        data.mu = Constant.GRAVITATIONAL_CONSTANT * celestialBody.getMass();

        // Initialize energy.
        data.kineticEnergy = kineticEnergy(initialVelocity);
        data.gravitationalPotentialEnergy = gravitationalPotentialEnergy(data.mu, initialPosition);
        data.initialTotalEnergy = data.kineticEnergy + data.gravitationalPotentialEnergy;

        data.angularMomentumVect = angularMomentum(initialPosition, initialVelocity);
        data.angularMomentum = data.angularMomentumVect.length();

        data.eccentricityVect = eccentricity(data.angularMomentumVect, initialVelocity, initialPosition, data.mu);
        data.eccentricity = data.eccentricityVect.length();

        data.maximumDistanceToBody = hillRadius(celestialBody.getDistanceToSun(), data.eccentricity,
                celestialBody.getMass(), celestialBody.getMassOfSun());

        data.p = semiLatusRectum(data.angularMomentum, data.mu);
        data.radiusOfPeriapsis = periapsis(data.p, data.eccentricity);
        data.radiusOfApoapsis = apoapsis(data.p, data.eccentricity);

        data.a = semiMajorAxis(data.p, data.eccentricity);
        data.period = period(data.a, data.mu);
        data.meanMotion = meanMotion(data.a, data.mu);

        data.lineOfNodesVect = lineOfNodes(data.angularMomentumVect);

        // Angles describing the orbital plane
        data.longitudeOfAscendingNode = longitudeOfAscendingNode(data.lineOfNodesVect);
        data.inclination = inclination(data.angularMomentumVect);
        data.argumentOfPeriapsis = argumentOfPeriapsis(data.lineOfNodesVect, data.eccentricityVect);

        // Anomalies
        data.trueAnomaly = initialTrueAnomaly(initialPosition, initialVelocity, data.eccentricity, data.p);
        data.eccentricAnomaly = initialEccentricAnomaly(data.eccentricity, data.trueAnomaly);
        data.meanAnomaly = initialMeanAnomaly(data.eccentricAnomaly, data.eccentricity);

        data.distance = initialDistance(data.p, data.eccentricity, data.trueAnomaly, data.maximumDistanceToBody);

        // speed
        data.speed = speed(data.mu, data.distance, data.a);

        // excess velocity
        data.excessSpeed = excessSpeed(data.mu, data.distance);

        return true;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    public static boolean getRelativeInfo(Satellite satellite) {
        SatelliteData data = satellite.getData();

        double currentTime = data.currentTime;
        double lastTime = data.lastTime;
        if (currentTime == lastTime) {
            return true;
        }

        // mean anomaly at t2
        double meanAnomaly = data.initialMeanAnomaly + (data.meanMotion * (data.currentTime - data.time0));

        // eccentric anomaly at t2
        data.eccentricAnomaly = eccentricAnomaly(meanAnomaly, data.eccentricity);

        // true anomaly at t2
        data.trueAnomaly = trueAnomaly(data.eccentricity, data.eccentricAnomaly);

        // distance
        data.distance = distance(data.a, data.eccentricity, data.trueAnomaly, data.maximumDistanceToBody);

        // position in 3D space
        Vector4d position = rotationPQWtoECI(data.longitudeOfAscendingNode, data.inclination,
                data.argumentOfPeriapsis)
                .transform(new Vector4d(constructDistancePQWvect(data.distance, data.trueAnomaly), 0));
        data.currentPosition = new Vector3d(position.x, position.y, position.z);

        // speed
        data.speed = speed(data.mu, data.distance, data.a);

        // velocity in 3D space
        Vector4d velocity = rotationPQWtoECI(data.longitudeOfAscendingNode, data.inclination,
                data.argumentOfPeriapsis)
                .transform(new Vector4d(constructVelocityPQWvect(data.mu, data.p, data.eccentricity, data.trueAnomaly),
                        0));
        data.currentVelocity = new Vector3d(velocity.x, velocity.y, velocity.z);

        // excessSpeed
        data.excessSpeed = excessSpeed(data.mu, data.distance);

        // kinetic energy
        data.kineticEnergy = kineticEnergy(data.currentVelocity);
        // gravitational potential energy
        data.gravitationalPotentialEnergy = gravitationalPotentialEnergy(data.mu, data.currentPosition);
        // total energy
        data.totalEnergy = data.kineticEnergy + data.gravitationalPotentialEnergy;

        data.lastTime = currentTime;

        return true;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    public static void constructSatelliteUsingAngle(Satellite satellite, double massOfCelestialBody,
            double distance, double eccentricity, double trueAnomaly,
            double longitudeAscendingNode, double inclination, double argumentOfPeriapsis) {

        SatelliteData data = satellite.getData();

        double p = distance + (distance * eccentricity * Math.cos(trueAnomaly));
        double mu = Constant.GRAVITATIONAL_CONSTANT * massOfCelestialBody;

        data.longitudeOfAscendingNode = Math.toRadians(longitudeAscendingNode);
        data.inclination = Math.toRadians(inclination);
        data.argumentOfPeriapsis = Math.toRadians(argumentOfPeriapsis);

        Vector4d distanceECI = rotationPQWtoECI(data.longitudeOfAscendingNode, data.inclination,
                data.argumentOfPeriapsis).transform(new Vector4d(constructDistancePQWvect(distance, trueAnomaly), 0));
        Vector4d velocityECI = rotationPQWtoECI(data.longitudeOfAscendingNode, data.inclination,
                data.argumentOfPeriapsis)
                .transform(new Vector4d(constructVelocityPQWvect(mu, p, eccentricity, trueAnomaly), 0));

        data.initialPosition = new Vector3d(distanceECI.x, distanceECI.y, distanceECI.z);
        data.initialVelocity = new Vector3d(velocityECI.x, velocityECI.y, velocityECI.z);
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
    public static Matrix4d rotationPQWtoECI(double longitudeOfAscendingNode, double inclination,
            double argumentOfPeriapsis) {

        // TODO: fix these rotations.

        // If we're using a Y up, X right, Z forward coordinate system, this should be
        // along the Y axis.
        Matrix4d xRotation = new Matrix4d(
                1, 0, 0, 0,
                0, Math.cos(inclination), Math.sin(inclination), 0,
                0, -Math.sin(inclination), Math.cos(inclination), 0,
                0, 0, 0, 1);

        // NOTICE: Inclination rotation should (probably) be done around the line of
        // nodes vector, though this is acceptable since we assume planets don't have
        // tilt. If we're using a Y up, X right, Z forward coordinate system, this
        // should be
        // along the X axis.
        Matrix4d yRotation = new Matrix4d(
                Math.cos(longitudeOfAscendingNode), 0, -Math.sin(longitudeOfAscendingNode), 0,
                0, 1, 0, 0,
                Math.sin(longitudeOfAscendingNode), 0, Math.cos(longitudeOfAscendingNode), 0,
                0, 0, 0, 1);

        // NOTICE: This is wrong because the argument of periapsis rotation is being
        // done around the absolute z-axis, assuming a Z up coordinate system (in
        // world-space, < 0, 1, 0 >), rather than
        // around the orbit plane's normal vector. The orbital plane's normal vector
        // should be the same direction as the angular momentum vector (make sure to
        // normalize this vector when using as an axis of rotation).

        Matrix4d zRotation = new Matrix4d(
                Math.cos(argumentOfPeriapsis), 0, -Math.sin(argumentOfPeriapsis), 0,
                0, 1, 0, 0,
                Math.sin(argumentOfPeriapsis), 0, Math.cos(argumentOfPeriapsis), 0,
                0, 0, 0, 1);

        // Matrix4d zRotation = new Matrix4d(
        //         Math.cos(argumentOfPeriapsis), -Math.sin(argumentOfPeriapsis), 0, 0,
        //         Math.sin(argumentOfPeriapsis), Math.cos(argumentOfPeriapsis), 0, 0,
        //         0, 0, 1, 0,
        //         0, 0, 0, 1);

        Matrix4d rotationMatrix = new Matrix4d();
        xRotation.mul(yRotation, rotationMatrix);
        rotationMatrix.mul(zRotation);

        return rotationMatrix;
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
