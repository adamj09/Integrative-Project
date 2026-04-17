package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.World.World;

import static org.lwjgl.opengl.GL41.*;

/**
 * Class responsible for rendering the orbits of celestial bodies.
 * 
 * @author Adam Johnston
 */
public class OrbitRenderSystem extends RenderSystem {

    /**
     * Vertex buffer object for the model matrices of the orbits.
     */
    private int vboModelMatrices;

    /**
     * Constructor for the OrbitRenderSystem class.
     * @param world The world containing the orbits to be rendered.
     * @param shaderProgram The shader program for rendering the orbits.
     */
    public OrbitRenderSystem(World world, ShaderProgram shaderProgram) {
        super(world, shaderProgram);
    }

    /**
     * Initializes vertex attribute for rendering the orbits of celestial bodies.
     */
    @Override
    public void init() {
        // Reset vertex buffer object in case this method is being called after the
        // initial initialization.
        vboModelMatrices = 0;

        super.getShaderProgram().use();

        glBindVertexArray(super.getWorld().getOrbitMesh().getVAO());

        // Model Matrices
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, super.getWorld().getOrbitMatrixBuffer(), GL_STATIC_DRAW);

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

    /**
     * Render loop for the orbits of celestial bodies. Draws the orbits using instanced rendering.
     */
    @Override
    public void loop() {
        super.getShaderProgram().use();

        glBindVertexArray(super.getWorld().getOrbitMesh().getVAO());
        glDrawArraysInstanced(GL_LINE_LOOP, 0, super.getWorld().getOrbitMesh().getVertices().size(),
                super.getWorld().getOrbitObjects().size());
    }
}
