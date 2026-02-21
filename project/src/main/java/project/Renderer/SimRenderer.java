package project.Renderer;

import static org.lwjgl.opengl.GL46.*;

public class SimRenderer extends Renderer {
    public SimRenderer(double fps, int msaa, int swapBuffers) {
        super(fps, msaa, swapBuffers);
    }

    @Override
    public void init() {
        //TODO: load shader code from file
        //TODO: should probably create class to manage loading and compilation of shaders

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, "");
        glCompileShader(vertexShader);
    }

    @Override
    public void loop() {
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
}
