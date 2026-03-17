package project.Renderer.World;

import java.util.HashMap;

import project.Renderer.Model.SphereGenerator;

public class World {
    private HashMap<String, WorldObject> objects = new HashMap<>();

    public World() {
        loadObjects();
    }

    private void loadObjects() {
        WorldObject object = new WorldObject("name", new SphereGenerator().create(2));
        objects.put(object.getName(), object);
    }

    public HashMap<String, WorldObject> getObjects() {
        return this.objects;
    }
}
