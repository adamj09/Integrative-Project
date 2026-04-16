package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.World.World;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * Class responsible for rendering the light source.
 * 
 * @author Adam Johnston
 */
public class LightRenderSystem extends RenderSystem {
    /**
     * Buffers for storing the light source's color and model matrix.
     */
    private FloatBuffer colorBuffer, matrixBuffer;

    /**
     * Vertex buffer objects for the light source's color and model matrix.
     */
    private int vboColor, vboModelMatrix;

    /**
     * Constructor for the LightRenderSystem class.
     *
     * @param world         The world containing the light source to be rendered.
     * @param shaderProgram The shader program for rendering the light source.
     */
    public LightRenderSystem(World world, ShaderProgram shaderProgram) {
        super(world, shaderProgram);
    }

    /**
     * Initializes vertex attributes and uniforms for rendering the light source.
     */
    @Override
    public void init() {
        // Reset buffers and vertex buffer objects in case this method is being caalled
        // after the initial initialization.
        vboColor = 0;
        vboModelMatrix = 0;

        if (colorBuffer != null) {
            colorBuffer.clear();
        } else {
            colorBuffer = BufferUtils.createFloatBuffer(3);
        }
        if (matrixBuffer != null) {
            matrixBuffer.clear();
        } else {
            matrixBuffer = BufferUtils.createFloatBuffer(16);
        }

        super.getShaderProgram().use();

        glBindVertexArray(super.getWorld().getLightSource().getMesh().getVAO());

        // Color
        vboColor = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColor);
        glBufferData(GL_ARRAY_BUFFER, super.getWorld().getLightSource().getColor().get(colorBuffer), GL_STATIC_DRAW);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(3, 3, GL_FLOAT, false, Renderer.VEC3F_SIZE, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glVertexAttribDivisor(3, 1);

        // Model Matrix
        vboModelMatrix = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrix);
        glBufferData(GL_ARRAY_BUFFER, super.getWorld().getLightSource().getTransformMatrix().get(matrixBuffer), GL_STATIC_DRAW);

        glBindVertexArray(super.getWorld().getLightSource().getMesh().getVAO());

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
     * Draws the light source.
     */
    @Override
    public void loop() {
        super.getShaderProgram().use();

        glBindVertexArray(super.getWorld().getLightSource().getMesh().getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, super.getWorld().getLightSourceMesh().getIndices().size() * 3, GL_UNSIGNED_INT,
                0, 1);
    }
}
