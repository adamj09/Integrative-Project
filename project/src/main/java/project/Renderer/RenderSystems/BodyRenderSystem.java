package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.World.World;
import static org.lwjgl.opengl.GL41.*;

public class BodyRenderSystem {
    private World world;

    private int vboColors,
            vboModelMatrices;

    private ShaderProgram shaderProgram;

    public BodyRenderSystem(World world, ShaderProgram shaderProgram) {
        this.world = world;
        this.shaderProgram = shaderProgram;
        init();
    }

    public void init() {
        shaderProgram.use();

        shaderProgram.addUniformVec3f("light_color", world.getLightSource().getLightColor());
        shaderProgram.addUniformVec3f("light_position", world.getLightSource().getTranslation());

        glBindVertexArray(world.getBodyMesh().getVAO());

        // Colors
        vboColors = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, world.getColorsBuffer(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(2, 3, GL_FLOAT, false, Renderer.VEC3F_SIZE, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glVertexAttribDivisor(2, 1);

        // Model Matrices
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getMatricesBuffer(), GL_STATIC_DRAW);

        // Model matrix attribute pointers. Note that we need to do this four times,
        // since the maximum size of an attribute is equivalent to a Vector4f. I.e.
        // setting up 4 Vector4fs is equivalent to setting up the Matrix4f. which is the
        // data structure we're trying to send over to our shader.
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 0);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 2 * Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 3 * Renderer.VEC4F_SIZE);

        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);
        glVertexAttribDivisor(6, 1);

        glBindVertexArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void loop() {
        shaderProgram.use();
        // Update model transformation matrices.
        world.updateMatrixBuffer();

        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getMatricesBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Draw bodies (instanced)
        glBindVertexArray(world.getBodyMesh().getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, world.getBodyMesh().getIndices().size() * 3, GL_UNSIGNED_INT, 0,
                world.getBodies().size());
    }

    public void setWorld(World world) {
        this.world = world;
        vboColors = 0;

        init();
    }
}
