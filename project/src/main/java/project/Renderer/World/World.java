package project.Renderer.World;

import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector4f;

import project.Renderer.Renderer;
import project.Renderer.Camera.Camera;
import project.Renderer.Model.Mesh;
import project.Renderer.Model.SphereGenerator;

public class World {
    private HashMap<String, WorldObject> satellites = new HashMap<>();
    private WorldObject body;
    private SphereGenerator sphereGenerator = new SphereGenerator();
    private Mesh sphere;
    private Camera camera = new Camera();

    public World(String name) {
        sphere = sphereGenerator.create(2);
        body = new WorldObject(name, sphere, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        initCamera();
        loadSatellites();
    }

    private void loadSatellites() {
        WorldObject satellite = new WorldObject("test satellite", sphere);
        satellites.put(satellite.getName(), satellite);
    }

    private void initCamera() {
        camera.setView(new Vector3f(0.f, 0.f, -5.f), new Vector3f(0.f, 0.f, 1.f));

        camera.setPerspectiveProjection((float) Math.toRadians(Renderer.DEFAULT_FOV),
                (float)Renderer.viewport.getGLCanvas().getWidth() / (float)Renderer.viewport.getGLCanvas().getHeight(),
                Renderer.DEFAULT_NEAR, Renderer.DEFAULT_FAR);
    }

    public HashMap<String, WorldObject> getObjects() {
        return this.satellites;
    }

    public WorldObject getBody() {
        return this.body;
    }

    public Camera getCamera() {
        return this.camera;
    }
}
