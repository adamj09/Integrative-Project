package project.Renderer.Camera;

import org.joml.Vector3d;
import org.joml.Vector3f;

import project.ControlManager;
import project.Renderer.Renderer;
import project.Renderer.World.World;
import project.Renderer.World.WorldObject;

/**
 * Class that controls a camera in a first person style (without rolling).
 * 
 * @author Adam Johnston
 */
public class FixedCameraController {
    private World world;
    private Camera camera;
    private ControlManager controls;
    private WorldObject focusedWorldObject;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private double translateSpeed = 0.1d;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private double rotateSpeed = 0.01d;

    /**
     * Radians to limit pitch to.
     */
    private double pitchLimit = Math.toRadians(89.0d);

    /**
     * Camera pitch angle in radians.
     */
    private double pitch;

    /**
     * Camera yaw angle in radians.
     */
    private double yaw;

    private Vector3d lookatPosition = new Vector3d();

    private double radius = 10.d;

    private double minRadius, maxRadius;

    /**
     * Initializes the camera controller with a camera and control manager.
     * 
     * @param camera   Camera to control.
     * @param controls Control manager to get user input from.
     */
    public FixedCameraController(World world, ControlManager controls) {
        this.world = world;
        this.camera = world.getCamera();
        this.controls = controls;
    }

    /**
     * Translates the camera based on user input.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    private void translate(double deltaTime) {
        // Translation is proportional to distance from lookatPosition
        double speed = -controls.getScrollDeltaY() * translateSpeed * deltaTime * radius;

        radius = Math.clamp(radius + speed, minRadius, maxRadius);

        Vector3d newPosition = new Vector3d(
                lookatPosition.x + radius * Math.cos(pitch) * Math.sin(yaw),
                lookatPosition.y + radius * Math.sin(pitch),
                lookatPosition.z + radius * Math.cos(pitch) * Math.cos(yaw));

        Vector3d newDirection = new Vector3d();
        lookatPosition.sub(newPosition, newDirection);

        camera.setView(new Vector3f((float) newPosition.x, (float) newPosition.y, (float) newPosition.z),
                new Vector3f((float) newDirection.x, (float) newDirection.y, (float) newDirection.z));
        controls.setScrollDeltaY(0);
    }

    private void rotate() {
        double pitch = -controls.getMouseDeltaYNormalized() * rotateSpeed;
        double yaw = -controls.getMouseDeltaXNormalized() * rotateSpeed;

        double newPitch = Math.clamp((this.pitch + pitch) % Math.PI, -pitchLimit, pitchLimit);
        double newYaw = ((this.yaw + yaw) % (Math.PI * 2.d));

        Vector3d newPosition = new Vector3d(
                lookatPosition.x + radius * Math.cos(newPitch) * Math.sin(newYaw),
                lookatPosition.y + radius * Math.sin(newPitch),
                lookatPosition.z + radius * Math.cos(newPitch) * Math.cos(newYaw));

        Vector3d newDirection = new Vector3d();
        lookatPosition.sub(newPosition, newDirection);

        // Update camera's view matrix.
        camera.setView(new Vector3f((float) newPosition.x, (float) newPosition.y, (float) newPosition.z),
                new Vector3f((float) newDirection.x, (float) newDirection.y, (float) newDirection.z));

        this.pitch = newPitch;
        this.yaw = newYaw;
    }

    /**
     * Updates the camera's transform by applying rotation and translation based on
     * user input.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    public void updateCameraTransform(double deltaTime) {
        lookatPosition = new Vector3d(focusedWorldObject.getTranslation());

        translate(deltaTime);

        if (controls.isFocusButtonPressed() == 1) {
            rotate();
        }
    }

    /**
     * Resets all camera transformations.
     */
    public void reset() {
        camera.reset();
    }

    /**
     * @return Camera's translation speed.
     */
    public double getTranslateSpeed() {
        return this.translateSpeed;
    }

    /**
     * Set camera's translation speed to specified value.
     * 
     * @param translateSpeed New translate speed.
     */
    public void setTranslateSpeed(float translateSpeed) {
        this.translateSpeed = translateSpeed;
    }

    /**
     * @return Camera's rotation speed.
     */
    public double getRotateSpeed() {
        return this.rotateSpeed;
    }

    /**
     * Set camera's rotation speed to specified value.
     * 
     * @param rotateSpeed New rotate speed.
     */
    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }

    /**
     * @return Camera's pitch limit in degrees.
     */
    public double getPitchLimit() {
        return this.pitchLimit;
    }

    /**
     * Sets a new pitch limit for the camera to adhere to.
     * 
     * @param pitchLimit New pitch limit in degrees.
     */
    public void setPitchLimit(float pitchLimit) {
        this.pitchLimit = pitchLimit;
    }

    public void setFocusObject(String name) {
        if (!world.getBodies().containsKey(name)) {
            return;
        }

        focusedWorldObject = world.getBodies().get(name);

        minRadius = focusedWorldObject.getScale().x + Renderer.DEFAULT_NEAR;
        maxRadius = focusedWorldObject.getScale().x + 100d;
        radius = minRadius + 5.f;

        this.lookatPosition = new Vector3d(focusedWorldObject.getTranslation());

        Vector3d newPosition = new Vector3d(lookatPosition.x + radius, 0.f, lookatPosition.z + radius);

        Vector3d newDirection = new Vector3d();
        lookatPosition.sub(newPosition, newDirection);

        camera.setView(new Vector3f((float) newPosition.x, (float) newPosition.y, (float) newPosition.z),
                new Vector3f((float) newDirection.x, (float) newDirection.y, (float) newDirection.z));
    }
}
