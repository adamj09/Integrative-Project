package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.Renderer.Camera.CameraMatrixLoader;
import project.Renderer.Camera.FixedCameraController;
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
     * The camera controller.
     */
    private FixedCameraController fixedCameraController;

    private Shader mainVertShader, orbitVertShader, bodyFragShader, lightFragShader, orbitFragShader;

    private ShaderProgram bodyShaderProgram, lightShaderProgram, orbitShaderProgram;

    private String currentFocusedObject = "";

    /**
     * Constructor for the Renderer class, initializes OpenGL and sets up render
     * event handlers.
     */
    public Renderer(World world) {
        this.world = world;

        controlManager = new ControlManager(this.viewport.getGLCanvas());
        // freeLookCameraController = new FreeLookCameraController(world,
        // controlManager);
        fixedCameraController = new FixedCameraController(this.world, controlManager);

        initOpenGLRenderEventHandlers();
    }

    public Renderer() {
        controlManager = new ControlManager(this.viewport.getGLCanvas());

        viewport.getGLCanvas().setVisible(false);
    }

    /**
     * Initializes the OpenGL render event handlers.
     */
    private void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> init());
        viewport.getGLCanvas().addOnRenderEvent(event -> loop(event.delta));
        viewport.getGLCanvas().addOnReshapeEvent(_ -> setCameraProjection());
        viewport.getGLCanvas().addOnDisposeEvent(_ -> dispose());
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

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        glFrontFace(GL_CCW);
        glEnable(GL_CULL_FACE);

        setUpShaders();

        // Create render systems.
        bodyRenderSystem = new BodyRenderSystem(world, bodyShaderProgram);
        lightRenderSystem = new LightRenderSystem(world, lightShaderProgram);
        orbitRenderSystem = new OrbitRenderSystem(world, orbitShaderProgram);

        // Create camera matrix loader.
        cameraMatrixLoader = new CameraMatrixLoader(world.getCamera());
        setCameraProjection(); // Set the camera's projection matrix.
    }

    /**
     * The main render loop, called every frame to update the world and render the
     * scene.
     * 
     * @param deltaTime The time elapsed since the last frame.
     */
    private void loop(double deltaTime) {
        glClearColor(0.f, 0.f, 0.f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        controlManager.updateMouse();
        controlManager.handleUnfocus();

        // Note: always update world before camera.
        world.updateSatellitePositions();

        fixedCameraController.updateCameraTransform(deltaTime);

        cameraMatrixLoader.update();

        bodyRenderSystem.loop();
        lightRenderSystem.loop();
        orbitRenderSystem.loop();
    }

    private void dispose() {
        bodyRenderSystem.dispose();
        lightRenderSystem.dispose();
        orbitRenderSystem.dispose();

        bodyShaderProgram.dispose();
        lightShaderProgram.dispose();
        orbitShaderProgram.dispose();

        mainVertShader.dispose();
        orbitVertShader.dispose();
        bodyFragShader.dispose();
        lightFragShader.dispose();
        orbitFragShader.dispose();

        cameraMatrixLoader.dispose();

        world.dispose();
    }

    /**
     * Sets the camera's projection matrix based on the current viewport dimensions.
     */
    private void setCameraProjection() {
        world.getCamera().setPerspectiveProjection((float) Math.toRadians(DEFAULT_FOV),
                (float) viewport.getGLCanvas().getWidth() / (float) viewport.getGLCanvas().getHeight(), DEFAULT_NEAR,
                DEFAULT_FAR);
    }

    private void setUpShaders() {
        // Create shaders if necessary.
        // We check if each and every one of these needs to be created because these are
        // fairly expensive operations.
        if (mainVertShader == null) {
            mainVertShader = new Shader("project/shaders/main.vert", GL_VERTEX_SHADER);
        }

        if (orbitVertShader == null) {
            orbitVertShader = new Shader("project/shaders/orbit.vert", GL_VERTEX_SHADER);
        }

        if (bodyFragShader == null) {
            bodyFragShader = new Shader("project/shaders/body.frag", GL_FRAGMENT_SHADER);
        }

        if (lightFragShader == null) {
            lightFragShader = new Shader("project/shaders/light_source.frag", GL_FRAGMENT_SHADER);
        }

        if (orbitFragShader == null) {
            orbitFragShader = new Shader("project/shaders/orbit.frag", GL_FRAGMENT_SHADER);
        }

        // Create shader programs if necessary.
        if (bodyShaderProgram == null) {
            bodyShaderProgram = new ShaderProgram(mainVertShader.getShader(), bodyFragShader.getShader());
        }

        if (lightShaderProgram == null) {
            lightShaderProgram = new ShaderProgram(mainVertShader.getShader(), lightFragShader.getShader());
        }

        if (orbitShaderProgram == null) {
            orbitShaderProgram = new ShaderProgram(orbitVertShader.getShader(), orbitFragShader.getShader());
        }

        // No need to check here -- there's a check for existing uniform block bindings
        // in this method.
        bodyShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
        lightShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
        orbitShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
    }

    public void setFocusObject(String name) {
        currentFocusedObject = name;
        fixedCameraController.setFocusObject(name);
    }

    /**
     * @return The viewport being rendered to.
     */
    public Viewport getViewport() {
        return this.viewport;
    }

    public void refreshRenderSystems() {
        // This is called when existing world is updated (after applying loaded
        // configuration)
        // All render systems need to be refreshed because world internals have been
        // completely reset
        world.getBodyMesh().setUpBuffers();
        world.getOrbitMesh().setUpBuffers();
        world.getLightSourceMesh().setUpBuffers();

        bodyRenderSystem = new BodyRenderSystem(world, bodyShaderProgram);
        lightRenderSystem = new LightRenderSystem(world, lightShaderProgram);
        orbitRenderSystem = new OrbitRenderSystem(world, orbitShaderProgram);

        cameraMatrixLoader = new CameraMatrixLoader(world.getCamera());
        setCameraProjection();
    }

    public void setWorld(World world) {
        this.world = world;
        fixedCameraController = new FixedCameraController(this.world, controlManager);
        if (currentFocusedObject.isEmpty() || !world.getBodyObjects().containsKey(currentFocusedObject)) {
            setFocusObject(world.getName());
        } else {
            setFocusObject(currentFocusedObject);
        }

        viewport.getGLCanvas().setVisible(true);

        initOpenGLRenderEventHandlers();
    }

    /**
     * @return The world being rendered.
     */
    public World getWorld() {
        return this.world;
    }
}