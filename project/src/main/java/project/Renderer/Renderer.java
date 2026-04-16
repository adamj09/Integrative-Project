package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.Renderer.Camera.CameraMatrixLoader;
import project.Renderer.Camera.FixedCameraController;
import project.Renderer.Camera.FreeLookCameraController;
import project.Renderer.RenderSystems.BodyRenderSystem;
import project.Renderer.RenderSystems.LightRenderSystem;
import project.Renderer.RenderSystems.OrbitRenderSystem;
import project.Renderer.World.World;

/**
 * Class responsible for managing the rendering of an entire world, including
 * the
 * camera and render systems.
 * 
 * @author Adam Johnston
 */
public class Renderer {

    /**
     * Default values for the camera projection parameters for convenience.
     */
    public static final float DEFAULT_FOV = 45.f, DEFAULT_NEAR = 0.001f, DEFAULT_FAR = 1_000_000.0f;

    /**
     * Sizes of varius data types in bytes for convenience when calculating buffer
     * sizes and offsets.
     */
    public static final int MAT4F_SIZE = 16 * Float.BYTES, VEC4F_SIZE = 4 * Float.BYTES, VEC3F_SIZE = 3 * Float.BYTES;

    /**
     * The viewport to render to.
     */
    public Viewport viewport = new Viewport();

    /**
     * The world to render.
     */
    private World world;

    /**
     * The camera matrix loader.
     */
    private CameraMatrixLoader cameraMatrixLoader;

    /**
     * The render systems for rendering different types of objects.
     */
    private BodyRenderSystem bodyRenderSystem;
    private LightRenderSystem lightRenderSystem;
    private OrbitRenderSystem orbitRenderSystem;

    /**
     * The control manager for handling user input.
     */
    private ControlManager controlManager;

    /**
     * The camera controllers for handling different camera modes.
     */
    private FreeLookCameraController freeLookCameraController;
    private FixedCameraController fixedCameraController;

    /**
     * Constructor for the Renderer class, initializes OpenGL and sets up render
     * event handlers.
     */
    public Renderer(World world) {
        this.world = world;

        initOpenGLRenderEventHandlers();
    }

    /**
     * Initializes the OpenGL render event handlers.
     */
    private void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> init());
        viewport.getGLCanvas().addOnRenderEvent(event -> loop(event.delta));
    }

    /**
     * Initializes the renderer, setting up the viewport resize handler, enabling
     * depth testing, loading the world, creating camera controllers, creating
     * shader programs, and creating render systems.
     */
    private void init() {
        world.getBodyMesh().setUpBuffers();
        world.getOrbitMesh().setUpBuffers();
        world.getLightSourceMesh().setUpBuffers();

        // Set up viewport resize handler
        handleViewportResize();

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        glFrontFace(GL_CCW);
        glEnable(GL_CULL_FACE);

        controlManager = new ControlManager(viewport.getGLCanvas());

        // Create camera controllers.
        // freeLookCameraController = new FreeLookCameraController(world,
        // controlManager);
        fixedCameraController = new FixedCameraController(world, controlManager);
        fixedCameraController.setFocusObject("test2");

        Shader mainVertShader = new Shader("project/shaders/main.vert", GL_VERTEX_SHADER);
        Shader orbitVertShader = new Shader("project/shaders/orbit.vert", GL_VERTEX_SHADER);

        Shader bodyFragShader = new Shader("project/shaders/body.frag", GL_FRAGMENT_SHADER);
        Shader lightFragShader = new Shader("project/shaders/light_source.frag", GL_FRAGMENT_SHADER);
        Shader orbitFragShader = new Shader("project/shaders/orbit.frag", GL_FRAGMENT_SHADER);

        // Create shader programs.
        ShaderProgram bodyShaderProgram = new ShaderProgram(mainVertShader.getShader(), bodyFragShader.getShader());
        ShaderProgram lightShaderProgram = new ShaderProgram(mainVertShader.getShader(), lightFragShader.getShader());
        ShaderProgram orbitShaderProgram = new ShaderProgram(orbitVertShader.getShader(), orbitFragShader.getShader());

        // Create render systems.
        bodyRenderSystem = new BodyRenderSystem(world, bodyShaderProgram);
        lightRenderSystem = new LightRenderSystem(world, lightShaderProgram);
        orbitRenderSystem = new OrbitRenderSystem(world, orbitShaderProgram);

        // Create camera matrix loader.
        cameraMatrixLoader = new CameraMatrixLoader(world.getCamera());
        setCameraProjection(); // Set the camera's projection matrix.

        // Add the camera matrix uniform buffers to the shader programs.
        bodyShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
        lightShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
        orbitShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
    }

    /**
     * The main render loop, called every frame to update the world and render the
     * scene.
     * 
     * @param deltaTime The time elapsed since the last frame.
     */
    private void loop(double deltaTime) {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        controlManager.updateMouse();
        controlManager.handleUnfocus();

        // Note: always update world before camera.
        world.updateSatellites();

        // TODO: switch between camera controllers when needed
        // freeLookCameraController.updateCameraTransform(event.delta);
        fixedCameraController.updateCameraTransform(deltaTime);
        
        cameraMatrixLoader.update();

        bodyRenderSystem.loop();
        lightRenderSystem.loop();
        orbitRenderSystem.loop();
    }

    /**
     * Handles viewport resize events by updating the camera projection.
     */
    private void handleViewportResize() {
        viewport.getGLCanvas().widthProperty().addListener(_ -> {
            setCameraProjection();
        });

        viewport.getGLCanvas().heightProperty().addListener(_ -> {
            setCameraProjection();
        });
    }

    /**
     * Sets the camera's projection matrix based on the current viewport dimensions.
     */
    private void setCameraProjection() {
        world.getCamera().setPerspectiveProjection((float) Math.toRadians(DEFAULT_FOV),
                (float) viewport.getGLCanvas().getWidth() / (float) viewport.getGLCanvas().getHeight(), DEFAULT_NEAR,
                DEFAULT_FAR);
    }

    /**
     * @return The viewport being rendered to.
     */
    public Viewport getViewport() {
        return this.viewport;
    }

    /**
     * @return The world being rendered.
     */
    public World getWorld() {
        return this.world;
    }
}