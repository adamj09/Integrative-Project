package project.Renderer;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

public class Viewport {
    public static final double DEFAULT_FPS = 60;
    public static final int DEFAULT_MSAA = 4;
    public static final int DEFAULT_SWAP_BUFFERS = 1;

    private GLCanvas canvas;

    public Viewport(double fps, int msaa, int swapBuffers) {
        canvas = createGLCanvas(fps, msaa, swapBuffers);
        canvas.setFocusTraversable(true);
    }

    public Viewport() {
        canvas = createGLCanvas(DEFAULT_FPS, DEFAULT_MSAA, DEFAULT_SWAP_BUFFERS);
        canvas.setFocusTraversable(true);
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

    public GLCanvas getGLCanvas() {
        return this.canvas;
    }
}
