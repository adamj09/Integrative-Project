package project.Renderer.Camera;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import project.ControlManager;

/**
 * Class that controls a camera in a first person style (without rolling).
 * 
 * @author Adam Johnston
 */
public class FreeLookCameraController {
    private Camera camera;
    private ControlManager controls;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private float translateSpeed = 10.0f;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private float rotateSpeed = 10;

    /**
     * Degrees to limit pitch to.
     */
    private double pitchLimit = 89;

    /**
     * Initializes the camera controller with a camera and control manager.
     * 
     * @param camera   Camera to control.
     * @param controls Control manager to get user input from.
     */
    public FreeLookCameraController(Camera camera, ControlManager controls) {
        this.camera = camera;
        this.controls = controls;
    }

    /**
     * Defines the maximum distance the camera can travel from the origin.
     */
    private float maxDistance = 100.f;

    /**
     * Translates the camera based on user input.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    private void translate(float deltaTime) {
        float speed = translateSpeed * deltaTime;

        Vector3f position = camera.getPosition(), direction = camera.getDirection(), up = camera.getUp();
        Vector3f newPosition = new Vector3f(), displacement = new Vector3f();

        if (controls.isForwardPressed()) { // Move forward
            direction.mul(speed, displacement);
            position.add(displacement, newPosition);

            setCameraView(newPosition, direction);
        }
        if (controls.isBackwardPressed()) { // Move backward
            direction.mul(speed, displacement);
            position.sub(displacement, newPosition);

            setCameraView(newPosition, direction);
        }
        if (controls.isLeftPressed()) {
            Vector3f frontCrossUp = new Vector3f();
            direction.cross(up, frontCrossUp);
            frontCrossUp.normalize().mul(speed, displacement);

            position.sub(displacement, newPosition);

            setCameraView(newPosition, direction);
        }
        if (controls.isRightPressed()) {
            Vector3f frontCrossUp = new Vector3f();
            direction.cross(up, frontCrossUp);
            frontCrossUp.normalize().mul(speed, displacement);

            position.add(displacement, newPosition);

            setCameraView(newPosition, direction);
        }
        if (controls.isUpPressed()) { // Move Up
            up.mul(speed, displacement);
            position.add(displacement, newPosition);

            setCameraView(newPosition, direction);
        }
        if (controls.isDownPressed()) { // Move Up
            up.mul(speed, displacement);
            position.sub(displacement, newPosition);

            setCameraView(newPosition, direction);
        }
    }

    private void setCameraView(Vector3f position, Vector3f direction) {
        if (position.length() > maxDistance) {
            return;
        }
        camera.setView(position, direction);
    }

    /**
     * Applies rotation to the camera using quaternions. Rotation is controlled by
     * mouse movement.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    private void rotate(float deltaTime) {
        // Find degrees in which to rotate based on mouse movement, rotation speed and
        // delta time.
        float pitchDegrees = -(float) Math.clamp(controls.getMouseDeltaYNormalized() * rotateSpeed * deltaTime,
                -pitchLimit, pitchLimit),
                yawDegrees = -controls.getMouseDeltaXNormalized() * rotateSpeed * deltaTime;

        // Set pitch axis to be perpendicular to the camera's direction and up vectors.
        Vector3f pitchAxis = new Vector3f();
        camera.getDirection().cross(camera.getUp(), pitchAxis);

        Quaternionf pitchQuaternion = new Quaternionf();
        pitchQuaternion.setAngleAxis(Math.toRadians(pitchDegrees), pitchAxis.x, pitchAxis.y, pitchAxis.z);

        // In this first person camera, yaw is always around the world's up axis (0, 1,
        // 0) so we can just use the camera's up vector as the yaw axis.
        Vector3f yawAxis = new Vector3f();
        yawAxis.set(camera.getUp());

        Quaternionf yawQuaternion = new Quaternionf();
        yawQuaternion.setAngleAxis(Math.toRadians(yawDegrees), yawAxis.x, yawAxis.y, yawAxis.z);

        // Create rotation quaternion by multiplying pitch and yaw quaternions.
        Quaternionf rotation = new Quaternionf();
        pitchQuaternion.mul(yawQuaternion, rotation);
        rotation.normalize(); // Normalize the quaternion to make sure rotation speed remains consistent
                              // regardless of rotation angle.

        Vector3f newDirection = new Vector3f();
        camera.getDirection().rotate(rotation, newDirection);

        // Update camera's view matrix.
        camera.setView(camera.getPosition(), newDirection);
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
            if (controls.isFocusButtonPressed()) {
                rotate(deltaTime);
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
    public void setPitchLimit(double pitchLimit) {
        this.pitchLimit = pitchLimit;
    }
}
