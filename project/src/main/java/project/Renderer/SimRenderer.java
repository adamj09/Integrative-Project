package project.Renderer;

import static org.lwjgl.opengl.GL46.*;

import java.io.IOException;

public class SimRenderer extends Renderer {
    private ShaderProgram shaderProgram;

    public SimRenderer(double fps, int msaa, int swapBuffers) {
        super(fps, msaa, swapBuffers);
    }

    @Override
    public void init() {
        try {
            shaderProgram = new ShaderProgram(
            "Integrative-Project/project/shaders/main.vert", 
            "Integrative-Project/project/shaders/main.frag"
            );
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void loop() {
        shaderProgram.use();
    }
}
