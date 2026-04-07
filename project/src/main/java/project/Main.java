package project;

import project.Math.Body;
import project.Math.Satellite;
import project.Math.SatelliteData;

public class Main {
    // Actual main method is here in order to be able to run the app via
    // commandline.
    public static void main(String[] args) {
       // App.main(args);

        //call this if want to test the satellite calculation with terminal logging
        testSatelliteCalculation();
    }

    /**
     * Test method to verify satellite calculations and time advancement
     */
    private static void testSatelliteCalculation() {
        // Create Earth as the central body (realistic values)
        // Mass of Earth: 5.972e24 kg
        // Radius of Earth: 6371 km
        Body earth = new Body("Earth", 5.972e24, 6371);
        earth.setTimeScale(1);

        // Create a satellite (e.g., simulating a low Earth orbit satellite)
        // International Space Station approximate values
        // Orbital radius: ~6778 km from Earth's center (407 km altitude)
        // Orbital velocity: ~7670 m/s
        Satellite satellite = new Satellite();

        /*
        if(!satellite.initialiseSatelliteValuesVectors(
            "hell", 20,earth.getName(),
            5.972e24,           // mass of Earth
            3000.0, 0, 0,         // position: x=6778km, y=0, z=0 (in km)
            0, 300, 0         // velocity: vx=0, vy=340 m/s, vz=0 (in m/s)
        )) {
            System.err.println("Failed to initialise satellite values");
            return;
        } */

        
        if(!satellite.initialiseSatelliteValuesAngles(
            "hell", 20,earth.getName(),
            5.972e24,           // mass of Earth
            3000.0, 0.8, 0,         
            0, 20, 0     
        )) {
            System.err.println("Failed to initialise satellite values");
            return;
        }

        // Add satellite to Earth
        if (!earth.addStellite(satellite)) {
            System.err.println("Failed to add satellite to Earth");
            return;
        }

        // Print static orbital elements
        SatelliteData data = satellite.getData();
        System.out.println("Static Orbital Elements:");
        System.out.printf("Name: %s%n", data.name);
        System.out.printf("Mass (satellite): %.3e kg%n", data.mass);
        System.out.printf("distance (satellite): %.3e km%n", data.distance/1000);
        System.out.printf("Speed (satellite): %.3e m/s%n", data.mass);
        System.out.printf("mu (central body): %.3e (m^3/s^2)%n", data.mu);
        System.out.printf("Initial Position: (%.3e, %.3e, %.3e) m%n", data.initialPosition.x, data.initialPosition.y, data.initialPosition.z);
        System.out.printf("Initial Velocity: (%.3e, %.3e, %.3e) m/s%n", data.initialVelocity.x, data.initialVelocity.y, data.initialVelocity.z);
        System.out.printf("Kinetic Energy (t0): %.3e J%n", data.kineticEnergy);
        System.out.printf("Gravitational Potential Energy (t0): %.3e J%n", data.gravitationalPotentialEnergy);
        System.out.printf("Total Energy (t0): %.3e J%n", data.initialTotalEnergy);
        System.out.printf("Angular Momentum (|h|): %.3e kg·m^2/s%n", data.angularMomentum);
        System.out.printf("Angular Momentum Vector: (%.3e, %.3e, %.3e)%n", data.angularMomentumVect.x, data.angularMomentumVect.y, data.angularMomentumVect.z);
        System.out.printf("Eccentricity Vector: (%.3e, %.3e, %.3e)%n", data.eccentricityVect.x, data.eccentricityVect.y, data.eccentricityVect.z);
        System.out.printf("Eccentricity: %.6f%n", data.eccentricity);
        System.out.printf("Semi-latus rectum (p): %.3e m%n", data.p);
        System.out.printf("Semi-major axis (a): %.3e m (%.2f km)%n", data.a, data.a / 1000);
        System.out.printf("Periapsis radius: %.3e m (%.2f km)%n", data.radiusOfPeriapsis, data.radiusOfPeriapsis / 1000);
        System.out.printf("Apoapsis radius: %.3e m (%.2f km)%n", data.radiusOfApoapsis, data.radiusOfApoapsis / 1000);
        System.out.printf("Orbital period: %.3e s (%.3f hours)%n", data.period, data.period / 3600);
        System.out.printf("Mean motion (n): %.6e rad/s%n", data.meanMotion);
        System.out.printf("Line of Nodes Vector: (%.3e, %.3e, %.3e)%n", data.lineOfNodesVect.x, data.lineOfNodesVect.y, data.lineOfNodesVect.z);
        System.out.printf("Longitude of Ascending Node: %.6f rad (%.2f deg)%n", data.longitudeOfAscendingNode, Math.toDegrees(data.longitudeOfAscendingNode));
        System.out.printf("Inclination: %.6f rad (%.2f deg)%n", data.inclination, Math.toDegrees(data.inclination));
        System.out.printf("Argument of Periapsis: %.6f rad (%.2f deg)%n", data.argumentOfPeriapsis, Math.toDegrees(data.argumentOfPeriapsis));
        System.out.printf("Initial True Anomaly: %.6f rad (%.2f deg)%n", data.initialTrueAnomaly, Math.toDegrees(data.initialTrueAnomaly));
        System.out.printf("Initial Eccentric Anomaly: %.6f rad%n", data.initialEccentricAnomaly);
        System.out.printf("Initial Mean Anomaly: %.6f rad%n", data.initialMeanAnomaly);
        System.out.printf("Time0: %.3f s | currentTime: %.3f s | lastTime: %.3f s%n", data.time0, data.currentTime, data.lastTime);
        System.out.println();

        System.out.println("=== Satellite Orbital Calculation Test ===");
        System.out.println("Central Body: " + earth.getName());
        System.out.println("Satellite: " + satellite.getData().name);
        System.out.println();


        earth.startTimeThread();
        // Start satellite calculation threads
        earth.startSatellites();
        

        // Start the simulation
        earth.start();

        // Print data every second for 10 seconds
        int printCount = 0;
        int maxPrints = 10;

        //Need to wait a bit before getting the first values because of initialization time of the threads, otherwise the first values will be 0 and it will cause a rejection in the getRelativeInfo method of the satellite
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // Wait 1 second before printing
        earth.resetTime();
        
        long timeNanos = System.nanoTime();

        while (printCount < maxPrints) {
            try {
                
                if(printCount == 3){
                    earth.stop();
                    System.out.println("stop");
                    Thread.sleep(2000);
                    earth.start();
                    System.out.println("start");
                }
                
                if(printCount == 6){
                    earth.resetTime();
                }
                 
                data = satellite.getData();
                double timeSeconds = earth.getTimeSeconds();
                long elapsedNanos = System.nanoTime() - timeNanos;
                double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
                System.out.printf("Time real N: %.2f s | ", elapsedSeconds);
                System.out.printf("Time: %.2f s | ", timeSeconds);
                System.out.printf("Time satelite: %.2f s | ", data.currentTime);
                System.out.printf("Energy: %.2f s | ", data.totalEnergy);
                System.out.printf("TrueAnomaly: %.2f s | ", data.trueAnomaly);
                System.out.printf("Position: (%.2f, %.2f, %.2f) km | ", 
                    data.currentPosition.x / 1000, data.currentPosition.y / 1000, data.currentPosition.z / 1000);
                System.out.printf("Velocity: (%.2f, %.2f, %.2f) m/s%n", 
                    data.currentVelocity.x, data.currentVelocity.y, data.currentVelocity.z);
                
                Thread.sleep(1000); // Wait 1 second before printing
                printCount++;

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        // Stop the simulation
        System.out.println();
        System.out.println("=== Test Complete ===");
        long finalElapsedNanos = System.nanoTime() - timeNanos;
        System.out.println("real time: " + (finalElapsedNanos / 1_000_000_000.0) + " s");
        earth.stop();
        earth.stopSatellites();
        earth.stopTimeThread();
    }
}
