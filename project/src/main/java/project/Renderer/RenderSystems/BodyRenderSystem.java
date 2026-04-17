package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.World.World;
import static org.lwjgl.opengl.GL41.*;

/**
 * Class responsible for rendering celestial bodies.
 * 
 * @author Adam Johnston
 */
public class BodyRenderSystem extends RenderSystem {
    /**
     * Vertex buffer object for the colors of the celestial bodies.
     */
    private int vboColors;

    /**
     * Vertex buffer object for the model matrices of the celestial bodies.
     */
    private int vboModelMatrices;

    /**
     * Constructor for the BodyRenderSystem class.
     * 
     * @param world         The world containing the celestial bodies to be
     *                      rendered.
     * @param shaderProgram The shader program for rendering the celestial bodies.
     */
    public BodyRenderSystem(World world, ShaderProgram shaderProgram) {
        super(world, shaderProgram);
    }

    /**
     * Initializes vertex attributes and uniforms for rendering the celestial
     * bodies.
     */
    @Override
    public void init() {
        // Reset vertex buffer objects in case this method is being called after the initial initialization.
        vboColors = 0;
        vboModelMatrices = 0;

        super.getShaderProgram().use();

        // Uniforms for lighting calculations.
        super.getShaderProgram().addUniformVec3f("light_color", super.getWorld().getLightSource().getLightColor());
        super.getShaderProgram().addUniformVec3f("light_position", super.getWorld().getLightSource().getTranslation());

        glBindVertexArray(super.getWorld().getBodyMesh().getVAO());

        // Colors
        vboColors = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, super.getWorld().getColorsBuffer(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(3, 3, GL_FLOAT, false, Renderer.VEC3F_SIZE, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glVertexAttribDivisor(3, 1);

        // Model Matrices
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, super.getWorld().getBodyMatrixBuffer(), GL_STATIC_DRAW);

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
     * Render loop for the celestial bodies. Updates the model transformation
     * matrices and draws the bodies using instanced rendering.
     */
    @Override
    public void loop() {
        super.getShaderProgram().use();

        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, super.getWorld().getBodyMatrixBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Draw bodies (instanced)
        glBindVertexArray(super.getWorld().getBodyMesh().getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, super.getWorld().getBodyMesh().getIndices().size() * 3, GL_UNSIGNED_INT, 0,
                super.getWorld().getBodyObjects().size());
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vboColors);
        glDeleteBuffers(vboModelMatrices);
    }
}
