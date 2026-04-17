package project;

import java.util.HashMap;

import org.joml.Vector3f;

import project.Math.Body;
import project.Math.Constant;
import project.Math.Satellite;
import project.Renderer.World.World;

public class SimulationPool {
    // Currently loaded worlds
    private static HashMap<String, World> worlds = new HashMap<>();

    public static void load() {
        createWorld("Earth");
        addSatellite("Earth");

        
        runWorld("Earth");
    }

    public static void createWorld(String name) {
        // TODO: This is a test body (replace this with body created via UI)
        Body body = new Body("Earth", Constant.EARTH_DEFAULT_MASS, Constant.EARTH_DEFAULT_RADIUS,
                Constant.EARTH_ORBIT_SEMIMAJOR_AXIS, Constant.EARTH_ORBIT_ECCENTRICITY);
        body.setTimeScale(10000.d);

        worlds.put(name, new World(body, new Vector3f(1.f, 1.f, 1.f)));
    }


    public static void addSatellite(String bodyName) {
        World world = worlds.get(bodyName);

        // Test
        if (!world.getBodyObjects().containsKey(bodyName)) {
            // TODO: handle case of non-existent body
        }

        Body body = world.getBody();

        Satellite test = new Satellite();
        test.initialiseSatelliteValuesAngles(body, "test", 20,
                body.getRadius() + 3000.0, 0.8, 0,
                35, 75, 10);

        Satellite test2 = new Satellite();
        test2.initialiseSatelliteValuesAngles(body, "test2", 20,
                body.getRadius() + 100_000.0, 0.8, 0,
                90, 75, 90);

        //world.addSatellite(test, new Vector3f(0.f, 0.f, 1.f));
        world.addSatellite(test2, new Vector3f(0.f, 0.f, 1.f));

        // TODO: call this function in the UI code to add a satellite to a specified
        // body
    }

    public static void runWorld(String worldName) {
        if (!worlds.containsKey(worldName)) {
            // TODO: handle case of non-existent body
        }

        World world = worlds.get(worldName);

        Body body = world.getBody();
        body.startTimeThread();
        body.startSatellites();

        body.start();

        body.resetTime();
    }

    public static World getWorld(String name) {
        if(!worlds.containsKey(name)) {
            return null;
        }
        return worlds.get(name);
    }
}
