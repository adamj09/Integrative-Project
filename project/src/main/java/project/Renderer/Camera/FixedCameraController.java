package project.Renderer.Camera;

import org.joml.Vector3f;

import project.ControlManager;

/**
 * Class that controls a camera in a first person style (without rolling).
 * 
 * @author Adam Johnston
 */
public class FixedCameraController {
    private Camera camera;
    private ControlManager controls;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private float translateSpeed = 0.1f;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private float rotateSpeed = 0.0001f;

    /**
     * Radians to limit pitch to.
     */
    private float pitchLimit = (float) Math.PI / 2.f;

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

    private float distanceToOrigin = 10.f;

    /**
     * Initializes the camera controller with a camera and control manager.
     * 
     * @param camera   Camera to control.
     * @param controls Control manager to get user input from.
     */
    public FixedCameraController(Camera camera, ControlManager controls) {
        this.camera = camera;
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
        float distance = position.distance(lookatPosition);

        float speed = controls.getScrollDeltaY() * translateSpeed * deltaTime * distance;

        direction.mul(speed, displacement);
        position.add(displacement, newPosition);

        if (position.length() > maxDistance) {
            controls.setScrollDeltaY(0);
            return;
        }

        camera.setView(newPosition, lookatPosition, direction);
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
        //TODO: fix this shit
        float pitch = Math.clamp(controls.getMouseDeltaYNormalized() * rotateSpeed, 0, (float)Math.PI * 2 - 0.001f);
        float yaw = Math.clamp(controls.getMouseDeltaXNormalized() * rotateSpeed, 0, (float)Math.PI - 0.001f);


        Vector3f translation = new Vector3f(lookatPosition.x + distanceToOrigin * (float)Math.cos(pitch) * (float)Math.sin(yaw),
                lookatPosition.y + distanceToOrigin * (float)Math.sin(pitch) * (float)Math.sin(yaw), 
                lookatPosition.z + distanceToOrigin * (float)Math.cos(yaw));

        // Update camera's view matrix.
        camera.setView(translation, lookatPosition, camera.getDirection());
    }

    /**
     * Updates the camera's transform by applying rotation and translation based on
     * user input.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    public void updateCameraTransform(float deltaTime) {
        if (controls.getFocusNode().isFocused()) {
            if (controls.isFocusButtonPressed() == 1) {
                rotate();
            }
            translate(deltaTime);
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

        Vector3f newPosition = new Vector3f(lookatPosition.x + 10, 0.f, lookatPosition.z + 10);

        Vector3f target = new Vector3f();
        newPosition.add(camera.getDirection(), target);

        camera.setView(newPosition, lookatPosition, camera.getDirection());
    }
}
