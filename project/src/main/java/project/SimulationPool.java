package project;

import java.util.HashMap;

import org.joml.Vector3f;

import project.Math.Body;
import project.Renderer.Renderer;
import project.Renderer.World.World;

public class SimulationPool {
    // Currently loaded worlds
    private Renderer renderer;
    private HashMap<String, World> worlds = new HashMap<>();
    private String currentWorld;

    public SimulationPool(Renderer renderer) {
        this.currentWorld = "";
        this.renderer = renderer;
    }

    public void load() {
        runWorld("Earth");
    }

    public void createWorld(String name, Body body, Vector3f color) {
        body.setTimeScale(1000); // TODO: add time scale adjustments
        worlds.put(name, new World(body, color));
    }

    public void addWorld(World world) {
        worlds.put(world.getName(), world);
    }

    public void runWorld(String worldName) {
        if (!worlds.containsKey(worldName)) {
            return;
        }

        // Stop currently running world.
        if (!currentWorld.isEmpty()) {
            worlds.get(currentWorld).stopWorld();
        }

        // Start running next world.
        World world = worlds.get(worldName);
        world.runWorld();

        renderer.setWorld(world);

        // Initalize focus on the central body.
        renderer.setFocusObject(worldName);

        currentWorld = worldName;
    }

    public void stopWorld() {
        // If a world is currently running, stop it.
        if (!currentWorld.isEmpty()) {
            worlds.get(currentWorld).stopWorld();
        }
    }

    public World getWorld(String name) {
        if (!worlds.containsKey(name)) {
            return null;
        }
        return worlds.get(name);
    }

    public World getCurrentWorld() {
        if(currentWorld.isEmpty()) {
            return null;
        }
        return worlds.get(currentWorld);
    }
}
