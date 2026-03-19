package project.Renderer.RenderSystems;

import project.Renderer.ShaderProgram;
import project.Renderer.Camera.Camera;
import project.Renderer.World.World;

public class SatelliteRenderSystem {
    private ShaderProgram shaderProgram;
    private String vertexShaderPath = "project/shaders/satellites.vert", fragmentShaderPath = "project/shaders/satellites.frag";

    private Camera camera;
    private World world;

    private int VAO, EBO, VBO;
    private int indexCount;

    public SatelliteRenderSystem(World world) {
        this.world = world;
        this.camera = world.getCamera();

        init();
    }

    public void init() {
        shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
        shaderProgram.use();

        setUpBuffers();
    }

    public void setUpBuffers() {
        setUpVertexBuffer();
        setUpIndexBuffer();
        setUpUniforms();
    }

    public void loop(float deltaTime) {
        updateUniforms();
        draw();
    }

    public void setUpVertexBuffer() {

    }

    public void setUpIndexBuffer() {

    }

    public void setUpUniforms() {

    }

    public void updateUniforms() {

    }

    public void draw() {

    }

    public int getEBO() {
        return EBO;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public void setWorld(World world) {
        this.world = world;
        this.camera = world.getCamera();
        VAO = 0;
        VBO = 0;
        EBO = 0;

        setUpBuffers();
    }
}
