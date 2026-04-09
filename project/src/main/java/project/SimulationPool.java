package project;

import java.util.HashMap;

import project.Math.Body;
import project.Math.Constant;
import project.Math.Satellite;
import project.Renderer.Renderer;

public class SimulationPool {
    private static HashMap<String, Body> bodies = new HashMap<>();

    public static void load() {
        Body body = new Body("Earth", Constant.EARTH_DEFAULT_MASS, Constant.EARTH_DEFAULT_RADIUS,
                Constant.EARTH_DEFAULT_DISTANCE_TO_SUN);
        body.setTimeScale(1.d);

        Satellite test = new Satellite();
        test.initialiseSatelliteValuesAngles(body, "test", 20,
                300.0, 0.8, 0,
                0, 20, 0);

        body.addSatellite(test);
        bodies.put(body.getName(), body);

        body.startTimeThread();
        body.startSatellites();

        body.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // Wait 1 second before printing
        body.resetTime();
    }

    public static void addBody() {
        // TODO: call this function in the UI code to add a body
    }

    public static void addSatellite(String bodyName) {
        // TODO: call this function in the UI code to add a satellite to a specified
        // body
    }

    public static void setCurrentBody(Renderer renderer, String name) {
        if (bodies.containsKey(name)) {
            renderer.getWorld().setBody(bodies.get(name));
        }
    }
}
