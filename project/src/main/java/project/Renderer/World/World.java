package project.Renderer.World;

import java.util.HashMap;

import org.joml.Vector3f;

import project.Renderer.Model.SphereGenerator;

public class World {
    private HashMap<String, WorldObject> satellites = new HashMap<>();
    private WorldObject body;
    private SphereGenerator sphereGenerator = new SphereGenerator();

    public World(String name) {
        body = new WorldObject(name, sphereGenerator.create(2), new Vector3f(1.0f, 0.0f, 0.0f));
        loadSatellites();
    }

    private void loadSatellites() {
        WorldObject satellite = new WorldObject("test satellite", sphereGenerator.create(2));
        satellites.put(satellite.getName(), satellite);
    }

    public HashMap<String, WorldObject> getObjects() {
        return this.satellites;
    }

    public WorldObject getBody() {
        return body;
    }
}
