package project.Renderer.Camera;

import org.joml.Quaternionf;
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
    private float rotateSpeed = 0.005f;

    /**
     * Radians to limit pitch to.
     */
    private float pitchLimit = (float)Math.PI / 2.f;

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

        setCameraView(newPosition, direction);
        controls.setScrollDeltaY(0);
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
    private void rotate() {
        // Greatest absolute pitch change is set to the pitch limit.
        float pitch = -controls.getMouseDeltaYNormalized() * rotateSpeed;
        float yaw = -controls.getMouseDeltaXNormalized() * rotateSpeed;
    
        Quaternionf pitchQuaternion = pitch(pitch);
        Quaternionf yawQuaternion = yaw(yaw);

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

    private Quaternionf pitch(float radians) {
        // Set pitch axis to be perpendicular to the camera's direction and up vectors.
        Vector3f pitchAxis = new Vector3f();
        camera.getDirection().cross(camera.getUp(), pitchAxis);
        pitchAxis.normalize();

        Quaternionf pitchQuaternion = new Quaternionf();

        if(pitch + radians < pitchLimit && pitch + radians > - pitchLimit) {
            pitchQuaternion.setAngleAxis(radians, pitchAxis.x, pitchAxis.y, pitchAxis.z);

            pitch = Math.clamp(pitch + radians, -pitchLimit, pitchLimit);
        }
        else {
            pitchQuaternion.setAngleAxis(0, pitchAxis.x, pitchAxis.y, pitchAxis.z);
        }

        return pitchQuaternion;
    }

    private Quaternionf yaw(float radians) {
        // The yaw axis is relative to the camera. TODO: make sure of this.
        Vector3f yawAxis = new Vector3f();
        yawAxis.set(camera.getUp());

        Quaternionf yawQuaternion = new Quaternionf();
        yawQuaternion.setAngleAxis(radians, yawAxis.x, yawAxis.y, yawAxis.z);
        yaw = (yaw + radians) % (float)(2.f * Math.PI);

        return yawQuaternion;
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
}

