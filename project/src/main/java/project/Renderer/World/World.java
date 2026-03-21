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
    private Mesh sphere = new SphereGenerator().create(2);
    private Camera camera = new Camera();

    private FloatBuffer colorsBuffer;
    private FloatBuffer matricesBuffer;

    public World(String name) {
        body = new WorldObject(name, sphere, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));

        initCamera();
        loadSatellites();
        packColorsIntoBuffer();
        packMatricesIntoBuffer();
    }

    private void loadSatellites() {
        WorldObject satellite = new WorldObject("test satellite", sphere, new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        satellite.setTranslation(new Vector3f(-10.f, 0.f, 0.f));
        satellite.setScale(new Vector3f(0.5f, 0.5f, 0.5f));
        satellites.put(satellite.getName(), satellite);
    }

    public void packColorsIntoBuffer() {
        int capacity = 4 * (satellites.size() + 1);
        colorsBuffer = BufferUtils.createFloatBuffer(capacity);

        float[] colors = new float[capacity];
        Vector4f colorBody = body.getColor();
        colors[0] = colorBody.x;
        colors[1] = colorBody.y;
        colors[2] = colorBody.z;
        colors[3] = colorBody.w;
                      
        int i = 4;
        for (Map.Entry<String, WorldObject> set : satellites.entrySet()) {
            Vector4f color = set.getValue().getColor();
            colors[i] = color.x;
            colors[i + 1] = color.y;
            colors[i + 2] = color.z;
            colors[i + 3] = color.w;
            i += 4;
        }

        colorsBuffer.put(colors).flip();
    }

    public void packMatricesIntoBuffer() {
        int capacity = 16 * (satellites.size() + 1);
        matricesBuffer = BufferUtils.createFloatBuffer(capacity);

        float[] matrices = new float[capacity];
        body.getTransformMatrix().get(matrices);

        int i = 1;
        for(Map.Entry<String, WorldObject> set : satellites.entrySet()) {
            set.getValue().getTransformMatrix().get(matrices, 16 * i);
        }

        matricesBuffer.put(matrices).flip();
    }

    private void initCamera() {
        camera.setView(new Vector3f(0.f, 0.f, -5.f), new Vector3f(0.f, 0.f, 1.f));

        camera.setPerspectiveProjection((float) Math.toRadians(Renderer.DEFAULT_FOV),
                (float) Renderer.viewport.getGLCanvas().getWidth()
                        / (float) Renderer.viewport.getGLCanvas().getHeight(),
                Renderer.DEFAULT_NEAR, Renderer.DEFAULT_FAR);
    }

    public HashMap<String, WorldObject> getSatellites() {
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

    public FloatBuffer getMatricesBuffer() {
        return this.matricesBuffer;
    }

    public Mesh getSphere() {
        return this.sphere;
    }
}
