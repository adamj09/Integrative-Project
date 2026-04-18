package project.Renderer.RenderSystems;

import project.Renderer.ShaderProgram;
import project.Renderer.World.World;

/**
 * Abstract class representing the skeleton of a rendering system.
 * 
 * @author Adam Johnston
 */
public abstract class RenderSystem {
    /**
     * World containing the data to be rendered by this render system.
     */
    private World world;

    /**
     * Shader program to be used.
     */
    private ShaderProgram shaderProgram;

    /**
     * Constructor for the RenderSystem class.
     * @param world The world containing the data to be rendered by this render system.
     * @param shaderProgram The shader program to be used by this render system.
     */
    public RenderSystem(World world, ShaderProgram shaderProgram) {
        this.world = world;
        this.shaderProgram = shaderProgram;
        
        init();
    }

    /**
     * Initializes the rendering system.
     */
    public abstract void init();

    /**
     * Main rendering loop.
     */
    public abstract void loop();

    public abstract void dispose();

    /**
     * @return The world to be rendered by this render system.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Sets the world to be rendered by this render system. Note that this method also calls init() to reinitialize the render system with the new world.
     * @param world The world to be rendered by this render system.
     */
    public void setWorld(World world) {
        this.world = world;

        init();
    }

    /**
     * @return The shader program to be used by this render system.
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }
}
