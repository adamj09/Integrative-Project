package project.Renderer.World;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

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

    private FloatBuffer colorsBuffer;

    // TODO: left off here, need to pack all vertices and indices from all meshes
    // into buffers for rendering
    private FloatBuffer verticesBuffer;
    private FloatBuffer indicesBuffer;

    public World(String name) {
        sphere = sphereGenerator.create(2);
        body = new WorldObject(name, sphere, new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));

        initCamera();
        loadSatellites();
    }

    private void loadSatellites() {
        WorldObject satellite = new WorldObject("test satellite", sphere);
        satellites.put(satellite.getName(), satellite);
    }

    public void packColorsIntoBuffer() {
        colorsBuffer = BufferUtils.createFloatBuffer(4 * (satellites.size() + 1));

        float[] colors = new float[satellites.size() + 1];
        colors[0] = body.getColor().x;
        colors[1] = body.getColor().y;
        colors[2] = body.getColor().z;
        colors[3] = body.getColor().w;

        int i = 4;
        for (Map.Entry<String, WorldObject> set : satellites.entrySet()) {
            colors[i] = set.getValue().getColor().x;
            colors[i + 1] = set.getValue().getColor().x;
            colors[i + 2] = set.getValue().getColor().x;
            colors[i + 3] = set.getValue().getColor().x;
            i += 4;
        }

        colorsBuffer.put(colors).flip();
    }

    private void initCamera() {
        camera.setView(new Vector3f(0.f, 0.f, -5.f), new Vector3f(0.f, 0.f, 1.f));

        camera.setPerspectiveProjection((float) Math.toRadians(Renderer.DEFAULT_FOV),
                (float) Renderer.viewport.getGLCanvas().getWidth()
                        / (float) Renderer.viewport.getGLCanvas().getHeight(),
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

    public FloatBuffer getColorsBuffer() {
        return this.colorsBuffer;
    }
}
