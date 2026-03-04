package project.Renderer.Camera;

import javafx.stage.Stage;
import project.Renderer.Controls;

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
    private Controls controls;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private double translateSpeed = 70;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private double rotateSpeed = 10000;

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

    /**
     * Default constructor.
     * 
     * @param scene  Scene viewed by the camera.
     * @param camera Camera managed by this camera controller.
     */
    public FirstPersonCameraController(Camera camera, Controls controls) {
        this.camera = camera;
        this.controls = controls;
    }

    /**
     * Applies translation to the camera depending on the key pressed.
     * 
     * @param frameTime The time in milliseconds between the last and current frame
     *                  (used to keep movement speed framerate independent).
     */
    private void translate(double deltaTime, Controls controls) {
        double speed = translateSpeed * deltaTime / 1000;

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
    private void rotate(double frameTime, double mouseDeltaX, double mouseDeltaY) {

    }

    /**
     * Rotates the camera around the x-axis by a given number of degrees.
     * 
     * @param degrees Number of degrees to rotate by.
     */
    public void pitch(double degrees) {

    }

    /**
     * Rotates the camera around its relative y-axis by a given number of degrees.
     * 
     * @param degrees Number of degrees to rotate by.
     */
    public void yaw(double degrees) {

    }

    /**
     * Updates the camera's translation and rotation.
     * 
     * @param stage       The stage containing the camera (used for the mouse
     *                    robot).
     * @param frameTime   The time in milliseconds between the last and current
     *                    frame.
     *                    (used to keep movement speed framerate independent).
     * @param mouseDeltaX The change in the cursor's x-position (normalized change
     *                    is recommended to keep feel consistent across window
     *                    sizes).
     * @param mouseDeltaY The change in the cursor's y-position (normalized change
     *                    is recommended to keep feel consistent across window
     *                    sizes).
     */
    public void updateCameraTransform(Stage stage, double frameTime, double mouseDeltaX, double mouseDeltaY, Controls controls) {
        // Apply translation to the camera.
        translate(frameTime, controls);

        // Apply rotation to the camera.
        rotate(frameTime, mouseDeltaX, mouseDeltaY);
    }

    /**
     * Resets all camera transformations.
     */
    public void reset() {

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
    public void setTranslateSpeed(double translateSpeed) {
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
    public void setRotateSpeed(double rotateSpeed) {
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
