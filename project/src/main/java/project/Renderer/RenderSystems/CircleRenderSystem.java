package project.Renderer.RenderSystems;

import project.Renderer.ShaderProgram;
import project.Renderer.Viewport;

import org.joml.Vector2f;

public class CircleRenderSystem {
    private ShaderProgram shaderProgram;
    private Viewport viewport;

    public CircleRenderSystem(Viewport viewport, ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.viewport = viewport;

        init();
    }

    private void init() {
        
    }

    public void loop() {
        shaderProgram.use();

        shaderProgram.addUniformVec2f("resolution",
                new Vector2f((float) viewport.getGLCanvas().getWidth(), (float) viewport.getGLCanvas().getHeight()));
    }
}
