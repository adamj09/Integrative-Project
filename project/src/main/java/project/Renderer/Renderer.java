package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.Renderer.Camera.FirstPersonCameraController;
import project.Renderer.RenderSystems.BodyRenderSystem;
import project.Renderer.RenderSystems.CameraRenderSystem;
import project.Renderer.RenderSystems.LightRenderSystem;
import project.Renderer.World.World;

public class Renderer {
    public Viewport viewport = new Viewport();

    public static final float DEFAULT_FOV = 90.f, DEFAULT_NEAR = 0.001f, DEFAULT_FAR = 1000.0f;
    public static final int MAT4F_SIZE = 16 * Float.BYTES, VEC4F_SIZE = 4 * Float.BYTES, VEC3F_SIZE = 3 * Float.BYTES;

    public World world;

    private CameraRenderSystem cameraRenderSystem;
    private BodyRenderSystem bodyRenderSystem;
    private LightRenderSystem lightRenderSystem;

    private ControlManager controlManager;
    private FirstPersonCameraController cameraController;
    private ShaderProgram bodyShaderProgram, lightShaderProgram;

    public Renderer() {
        initOpenGLRenderEventHandlers();
    }

    private void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> {
            // Set up viewport resize handler
            handleViewportResize();

            // Enable depth testing
            glEnable(GL_DEPTH_TEST);

            world = new World();
            controlManager = new ControlManager(viewport.getGLCanvas());

            // Create camera controller
            cameraController = new FirstPersonCameraController(world.getCamera(), controlManager);

            // Create shader programs
            bodyShaderProgram = new ShaderProgram("project/shaders/body.vert",
                    "project/shaders/body.frag");

            lightShaderProgram = new ShaderProgram("project/shaders/lightSource.vert", "project/shaders/lightSource.frag");

            // Create render systems
            bodyShaderProgram.use();
            bodyRenderSystem = new BodyRenderSystem(world);
            cameraRenderSystem = new CameraRenderSystem(world.getCamera());

            lightShaderProgram.use();
            lightRenderSystem = new LightRenderSystem(world);

            setCameraProjection();

            glUniformBlockBinding(bodyShaderProgram.getID(), glGetUniformBlockIndex(bodyShaderProgram.getID(), "CameraMatrices"), 0);
            glUniformBlockBinding(bodyShaderProgram.getID(), glGetUniformBlockIndex(lightShaderProgram.getID(), "CameraMatrices"), 0);

        });

        viewport.getGLCanvas().addOnRenderEvent(event -> {
            glClearColor(0.1f, 0.1f, 0.5f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            controlManager.updateMousePosition();
            controlManager.handleUnfocus();

            cameraController.updateCameraTransform((float) event.delta);

            bodyShaderProgram.use();
            cameraRenderSystem.loop();
            bodyRenderSystem.loop();

            lightShaderProgram.use();
            lightRenderSystem.loop();
        });
    }

    private void handleViewportResize() {
        viewport.getGLCanvas().widthProperty().addListener(_ -> {
            setCameraProjection();
        });

        viewport.getGLCanvas().heightProperty().addListener(_ -> {
            setCameraProjection();
        });
    }

    private void setCameraProjection() {
        world.getCamera().setPerspectiveProjection((float) Math.toRadians(DEFAULT_FOV),
                (float) viewport.getGLCanvas().getWidth() / (float) viewport.getGLCanvas().getHeight(), DEFAULT_NEAR,
                DEFAULT_FAR);
    }

    public Viewport getViewport() {
        return this.viewport;
    }
}
