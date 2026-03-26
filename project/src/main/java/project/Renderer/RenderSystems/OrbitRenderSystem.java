package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.Viewport;
import project.Renderer.Model.Mesh;
import project.Renderer.World.World;

import static org.lwjgl.opengl.GL41.*;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class OrbitRenderSystem {
    private Viewport viewport;
    private World world;
    private ShaderProgram shaderProgram;

    private Mesh screenQuad;

    public OrbitRenderSystem(Viewport viewport, World world, ShaderProgram shaderProgram) {
        this.viewport = viewport;
        this.world = world;
        this.shaderProgram = shaderProgram;

        init();
    }

    public void init() {
        shaderProgram.use();

        ArrayList<Vector3f> vertices = new ArrayList<>();
        vertices.add(new Vector3f(-1.0f, 1.0f, 0.0f));
        vertices.add(new Vector3f(1.0f, 1.0f, 0.0f));
        vertices.add(new Vector3f(1.0f, -1.0f, 0.0f));
        vertices.add(new Vector3f(-1.0f, -1.0f, 0.0f));

        ArrayList<Vector3i> indices = new ArrayList<>();
        indices.add(new Vector3i(0, 3, 1));
        indices.add(new Vector3i( 1, 3, 2));

        // TODO: create screenQuad texture coordinates
        screenQuad = new Mesh(vertices, indices, new ArrayList<Vector3f>(), new ArrayList<>());
    }

    public void loop() {
        shaderProgram.use();

        shaderProgram.addUniformVec2f("resolution",
                new Vector2f((float) viewport.getGLCanvas().getWidth(), (float) viewport.getGLCanvas().getHeight()));

        glClear(GL_DEPTH_BUFFER_BIT);
        glBindVertexArray(screenQuad.getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, screenQuad.getVertices().size() * 3, GL_UNSIGNED_INT, 0, world.getBodies().size());
    }
}
