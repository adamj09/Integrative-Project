package project.Renderer;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.Renderer.Camera.Camera;
import project.Renderer.Camera.FirstPersonCameraController;
import project.Renderer.Renderers.SimRenderer;

public class Renderer {
    private Viewport viewport;
    private Camera simCamera = new Camera();
    private SimRenderer simRenderer;
    private ControlManager controlManager;
    private FirstPersonCameraController cameraController;
    private float viewportWidth, viewportHeight;

    public Renderer(Viewport viewport, ControlManager controlManager) {
        this.viewport = viewport;
        this.controlManager = controlManager;

        initOpenGLRenderEventHandlers();

        cameraController = new FirstPersonCameraController(simCamera, this.controlManager);
        simRenderer = new SimRenderer(simCamera);
    }

    public void init() {
        viewportWidth = (float) viewport.getGLCanvas().getWidth();
        viewportHeight = (float) viewport.getGLCanvas().getHeight();

        simCamera.setView(new Vector3f(0.f, 0.f, -5.f), new Vector3f(0.f, 0.f, -1.f));
        simCamera.setPerspectiveProjection((float) Math.toRadians(90.0f), viewportWidth / viewportHeight, 0.001f,
                1000.0f);

        simRenderer.init();
    }

    public void loop(float deltaTime) {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        controlManager.updateMousePosition();
        controlManager.handleUnfocus();

        updateCamera(deltaTime);
        simRenderer.loop(deltaTime);
    }

    private void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> {
            this.init();
        });

        viewport.getGLCanvas().addOnRenderEvent(event -> {
            this.loop((float) event.delta);
        });
    }

    private void updateCamera(float deltaTime) {
        cameraController.updateCameraTransform(deltaTime);

        if (viewportWidth != viewport.getGLCanvas().getWidth() || viewportHeight != viewport.getGLCanvas().getHeight()) {
            simCamera.setPerspectiveProjection((float) Math.toRadians(90.0f),
                    (float) viewport.getGLCanvas().getWidth() / (float) viewport.getGLCanvas().getHeight(), 0.001f, 1000.0f);
            viewportHeight = (float) viewport.getGLCanvas().getWidth();
            viewportHeight = (float) viewport.getGLCanvas().getHeight();
        }
    }

    public Viewport getViewport() {
        return this.viewport;
    }
}
