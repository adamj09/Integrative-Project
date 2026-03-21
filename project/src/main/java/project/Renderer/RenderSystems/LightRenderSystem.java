package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.World.World;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class LightRenderSystem {
    private World world;

    private FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(3), matrixBuffer = BufferUtils.createFloatBuffer(16);

    private int vboColor, vboModelMatrix;

    public LightRenderSystem(World world) {
        this.world = world;

        init();
    }

    public void init() {
        glBindVertexArray(world.getLightSource().getMesh().getVAO());

        // Color
        vboColor = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColor);
        glBufferData(GL_ARRAY_BUFFER, world.getLightSource().getColor().get(colorBuffer), GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, Renderer.VEC3F_SIZE, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glVertexAttribDivisor(1, 1);

        // Model Matrix
        vboModelMatrix = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrix);
        glBufferData(GL_ARRAY_BUFFER, world.getLightSource().getTransformMatrix().get(matrixBuffer), GL_STATIC_DRAW);

        glBindVertexArray(world.getLightSource().getMesh().getVAO());

        // Model matrix attribute pointers. Note that we need to do this four times,
        // since the maximum size of an attribute is equivalent to a Vector4f. I.e.
        // setting up 4 Vector4fs is equivalent to setting up the Matrix4f. which is the
        // data structure we're trying to send over to our shader.
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 0);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 2 * Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 3 * Renderer.VEC4F_SIZE);

        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void loop() {
        glBindVertexArray(world.getLightSource().getMesh().getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, world.getLightSource().getMesh().getIndices().size() * 3, GL_UNSIGNED_INT, 0, 1);
    }
}
