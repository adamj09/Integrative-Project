package project.Renderer;

import static org.lwjgl.opengl.GL11.*;

public class SimRenderer extends Renderer {
    public SimRenderer(double fps, int msaa, int swapBuffers) {
        super(fps, msaa, swapBuffers);
    }

    @Override
    public void init() {
        
    }

    @Override
    public void loop() {
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
