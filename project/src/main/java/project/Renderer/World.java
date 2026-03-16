package project.Renderer;

import java.util.HashMap;

import project.Renderer.Model.SphereGenerator;

public class World {
    private HashMap<String, WorldObject> objects = new HashMap<>();

    public World() {

    }

    public void loadObjects() {
        WorldObject object = new WorldObject("name", new SphereGenerator().create(2));
    }
}
