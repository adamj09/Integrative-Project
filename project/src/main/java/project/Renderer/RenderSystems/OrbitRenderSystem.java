package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.Viewport;
import project.Renderer.World.World;

import static org.lwjgl.opengl.GL41.*;

import org.joml.Vector2f;

public class OrbitRenderSystem {
    private Viewport viewport;
    private World world;
    private ShaderProgram shaderProgram;
    private int vboModelMatrices;

    public OrbitRenderSystem(Viewport viewport, World world, ShaderProgram shaderProgram) {
        this.viewport = viewport;
        this.world = world;
        this.shaderProgram = shaderProgram;

        init();
    }

    public void init() {
        shaderProgram.use();

        glBindVertexArray(world.getOrbitMesh().getVAO());

        // Model Matrices
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getOrbitMatrixBuffer(), GL_STATIC_DRAW);

        // Model matrix attribute pointers. Note that we need to do this four times,
        // since the maximum size of an attribute is equivalent to a Vector4f. I.e.
        // setting up 4 Vector4fs is equivalent to setting up the Matrix4f. which is the
        // data structure we're trying to send over to our shader.
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 0);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 2 * Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 3 * Renderer.VEC4F_SIZE);

        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);
        glVertexAttribDivisor(6, 1);
        glVertexAttribDivisor(7, 1);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void loop() {
        shaderProgram.use();

        shaderProgram.addUniformVec2f("resolution",
                new Vector2f((float) viewport.getGLCanvas().getWidth(), (float) viewport.getGLCanvas().getHeight()));

        //glClear(GL_DEPTH_BUFFER_BIT);
        glBindVertexArray(world.getOrbitMesh().getVAO());
        glDrawArraysInstanced(GL_LINE_LOOP, 0, world.getOrbitMesh().getVertices().size(), world.getOrbits().size() - 1);
    }
}
