package project.Renderer.Camera;

import org.joml.Vector3f;

import project.ControlManager;
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
    private float translateSpeed = 0.1f;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private float rotateSpeed = 0.01f;

    /**
     * Radians to limit pitch to.
     */
    private float pitchLimit = (float) Math.toRadians(89.0f);

    /**
     * Camera pitch angle in radians.
     */
    private float pitch;

    /**
     * Camera yaw angle in radians.
     */
    private float yaw;

    /**
     * Defines the maximum distance the camera can travel from the origin.
     */
    private float maxDistance = 100.f;

    private Vector3f lookatPosition = new Vector3f();

    private float radius = 10.f;

    private float minRadius;

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
    private void translate(float deltaTime) {
        Vector3f position = camera.getPosition();
        Vector3f direction = camera.getDirection().normalize();
        Vector3f newPosition = new Vector3f();
        Vector3f displacement = new Vector3f();

        // Translation is proportional to distance from lookatPosition

        float speed = controls.getScrollDeltaY() * translateSpeed * deltaTime * radius;

        direction.mul(speed, displacement);
        position.add(displacement, newPosition);

        radius = newPosition.distance(lookatPosition);

        if (position.length() < maxDistance && radius <= minRadius) {
            controls.setScrollDeltaY(0);
            return;
        }

        camera.setView(newPosition, lookatPosition);
        controls.setScrollDeltaY(0);
    }

    /**
     * Applies rotation to the camera using quaternions. Rotation is controlled by
     * mouse movement.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    private void rotate() {
        float pitch = -controls.getMouseDeltaYNormalized() * rotateSpeed;
        float yaw = -controls.getMouseDeltaXNormalized() * rotateSpeed;

        float newPitch = Math.clamp((this.pitch + pitch) % (float) Math.PI, -pitchLimit, pitchLimit);
        float newYaw = (this.yaw + yaw) % ((float) Math.PI * 2.f);

        Vector3f translation = new Vector3f(
                lookatPosition.x + radius * (float) Math.cos(newPitch) * (float) Math.sin(newYaw),
                lookatPosition.y + radius * (float) Math.sin(newPitch),
                lookatPosition.z + radius * (float) Math.cos(newPitch) * (float) Math.cos(newYaw));

        // Update camera's view matrix.
        camera.setView(translation, lookatPosition);

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
    public void updateCameraTransform(float deltaTime) {
        // Update target as object's position updates
        camera.setView(camera.getPosition(), focusedWorldObject.getTranslation());

        if (controls.isFocusButtonPressed() == 1) {
            rotate();
        }
        translate(deltaTime);
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
    public float getTranslateSpeed() {
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
    public float getRotateSpeed() {
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

    public void setLookatPosition(Vector3f lookatPosition) {
        this.lookatPosition = lookatPosition;

        Vector3f newPosition = new Vector3f(lookatPosition.x + radius, 0.f, lookatPosition.z + radius);

        Vector3f target = new Vector3f();
        newPosition.add(camera.getDirection(), target);

        camera.setView(newPosition, lookatPosition);
    }

    public void setFocusObject(String name) {
        // TODO: add check to make sure desired object exists
        focusedWorldObject = world.getBodies().get(name);

        minRadius = focusedWorldObject.getScale().x + 0.5f;
        radius = minRadius + 5.f;
        setLookatPosition(focusedWorldObject.getTranslation());
    }
}
