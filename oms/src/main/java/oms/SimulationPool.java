package oms;

import java.util.HashMap;

import org.joml.Vector3f;

import oms.Physics.Body;
import oms.Renderer.Renderer;
import oms.Renderer.World.World;

/**
 * Class used to store and perform operations on all currently loaded worlds.
 * 
 * @author Adam Johnston
 */
public class SimulationPool {
    /**
     * Renderer used to display these worlds.
     */
    private Renderer renderer;

    /**
     * Currently loaded worlds.
     */
    private HashMap<String, World> worlds = new HashMap<>();

    /**
     * World that is currently running.
     */
    private String selectedWorld;

    /**
     * Creates a simulation pool with a given Renderer.
     * 
     * @param renderer the renderer used to display the simulation pool's worlds.
     */
    public SimulationPool(Renderer renderer) {
        this.selectedWorld = "";
        this.renderer = renderer;
    }

    /**
     * Creates and adds a new world to this simulation pool.
     * 
     * @param name  the world's name.
     * @param body  the central celestial body used by the new world.
     * @param color the colour of the central celestial body used by the new world.
     */
    public void createWorld(String name, Body body, Vector3f color) {
        worlds.put(name, new World(body, color));
    }

    /**
     * Adds an existing world to the simulation pool.
     * 
     * @param world world to add to the simulation pool.
     */
    public void addWorld(World world) {
        worlds.put(world.getName(), world);
    }

    /**
     * Stops the currently running world and starts simulating a world with a given
     * name.
     * Also sets the currentWorld variable to the given world name.
     * 
     * Does nothing if a world with the given name cannot be found in the simulation
     * pool.
     * 
     * @param worldName the name of the world to begin simulating.
     */
    public void runWorld(String worldName) {
        if (!worlds.containsKey(worldName)) {
            return;
        }

        // Stop currently running world.
        stopWorld(worldName);

        // Start running next world.
        World world = worlds.get(worldName);
        world.runWorld();

        renderer.setWorld(world);

        selectedWorld = worldName;
    }

    /**
     * Stops the currently running world, if there is a currently running world.
     * Note that currentWorld remains the name of the world that was last run.
     */
    public void stopWorld(String worldName) {
        // If a world is currently running, stop it.
        if (worlds.containsKey(worldName)) {
            worlds.get(worldName).stopWorld();
        }
    }

    /**
     * Starts a world back up from pause (no time resetting to 0).
     */
    public void startWorld() {
        // If a world is currently running, stop it.
        if (!selectedWorld.isEmpty()) {
            worlds.get(selectedWorld).startWorld();
        }
    }

    /**
     * Resets the simulation time of the currently running world to zero, if there
     * is a currently running world.
     */
    public void resetWorld() {
        if (!selectedWorld.isEmpty()) {
            worlds.get(selectedWorld).getBody().resetTime();
        }
    }

    /**
     * Sets the simulation time scale of the current running world.
     * 
     * @param timeScale the simulation time scale to set. This acts as a multiplier,
     *                  i.e. a time scale of two will cause the simulation to run
     *                  twice as fast.
     */
    public void setTimeScale(double timeScale) {
        if (getSelectedWorld() == null) {
            return;
        }
        getSelectedWorld().getBody().setTimeScale(timeScale);
    }

    /**
     * Sets the elapsed time of the simulation to the desired time.
     * 
     * @param timeSeconds desired time in seconds.
     */
    public void setTime(double timeSeconds) {
        if (getSelectedWorld() == null) {
            return;
        }
        getSelectedWorld().getBody().setTime(timeSeconds);
    }

    /**
     * Gets a world in the simulation pool with a given name.
     * 
     * @param name the name of the desired world.
     * @return the world with the provided name, null if a world with that name
     *         cannot be found in the simulation pool.
     */
    public World getWorld(String name) {
        if (!worlds.containsKey(name)) {
            return null;
        }
        return worlds.get(name);
    }

    /**
     * @return the currentWorld String, or null if currentWorld is empty.
     */
    public World getSelectedWorld() {
        if (selectedWorld.isEmpty()) {
            return null;
        }
        return worlds.get(selectedWorld);
    }

    public void selectWorld(String name) {
        if(worlds.containsKey(name)) {
            selectedWorld = name;
        }
    }

    /**
     * @return the renderer used to display this simulation pool's worlds.
     */
    public Renderer getRenderer() {
        return this.renderer;
    }

    /**
     * @return a HashMap containing all worlds in this simulation pool, with their
     *         names (String) as keys and worlds themselves as values (World).
     */
    public HashMap<String, World> getWorlds() {
        return this.worlds;
    }
}
