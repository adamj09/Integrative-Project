package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.Renderer.Camera.FirstPersonCameraController;
import project.Renderer.RenderSystems.BodyRenderSystem;
import project.Renderer.World.World;

public class Renderer {
    public static Viewport viewport = new Viewport();
    public static final float DEFAULT_FOV = 90.f;
    public static final float DEFAULT_NEAR = 0.001f;
    public static final float DEFAULT_FAR = 1000.0f;

    // TODO: replace this with access to celestial body database
    public static World simWorld = new World("test");

    private static BodyRenderSystem bodyRenderSystem;

    private static ControlManager controlManager = new ControlManager(viewport.getGLCanvas());
    private static FirstPersonCameraController cameraController;

    public static void init() {
        initOpenGLRenderEventHandlers();
    }

    private static void loop(float deltaTime) {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        controlManager.updateMousePosition();
        controlManager.handleUnfocus();

        updateCamera(deltaTime);

        bodyRenderSystem.loop(deltaTime);
    }

    private static void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> {
            cameraController = new FirstPersonCameraController(simWorld.getCamera(), controlManager);
            bodyRenderSystem = new BodyRenderSystem(simWorld, simWorld.getCamera());

            handleWindowResize();

            bodyRenderSystem.init();
        });

        viewport.getGLCanvas().addOnRenderEvent(event -> {
            loop((float) event.delta);
        });
    }

    private static void updateCamera(float deltaTime) {
        cameraController.updateCameraTransform(deltaTime);
    }

    private static void handleWindowResize() {
        viewport.getGLCanvas().widthProperty().addListener(_ -> {
            setCameraProjection();
        });

        viewport.getGLCanvas().heightProperty().addListener(_ -> {
            setCameraProjection();
        });
    }

    private static void setCameraProjection() {
        simWorld.getCamera().setPerspectiveProjection((float) Math.toRadians(90.0f),
                (float) viewport.getGLCanvas().getWidth() / (float) viewport.getGLCanvas().getHeight(), 0.001f,
                1000.0f);
    }
}
