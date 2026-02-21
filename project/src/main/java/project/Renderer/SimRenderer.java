package project.Renderer;

import static org.lwjgl.opengl.GL11.*;

public class SimRenderer extends Renderer {
    @Override
    public void initialize() {
        
    }

    @Override
    public void renderLoop() {
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
