package project.Renderer.Camera;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import project.Renderer.ControlManager;

/**
 * @author @adamj09
 * 
 *         A camera controller allowing the user to move freely in space without
 *         rolling.
 */
public class FirstPersonCameraController {
    /**
     * Camera
     */
    private Camera camera;

    /**
     * Controls
     */
    private ControlManager controls;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private float translateSpeed = 10.0f;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private float rotateSpeed = 10000;

    /**
     * Camera pitch angle in degrees.
     */
    private double pitch;

    /**
     * Camera yaw angle in degrees.
     */
    private double yaw;

    /**
     * Degrees to limit pitch to.
     */
    private double pitchLimit = 89;

    private Vector3f translation = new Vector3f();

    /**
     * Default constructor.
     * 
     * @param scene  Scene viewed by the camera.
     * @param camera Camera managed by this camera controller.
     */
    public FirstPersonCameraController(Camera camera, ControlManager controls) {
        this.camera = camera;
        this.controls = controls;
    }

    /**
     * Applies translation to the camera depending on the key pressed.
     * 
     * @param deltaTime The time in seconds between the last and current frame
     *                  (used to keep movement speed framerate independent).
     */
    private void translate(float deltaTime) {
        float speed = translateSpeed * deltaTime;

        Vector3f position = camera.getPosition(), direction = camera.getDirection(), up = camera.getUp();
        Vector3f newPosition = new Vector3f(), displacement = new Vector3f();
    
        if (controls.isForwardPressed()) { // Move forward
            direction.mul(speed, displacement);
            position.add(displacement, newPosition);
            camera.setView(newPosition, direction);
        }
        if (controls.isBackwardPressed()) { // Move backward
            direction.mul(speed, displacement);
            position.sub(displacement, newPosition);
            camera.setView(newPosition, direction);
        }  
        if (controls.isLeftPressed()) {
            Vector3f frontCrossUp = new Vector3f();
            direction.cross(up, frontCrossUp);
            frontCrossUp.normalize().mul(speed, displacement);

            position.sub(displacement, newPosition);
            camera.setView(newPosition, direction);
        }  
        if (controls.isRightPressed()) {
            Vector3f frontCrossUp = new Vector3f();
            direction.cross(up, frontCrossUp);
            frontCrossUp.normalize().mul(speed, displacement);

            position.add(displacement, newPosition);
            camera.setView(newPosition, direction);
        }
        if (controls.isUpPressed()) { // Move Up
            up.mul(speed, displacement);
            position.add(displacement, newPosition);
            camera.setView(newPosition, direction);
        }
        if (controls.isDownPressed()) { // Move Up
            up.mul(speed, displacement);
            position.sub(displacement, newPosition);
            camera.setView(newPosition, direction);
        }  
    }

    /**
     * Applies rotation to the camera according to change in cursor position.
     * 
     * @param frameTime   The time in milliseconds between the last and current
     *                    frame
     *                    (used to keep movement speed framerate independent).
     * @param mouseDeltaX The change in the cursor's x-position (normalized change
     *                    is recommended to keep feel consistent across window
     *                    sizes)
     * @param mouseDeltaY The change in the cursor's y-position (normalized change
     *                    is recommended to keep feel consistent across window
     *                    sizes)
     */
    private void rotate(float deltaTime) {
        float pitchDegrees = -(float)Math.clamp(controls.getMouseDeltaYNormalized() * rotateSpeed * deltaTime, -90, 90), 
            yawDegrees = -controls.getMouseDeltaXNormalized() * rotateSpeed * deltaTime; 

        Vector3f pitchAxis = new Vector3f();
        camera.getDirection().cross(camera.getUp(), pitchAxis);

        Quaternionf pitchQuaternion = new Quaternionf();
        pitchQuaternion.setAngleAxis(Math.toRadians(pitchDegrees), pitchAxis.x, pitchAxis.y, pitchAxis.z);

        Vector3f yawAxis = new Vector3f();
        yawAxis.set(camera.getUp());

        Quaternionf yawQuaternion = new Quaternionf();
        yawQuaternion.setAngleAxis(Math.toRadians(yawDegrees), yawAxis.x, yawAxis.y, yawAxis.z);

        Quaternionf rotation = new Quaternionf();
        pitchQuaternion.mul(yawQuaternion, rotation);
        rotation.normalize();

        Vector3f newDirection = new Vector3f();
        camera.getDirection().rotate(rotation, newDirection);

        camera.setView(camera.getPosition(), newDirection);
    }


    public void updateCameraTransform(float deltaTime) {
        rotate(deltaTime);
        translate(deltaTime);
    }

    /**
     * Resets all camera transformations.
     */
    public void reset() {

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
     * @return Camera's pitch in degrees.
     */
    public double getPitch() {
        return this.pitch;
    }

    /**
     * @return Camera's yaw in degrees.
     */
    public double getYaw() {
        return this.yaw;
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
     * @param pitchLimit New pitch limit.
     */
    public void setPitchLimit(double pitchLimit) {
        this.pitchLimit = pitchLimit;
    }
}
