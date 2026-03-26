package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.Viewport;
import project.Renderer.World.World;
import static org.lwjgl.opengl.GL41.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class BodyRenderSystem {
    private World world;
    private Viewport viewport;

    private int vboColors,
            vboModelMatrices;

    private int framebuffer, framebufferTexture;
    private int renderBuffer;

    private ShaderProgram shaderProgram;

    public BodyRenderSystem(Viewport viewport, World world, ShaderProgram shaderProgram) {
        this.viewport = viewport;
        this.world = world;
        this.shaderProgram = shaderProgram;
        init();
    }

    public void init() {
        shaderProgram.use();

        shaderProgram.addUniformVec3f("light_color", world.getLightSource().getLightColor());
        shaderProgram.addUniformVec3f("light_position", world.getLightSource().getTranslation());

        int width = (int)viewport.getGLCanvas().getWidth(), height = (int)viewport.getGLCanvas().getHeight();

        // Create framebuffer
        framebuffer = glGenFramebuffers();

        framebufferTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(0));
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // Attach texture to framebuffer.
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, framebufferTexture, 0);

        renderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        glBindVertexArray(world.getBodyMesh().getVAO());

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Framebuffer is not complete!");
            System.exit(1);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // Colors
        vboColors = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboColors);
        glBufferData(GL_ARRAY_BUFFER, world.getColorsBuffer(), GL_STATIC_DRAW);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(3, 3, GL_FLOAT, false, Renderer.VEC3F_SIZE, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glVertexAttribDivisor(3, 1);

        // Model Matrices
        vboModelMatrices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getMatricesBuffer(), GL_STATIC_DRAW);

        // Model matrix attribute pointers. Note that we need to do this four times,
        // since the maximum size of an attribute is equivalent to a Vector4f. I.e.
        // setting up 4 Vector4fs is equivalent to setting up the Matrix4f. which is the
        // data structure we're trying to send over to our shader.
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 0);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, Renderer.MAT4F_SIZE, 2 * Renderer.VEC4F_SIZE);
        glEnableVertexAttribArray(6);
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
        // Update model transformation matrices.
        world.updateMatrixBuffer();

        glBindBuffer(GL_ARRAY_BUFFER, vboModelMatrices);
        glBufferData(GL_ARRAY_BUFFER, world.getMatricesBuffer(), GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        // Draw bodies (instanced)
        glBindVertexArray(world.getBodyMesh().getVAO());
        glDrawElementsInstanced(GL_TRIANGLES, world.getBodyMesh().getIndices().size() * 3, GL_UNSIGNED_INT, 0,
                world.getBodies().size());

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void setWorld(World world) {
        this.world = world;
        vboColors = 0;

        init();
    }
}
