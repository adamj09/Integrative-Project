package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import project.ControlManager;
import project.SimulationPool;
import project.Renderer.Camera.FixedCameraController;
import project.Renderer.Camera.FreeLookCameraController;
import project.Renderer.RenderSystems.BodyRenderSystem;
import project.Renderer.RenderSystems.CameraRenderSystem;
import project.Renderer.RenderSystems.LightRenderSystem;
import project.Renderer.RenderSystems.OrbitRenderSystem;
import project.Renderer.World.World;

public class Renderer {
    public Viewport viewport = new Viewport();

    public static final float DEFAULT_FOV = 45.f, DEFAULT_NEAR = 0.001f, DEFAULT_FAR = 100_000.0f;
    public static final int MAT4F_SIZE = 16 * Float.BYTES, VEC4F_SIZE = 4 * Float.BYTES, VEC3F_SIZE = 3 * Float.BYTES;

    private World world;

    private CameraRenderSystem cameraRenderSystem;
    private BodyRenderSystem bodyRenderSystem;
    private LightRenderSystem lightRenderSystem;
    private OrbitRenderSystem orbitRenderSystem;

    private ControlManager controlManager;

    private FreeLookCameraController freeLookCameraController;
    private FixedCameraController fixedCameraController;

    public Renderer() {
        initOpenGLRenderEventHandlers();
    }

    private void initOpenGLRenderEventHandlers() {
        viewport.getGLCanvas().addOnInitEvent(_ -> {
            // Set up viewport resize handler
            handleViewportResize();

            // Enable depth testing
            glEnable(GL_DEPTH_TEST);

            glFrontFace(GL_CCW);
            glEnable(GL_CULL_FACE);

            controlManager = new ControlManager(viewport.getGLCanvas());

            SimulationPool.load();
            world = SimulationPool.getWorld("Earth");

            // Create camera controllers
            freeLookCameraController = new FreeLookCameraController(world, controlManager);
            //fixedCameraController = new FixedCameraController(world, controlManager);
            //fixedCameraController.setFocusObject("test");

            Shader mainVertShader = new Shader("project/shaders/main.vert", GL_VERTEX_SHADER);
            Shader orbitVertShader = new Shader("project/shaders/orbit.vert", GL_VERTEX_SHADER);

            Shader bodyFragShader = new Shader("project/shaders/body.frag", GL_FRAGMENT_SHADER);
            Shader lightFragShader = new Shader("project/shaders/light_source.frag", GL_FRAGMENT_SHADER);
            Shader orbitFragShader = new Shader("project/shaders/orbit.frag", GL_FRAGMENT_SHADER);

            // Create shader programs
            ShaderProgram bodyShaderProgram = new ShaderProgram(mainVertShader.getShader(), bodyFragShader.getShader());
            ShaderProgram lightShaderProgram = new ShaderProgram(mainVertShader.getShader(), lightFragShader.getShader());
            ShaderProgram orbitShaderProgram = new ShaderProgram(orbitVertShader.getShader(), orbitFragShader.getShader());

            // Create render systems
            bodyRenderSystem = new BodyRenderSystem(world, bodyShaderProgram);
            cameraRenderSystem = new CameraRenderSystem(world.getCamera());
            lightRenderSystem = new LightRenderSystem(world, lightShaderProgram);
            orbitRenderSystem = new OrbitRenderSystem(viewport, world, orbitShaderProgram);

            setCameraProjection();
            
            bodyShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
            lightShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
            orbitShaderProgram.addUniformBlockBinding("CameraMatrices", 0);
        });

        viewport.getGLCanvas().addOnRenderEvent(event -> {
            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            controlManager.updateMouse();
            controlManager.handleUnfocus();
        
            // Note: always update world before camera.
            world.updateSatellites();

            //TODO: switch between camera controllers when needed
            //freeLookCameraController.updateCameraTransform(event.delta);
            fixedCameraController.updateCameraTransform(event.delta);

            cameraRenderSystem.loop();
            bodyRenderSystem.loop();
            lightRenderSystem.loop();
            orbitRenderSystem.loop();
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

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }
}