package project.Renderer;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;

public abstract class Renderer {
    private GLCanvas canvas;

    public Renderer() {
        canvas = createGLCanvas();
        initOpenGLRenderEventHandlers();
    }

    public abstract void initialize();

    public abstract void renderLoop();

    private void initOpenGLRenderEventHandlers() {
        canvas.addOnInitEvent(_ -> {
            this.initialize();
        });

        canvas.addOnRenderEvent(_ -> {
            this.renderLoop();
        });
    }

    private GLCanvas createGLCanvas() {

        GLCanvas.Builder glCanvasBuilder = new GLCanvas.Builder();
        glCanvasBuilder.setFlipY(true);
        glCanvasBuilder.setExecutor(LWJGLExecutor.LWJGL_MODULE);
        glCanvasBuilder.setFps(60);
        glCanvasBuilder.setMSAA(4);
        glCanvasBuilder.setSwapBuffers(2);

        var canvas = glCanvasBuilder.build();

        return canvas;
    }


    public GLCanvas getCanvas() {
        return this.canvas;
    }
}
