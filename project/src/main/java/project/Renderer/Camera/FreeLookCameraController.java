package project.Renderer.Camera;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import project.ControlManager;
import project.Renderer.World.World;

/**
 * Class that controls a camera in a first person style (without rolling).
 * 
 * @author Adam Johnston
 */
public class FreeLookCameraController {
    /**
     * Camera transfomations that this class controls.
     */
    private Camera camera;

    /**
     * Control manager to get user input from.
     */
    private ControlManager controls;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private double translateSpeed = 0.f;

    private double maxTranslateSpeed = 10.f;

    /**
     * Scalar dictating speed at which the camera translates.
     */
    private double translateAcceleration = 50.f;

    /**
     * Scalar dictating speed at which the camera rotates.
     */
    private double rotateSpeed = 0.005f;

    /**
     * Radians to limit pitch to.
     */
    private double pitchLimit = (float) Math.PI / 2.f;

    /**
     * Camera pitch angle in radians.
     */
    private double pitch;

    /**
     * Camera yaw angle in radians.
     */
    private double yaw;

    /**
     * Defines the maximum distance the camera can travel from the origin.
     */
    private double maxDistance = 500.f;

    /**
     * Initializes the camera controller with a camera and control manager.
     * 
     * @param camera   Camera to control.
     * @param controls Control manager to get user input from.
     */
    public FreeLookCameraController(World world, ControlManager controls) {
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
        Vector3d position = new Vector3d(camera.getPosition());
        Vector3d direction = new Vector3d(camera.getDirection().normalize());
        Vector3d up = new Vector3d(camera.getUp().normalize());

        Vector3d forward = new Vector3d(Math.sin(yaw), 0.0f, Math.cos(yaw));
        Vector3d right = new Vector3d();
        direction.cross(up, right);
        right.normalize();

        Vector3d displacement = new Vector3d();
        displacement.add(new Vector3d(forward).mul(controls.isBackwardPressed() - controls.isForwardPressed()))
                .add(new Vector3d(right).mul(controls.isRightPressed() - controls.isLeftPressed()))
                .add(new Vector3d(up).mul(controls.isUpPressed() - controls.isDownPressed()));

        if (displacement.equals(0, 0, 0)) {
            translateSpeed = 0.f;
            return;
        }

        translateSpeed = Math.clamp(translateSpeed + translateAcceleration * deltaTime, 0, maxTranslateSpeed);

        displacement.normalize().mul(translateSpeed).mul(deltaTime);

        Vector3d newPosition = new Vector3d();
        position.add(displacement, newPosition);

        // Limit camera distance from origin
        if (newPosition.length() > maxDistance) {
            return;
        }

        camera.setView(new Vector3f((float) newPosition.x, (float) newPosition.y, (float) newPosition.z),
                new Vector3f((float) direction.x, (float) direction.y, (float) direction.z));
    }

    /**
     * Applies rotation to the camera using quaternions. Rotation is controlled by
     * mouse movement.
     */
    private void rotate() {
        double pitch = -controls.getMouseDeltaYNormalized() * rotateSpeed;
        double yaw = -controls.getMouseDeltaXNormalized() * rotateSpeed;

        Quaterniond pitchQuaternion = pitch(pitch);
        Quaterniond yawQuaternion = yaw(yaw);

        // Create rotation quaternion by multiplying pitch and yaw quaternions.
        Quaterniond rotation = new Quaterniond();
        pitchQuaternion.mul(yawQuaternion, rotation);
        rotation.normalize(); // Normalize the quaternion to make sure rotation speed remains consistent
                              // regardless of rotation angle.

        Vector3d newDirection = new Vector3d();
        new Vector3d(camera.getDirection()).rotate(rotation, newDirection);

        // Update camera's view matrix.
        camera.setView(camera.getPosition(),
                new Vector3f((float) newDirection.x, (float) newDirection.y, (float) newDirection.z));
    }

    /**
     * Creates a quaternion representing a rotation around the X-axis (pitch).
     * 
     * @param radians The angle of rotation in radians.
     * @return The resulting quaternion.
     */
    private Quaterniond pitch(double radians) {
        // Set pitch axis to be perpendicular to the camera's direction and up vectors.
        Vector3d pitchAxis = new Vector3d();
        new Vector3d(camera.getDirection()).cross(new Vector3d(camera.getUp()), pitchAxis);
        pitchAxis.normalize();

        Quaterniond pitchQuaternion = new Quaterniond();

        if (pitch + radians < pitchLimit && pitch + radians > -pitchLimit) {
            pitchQuaternion.setAngleAxis(radians, pitchAxis.x, pitchAxis.y, pitchAxis.z);

            pitch = Math.clamp(pitch + radians, -pitchLimit, pitchLimit);
        } else {
            pitchQuaternion.setAngleAxis(0, pitchAxis.x, pitchAxis.y, pitchAxis.z);
        }

        return pitchQuaternion;
    }

    /**
     * Creates a quaternion representing a rotation around the Y-axis (yaw).
     * 
     * @param radians The angle of rotation in radians.
     * @return The resulting quaternion.
     */
    private Quaterniond yaw(double radians) {
        // Yaw is always around the world's up axis (0, 1, 0), since we don't want the camera to roll.
        Vector3d yawAxis = new Vector3d(0, 1, 0);

        Quaterniond yawQuaternion = new Quaterniond();
        yawQuaternion.setAngleAxis(radians, yawAxis.x, yawAxis.y, yawAxis.z);
        yaw = (yaw + radians) % (2.f * Math.PI);

        return yawQuaternion;
    }

    /**
     * Updates the camera's transform by applying rotation and translation based on
     * user input.
     * 
     * @param deltaTime The time in seconds between the last and current frame (used
     *                  to keep movement speed framerate independent).
     */
    public void updateCameraTransform(double deltaTime) {
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
}
