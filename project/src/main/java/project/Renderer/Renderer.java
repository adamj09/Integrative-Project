package project.Renderer;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

public abstract class Renderer {
    private GLCanvas canvas;

    public Renderer(double fps, int msaa, int swapBuffers) {
        canvas = createGLCanvas(fps, msaa, swapBuffers);
        initOpenGLRenderEventHandlers();
    }

    public abstract void init();

    public abstract void loop();

    private void initOpenGLRenderEventHandlers() {
        canvas.addOnInitEvent(_ -> {
            this.init();
        });

        canvas.addOnRenderEvent(_ -> {
            this.loop();
        });
    }

    private GLCanvas createGLCanvas(double fps, int msaa, int swapBuffers) {

        GLCanvas.Builder glCanvasBuilder = new GLCanvas.Builder();
        glCanvasBuilder.setFlipY(false);
        glCanvasBuilder.setExecutor(LWJGLExecutor.LWJGL_MODULE);
        glCanvasBuilder.setFps(fps);
        glCanvasBuilder.setMSAA(msaa);
        glCanvasBuilder.setSwapBuffers(swapBuffers);

        var canvas = glCanvasBuilder.build();

        return canvas;
    }


    public GLCanvas getCanvas() {
        return this.canvas;
    }
}
