package project.Renderer;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

/**
 * Class representing a viewport for rendering.
 * 
 * @author Adam Johnston
 */
public class Viewport {
    /**
     * Default viewport values for convenience.
     */
    public static final double DEFAULT_FPS = 60;
    public static final int DEFAULT_MSAA = 4;
    public static final int DEFAULT_SWAP_BUFFERS = 1;

    /**
     * The OpenGL canvas for rendering.
     */
    private GLCanvas canvas;

    /**
     * Creates a new viewport with the specified settings.
     * @param fps The desired frames per second at which the viewport should render.
     * @param msaa The desired level of multisample anti-aliasing (MSAA) for the viewport.
     * @param swapBuffers The desired number of buffers to use for swapping (can be 1 or 2 for single and double buffering respectively).
     */
    public Viewport(double fps, int msaa, int swapBuffers) {
        if(swapBuffers < 1 || swapBuffers > 2) {
            System.err.println("Invalid number of swap buffers specified: " + swapBuffers + ". Must be 1 or 2.");
            System.exit(1);
        }

        

        canvas = createGLCanvas(fps, msaa, swapBuffers);
        canvas.setFocusTraversable(true);
    }

    /**
     * Creates a new viewport with default settings.
     */
    public Viewport() {
        canvas = createGLCanvas(DEFAULT_FPS, DEFAULT_MSAA, DEFAULT_SWAP_BUFFERS);
        canvas.setFocusTraversable(true);
    }

    /**
     * Helper method to create a GLCanvas with the specified settings.
     * @param fps The desired frames per second at which the canvas should render.
     * @param msaa The desired level of multisample anti-aliasing (MSAA) for the canvas.
     * @param swapBuffers The desired number of buffers to use for swapping.
     * @return The created GLCanvas.
     */
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

    /**
     * Gets the OpenGL canvas for rendering.
     * @return The GLCanvas used for rendering in this viewport.
     */
    public GLCanvas getGLCanvas() {
        return this.canvas;
    }

    /**
     * Disposes of the GLCanvas object.
     */
    public void dispose() {
        canvas.dispose();
    }
}
