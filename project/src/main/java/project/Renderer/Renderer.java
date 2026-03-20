package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.Renderer.Camera.FirstPersonCameraController;
import project.Renderer.World.World;

public class Renderer {
    public static Viewport viewport = new Viewport();

    public static final float DEFAULT_FOV = 90.f;
    public static final float DEFAULT_NEAR = 0.001f;
    public static final float DEFAULT_FAR = 1000.0f;

    // TODO: replace this with access to celestial body database
    public static World simWorld;

    private static RenderSystem renderSystem;

    private static ControlManager controlManager;
    private static FirstPersonCameraController cameraController;

    public static void init() {
        initOpenGLRenderEventHandlers();
    }

    private static void loop(float deltaTime) {
        glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        controlManager.updateMousePosition();
        controlManager.handleUnfocus();

        updateCamera(deltaTime);

        renderSystem.loop(deltaTime);
    }

    private static void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> {
            // Set up viewport resize handler
            handleViewportResize();

            simWorld = new World("test");
            controlManager = new ControlManager(viewport.getGLCanvas());

            // Create camera controller
            cameraController = new FirstPersonCameraController(simWorld.getCamera(), controlManager);

            // Create render systems
            renderSystem = new RenderSystem(simWorld);
        });

        viewport.getGLCanvas().addOnRenderEvent(event -> {
            loop((float) event.delta);
        });
    }

    private static void updateCamera(float deltaTime) {
        cameraController.updateCameraTransform(deltaTime);
    }

    private static void handleViewportResize() {
        viewport.getGLCanvas().widthProperty().addListener(_ -> {
            setCameraProjection();
        });

        viewport.getGLCanvas().heightProperty().addListener(_ -> {
            setCameraProjection();
        });
    }

    private static void setCameraProjection() {
        simWorld.getCamera().setPerspectiveProjection((float) Math.toRadians(DEFAULT_FOV),
                (float) viewport.getGLCanvas().getWidth() / (float) viewport.getGLCanvas().getHeight(), DEFAULT_NEAR,
                DEFAULT_FAR);
    }
}
