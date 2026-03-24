package project.Renderer.RenderSystems;

import project.Renderer.Renderer;
import project.Renderer.ShaderProgram;
import project.Renderer.Viewport;
import project.Renderer.World.World;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

public class OrbitRenderSystem {
    private Viewport viewport;
    private World world;
    private ShaderProgram shaderProgram;

    private FloatBuffer vertexBuffer;
    private IntBuffer indexBuffer;

    private int VAO, VBO, EBO;

    public OrbitRenderSystem(Viewport viewport, World world, ShaderProgram shaderProgram) {
        this.viewport = viewport;
        this.world = world;
        this.shaderProgram = shaderProgram;

        init();
    }

    public void init() {
        shaderProgram.use();

        float[] vertices = {
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
        };

        vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();

        int[] indices = {
            0, 3, 1,
            1, 3, 2,
        };

        indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices).flip();

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Renderer.VEC3F_SIZE, 0);

        EBO = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        //glBindVertexArray(0);
    }

    public void loop() {
        shaderProgram.use();

        shaderProgram.addUniformVec2f("resolution",
                new Vector2f((float) viewport.getGLCanvas().getWidth(), (float) viewport.getGLCanvas().getHeight()));

        glClear(GL_DEPTH_BUFFER_BIT);
        glBindVertexArray(VAO);
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, world.getBodies().size());
    }
}
