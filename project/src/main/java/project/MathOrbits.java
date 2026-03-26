package project;

import org.joml.Vector3d;
import org.joml.Matrix3d;

public class MathOrbits {
    
    /**
     * Need to have the initial position and velocity vector for the satelite to call this method
     * Add all constant propreties of the satellite to the satellite data object
     * @param massOfCelestialBody 
     * @param satellite
     */
    public static void getStaticInfo(double massOfCelestialBody, Satellite satellite){
    
        Vector3d initialPosition = satellite.getData().initialPosition;
        Vector3d initialVelocity = satellite.getData().initialVelocity;
        
        //gravitational parameter of the central body
        double mu = Constant.GRAVITATIONAL_CONSTANT * massOfCelestialBody;
        satellite.getData().mu = mu;

        //kinetic energy
        double kineticEnergy =  0.5 * initialVelocity.lengthSquared();
        satellite.getData().kineticEnergy = kineticEnergy;
        //gravitational potential energy
        double gravitationalPotEnergy = -mu / satellite.getData().initialPosition.length();
        satellite.getData().gravitationalPotentialEnergy = gravitationalPotEnergy;
        //total energy
        satellite.getData().initialTotalEnergy = kineticEnergy + gravitationalPotEnergy;

        //angular momentum vector
        Vector3d angularMomentumVect = new Vector3d();
        initialPosition.cross(initialVelocity,angularMomentumVect);
        satellite.getData().angularMomentumVect = angularMomentumVect;
        //angular momentum
        double angularMomentum = angularMomentumVect.length();
        satellite.getData().angularMomentum = angularMomentum;

        //eccentricity vector
        
        Vector3d vct1 = new Vector3d();
        initialVelocity.cross(angularMomentumVect,vct1);
        Vector3d vct2 = new Vector3d();
        initialPosition.normalize(vct2);
        vct2.mul(mu); 
        Vector3d eccentricityVect =  (vct1.sub(vct2)).mul(1.0 / mu);
        satellite.getData().eccentricityVect = eccentricityVect;
        //eccentricity
        double eccentricity = eccentricityVect.length();
        satellite.getData().eccentricity = eccentricity;

        if(eccentricity >= 1 || eccentricity <= 0){
            satellite.setLatestError("eccentricity not supported "+eccentricity+" energy "+(kineticEnergy + gravitationalPotEnergy));
            satellite.setatestErrorActive(true);
            return;
        }

        //semi-latus rectum
        double p = Math.pow(angularMomentum,2) / mu;
        satellite.getData().p = p;

        //radius of periapsis
        satellite.getData().radiusOfPeriapsis = p / (1 + eccentricity);
        // TODO point of peiapsis

        //radius of apoapsis
        satellite.getData().radiusOfApoapsis = p / (1 - eccentricity);
        // TODO point of apoapsis

        //semis-major axis
        double a = p / (1 - Math.pow(eccentricity, 2));
        satellite.getData().a = a;

        //period
        double period = 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / mu);
        satellite.getData().period = period;

        //mean motion (n)
        satellite.getData().meanMotion = 2 * Math.PI / period;

        //line of nodes vector
        Vector3d lineOfNodesVect = new Vector3d();
        new Vector3d(0, 0, 1).cross(angularMomentumVect,lineOfNodesVect);
        satellite.getData().lineOfNodesVect = lineOfNodesVect;

        //longitude of ascending node (Ω)
        double longitudeOfAscendingNode;
        if (lineOfNodesVect.length() < 1e-10) {
            // Equatorial orbit, longitude of ascending node is undefined, set to 0
            longitudeOfAscendingNode = 0.0;
        } else {
            double arg = lineOfNodesVect.x / lineOfNodesVect.length();
            arg = Math.max(-1.0, Math.min(1.0, arg));
            longitudeOfAscendingNode = Math.acos(arg); 
            if (lineOfNodesVect.y < 0) {
                longitudeOfAscendingNode = 2 * Math.PI - longitudeOfAscendingNode;
            }
        }
        satellite.getData().longitudeOfAscendingNode = longitudeOfAscendingNode;

        //inclination (i)
        double inclinationArg = angularMomentumVect.z / angularMomentum;
        inclinationArg = Math.max(-1.0, Math.min(1.0, inclinationArg));
        satellite.getData().inclination = Math.acos(inclinationArg);

        //argument of periapsis (ω)
        double argumentOfPeriapsis;
        if (lineOfNodesVect.length() < 1e-10) {
            // Equatorial orbit, argument of periapsis is the angle of eccentricity vector in xy plane
            argumentOfPeriapsis = Math.atan2(eccentricityVect.y, eccentricityVect.x);
            if (argumentOfPeriapsis < 0) {
                argumentOfPeriapsis += 2 * Math.PI;
            }
        } else {
            double arg = lineOfNodesVect.dot(eccentricityVect) / (lineOfNodesVect.length() * eccentricity);
            arg = Math.max(-1.0, Math.min(1.0, arg));
            argumentOfPeriapsis = Math.acos(arg);
            if (eccentricityVect.z < 0) {
                argumentOfPeriapsis = 2 * Math.PI - argumentOfPeriapsis;
            }
        }
        satellite.getData().argumentOfPeriapsis = argumentOfPeriapsis;

        //true anomalay
        double arg = (p - initialPosition.length()) / (eccentricity * initialPosition.length());
        arg = Math.max(-1.0, Math.min(1.0, arg));
        double initialTrueAnomaly = Math.acos(arg);
        if (initialPosition.dot(initialVelocity) < 0) {
            initialTrueAnomaly = 2 * Math.PI - initialTrueAnomaly;
        }
        satellite.getData().initialTrueAnomaly = initialTrueAnomaly;

        // initial eccentric anomaly
        double initialEccentricAnomaly = 2*Math.atan(Math.sqrt((1-eccentricity)/(1+eccentricity))*Math.tan(initialTrueAnomaly/2));
        satellite.getData().initialEccentricAnomaly = initialEccentricAnomaly; 

        // initial mean anomaly
        satellite.getData().initialMeanAnomaly = initialEccentricAnomaly - (eccentricity*Math.sin(initialEccentricAnomaly));

        //distance at t0
        double distance = p/(1+(eccentricity*Math.cos(initialTrueAnomaly)));
        satellite.getData().distance = distance;

        //speed
        satellite.getData().speed = Math.sqrt(mu*((2/distance)-(1/a)));

        //excess velocity
        satellite.getData().excessSpeed = Math.sqrt((2*mu)/distance);

    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    public static void getRelativeInfo(Satellite satellite){
        double currentTime = satellite.getData().currentTime;
        double lastTime = satellite.getData().lastTime;
        if(currentTime == lastTime){
            //System.out.println("current "+currentTime+" last "+lastTime+" rejection");
            return;
        }

        double eccentricity = satellite.getData().eccentricity;
        double a = satellite.getData().a; // semi-major axis
        double mu = satellite.getData().mu;

        double deltaT = satellite.getData().currentTime - satellite.getData().time0; //TODO check what happens when the deltaT is negetives

        // mean anomaly at t2
        double meanAnomaly = satellite.getData().initialMeanAnomaly + (satellite.getData().meanMotion * deltaT);

        // eccentric anomaly at t2
        double eccentricAnomaly = getEccentricAnomaly(meanAnomaly, eccentricity);
        satellite.getData().eccentricAnomaly = eccentricAnomaly;

        // true anomaly at t2
        double trueAnomaly = 2*Math.atan(Math.sqrt((1+eccentricity)/(1-eccentricity))*Math.tan(eccentricAnomaly/2));
        satellite.getData().trueAnomaly = trueAnomaly;
        
        //distance
        double distance = (a*(1-Math.pow(eccentricity, 2)))/(1+(eccentricity*Math.cos(trueAnomaly)));
        satellite.getData().distance = distance;

        // position in 3D space
        Vector3d currentPosition = rotationPQWtoECI(satellite,constructDistancePQWvect(distance,trueAnomaly));
        satellite.getData().currentPosition =  currentPosition;

        //speed
        double speed = Math.sqrt(mu*((2/distance)-(1/a)));
        satellite.getData().speed = speed;

        // velocity in 3D space
        Vector3d currentVelocity = rotationPQWtoECI(satellite,constructVelocityPQWvect(mu,satellite.getData().p,eccentricity,trueAnomaly));
        satellite.getData().currentVelocity = currentVelocity;

        //excessSpeed
        satellite.getData().excessSpeed = Math.sqrt((2*mu)/distance);

        //kinetic energy
        double kineticEnergy =  0.5 * satellite.getData().mass * currentVelocity.lengthSquared();
        satellite.getData().kineticEnergy = kineticEnergy;
        //gravitational potential energy
        double gravitationalPotEnergy = -mu / currentPosition.length();
        satellite.getData().gravitationalPotentialEnergy = gravitationalPotEnergy;
        //total energy
        satellite.getData().totalEnergy = kineticEnergy + gravitationalPotEnergy;

        satellite.getData().lastTime = currentTime;

    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Newton-Raphson Method to solve for the eccentric anomaly 
    private static double getEccentricAnomaly(double meanAnomaly, double eccentricity){
        double finalEccentricAnomaly = 0.0;
        double eccentricAnomaly = meanAnomaly;
        double res = 1.0;
        int maxIterations = 1000;
        int iteration = 0;

        do {
            finalEccentricAnomaly = eccentricAnomaly-((meanAnomaly-eccentricAnomaly+(eccentricity*Math.sin(eccentricAnomaly)))/((eccentricity*Math.cos(eccentricAnomaly))-1));

            res = meanAnomaly-finalEccentricAnomaly+(eccentricity*Math.sin(finalEccentricAnomaly));

            eccentricAnomaly = finalEccentricAnomaly;
            iteration++;
        } while (Math.abs(res) >= Constant.PRECISON_ECCENTRIC_ANOMALY && iteration < maxIterations);

        if (iteration >= maxIterations) {
            System.err.println("Warning: getEccentricAnomaly did not converge after " + maxIterations + " iterations");
        }else{
            //System.out.println("iteration: "+iteration+" res "+res);
        }

        return finalEccentricAnomaly;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d rotationPQWtoECI(Satellite satellite, Vector3d vector){
        double longitudeOfAscendingNode = satellite.getData().longitudeOfAscendingNode; //Ω
        double inclination = satellite.getData().inclination; //i
        double argumentOfPeriapsis = satellite.getData().argumentOfPeriapsis; //ω

        Matrix3d r1 = new Matrix3d(
            1, 0, 0,
            0, Math.cos(longitudeOfAscendingNode), -Math.sin(longitudeOfAscendingNode),
            0, Math.sin(longitudeOfAscendingNode), Math.cos(longitudeOfAscendingNode)
        );

        Matrix3d r2 = new Matrix3d(
            Math.cos(inclination), 0, Math.sin(inclination),
             0, 1, 0,
            -Math.sin(inclination), 0, Math.cos(inclination)
        );

        Matrix3d r3 = new Matrix3d(
            Math.cos(argumentOfPeriapsis), -Math.sin(argumentOfPeriapsis), 0,
            Math.sin(argumentOfPeriapsis), Math.cos(argumentOfPeriapsis), 0,
            0, 0, 1
        );

        Matrix3d rotationMatrix = new Matrix3d();
        r1.mul(r2,rotationMatrix);
        rotationMatrix.mul(r3);
        return rotationMatrix.transform(vector);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d constructDistancePQWvect(double radius, double trueAnomaly){
        double x = radius * Math.cos(trueAnomaly);
        double y = radius * Math.sin(trueAnomaly);

        return new Vector3d(x, y, 0);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //
    private static Vector3d constructVelocityPQWvect(double mu, double p, double eccentricity, double trueAnomaly){
        double x = -Math.sqrt(mu / p) * Math.sin(trueAnomaly);
        double y = Math.sqrt(mu / p) * (eccentricity + Math.cos(trueAnomaly));

        return new Vector3d(x, y, 0);
    }
}
