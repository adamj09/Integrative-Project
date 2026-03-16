package project.Renderer;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL41.*;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

import project.Renderer.Camera.Camera;
import project.Renderer.Camera.FirstPersonCameraController;

public class Renderer {
    private GLCanvas canvas;
    private Camera simCamera = new Camera();
    private SimRenderer simRenderer;
    private ControlManager controlManager;
    private FirstPersonCameraController cameraController;
    private float viewportWidth, viewportHeight;

    public Renderer(double fps, int msaa, int swapBuffers) {
        canvas = createGLCanvas(fps, msaa, swapBuffers);
        canvas.setFocusTraversable(true);

        initOpenGLRenderEventHandlers();

        controlManager = new ControlManager(canvas);
        cameraController = new FirstPersonCameraController(simCamera, controlManager);
        simRenderer = new SimRenderer(simCamera);
    }

    public void init() {
        viewportWidth = (float) canvas.getWidth();
        viewportHeight = (float) canvas.getHeight();

        simCamera.setView(new Vector3f(0.f, 0.f, -5.f), new Vector3f(0.f, 0.f, -1.f));
        simCamera.setPerspectiveProjection((float) Math.toRadians(90.0f), viewportWidth / viewportHeight, 0.001f,
                1000.0f);

        simRenderer.init();
    }

    public void loop(float deltaTime) {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        controlManager.updateMousePosition();

        updateCamera(deltaTime);
        simRenderer.loop(deltaTime);
    }

    private void initOpenGLRenderEventHandlers() {
        canvas.addOnInitEvent(_ -> {
            this.init();
        });

        canvas.addOnRenderEvent(event -> {
            this.loop((float) event.delta);
        });
    }

    private GLCanvas createGLCanvas(double fps, int msaa, int swapBuffers) {
        GLCanvas.Builder glCanvasBuilder = new GLCanvas.Builder();
        glCanvasBuilder.setFlipY(false);
        glCanvasBuilder.setExecutor(LWJGLExecutor.LWJGL_MODULE);
        glCanvasBuilder.setFps(fps);
        glCanvasBuilder.setMSAA(msaa);
        glCanvasBuilder.setSwapBuffers(swapBuffers);

        GLCanvas canvas = glCanvasBuilder.build();

        return canvas;
    }

    private void updateCamera(float deltaTime) {
        cameraController.updateCameraTransform(deltaTime);

        if (viewportWidth != canvas.getWidth() || viewportHeight != canvas.getHeight()) {
            simCamera.setPerspectiveProjection((float) Math.toRadians(90.0f),
                    (float) canvas.getWidth() / (float) canvas.getHeight(), 0.001f, 1000.0f);
            viewportHeight = (float) canvas.getWidth();
            viewportHeight = (float) canvas.getHeight();
        }
    }

    public GLCanvas getCanvas() {
        return this.canvas;
    }
}
