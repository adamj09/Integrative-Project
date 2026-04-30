package oms;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.MouseInfo;

/**
 * Class used to take input from the user, using the JavaFX node provided in the
 * constructor. Mouse movement is recorded using AWT (not JavaFX) so that mouse
 * position can be calculated apart from the provided JavaFX node. This is
 * useful if we'd like to know changes in mouse position while the current node
 * isn't focused.
 * 
 * @author Adam Johnston
 */
public class ControlManager {
    private Node focusNode;

    /**
     * Camera Controls
     */
    public String forwardKey = "W",
            leftKey = "A",
            backwardKey = "S",
            rightKey = "D",
            upKey = "Space",
            downKey = "Shift";

    /**
     * int values representing whether each key is pressed. Booleans could equally
     * be used here,
     * but ints are more convenient for use in the camera controllers.
     * 
     * A value of 0 means the binding is not active; a value of 1 means the binding
     * is active.
     * 
     * @see project.Renderer.Camera.FixedCameraController
     * @see project.Renderer.Camera.FreeLookCameraController
     */
    private int forwardPressed = 0,
            leftPressed = 0,
            backwardPressed = 0,
            rightPressed = 0,
            upPressed = 0,
            downPressed = 0,
            focusButtonPressed = 0;

    /**
     * Differences in mouse position between previous and current update.
     */
    private double mouseDeltaX, mouseDeltaY;

    /**
     * Differences in mouse position between previous and current update, taking
     * into account the size of the JavaFX node. This is useful if we'd like changes
     * in mouse position relative to the JavaFX node to stay consistent regardless
     * of node size.
     */
    private double mouseDeltaXNormalized, mouseDeltaYNormalized;

    /**
     * Difference in vertical scroll distance between previous and current update.
     */
    private double scrollDeltaY;

    /**
     * Latest mouse position.
     */
    private double mouseCurrentX = MouseInfo.getPointerInfo().getLocation().getX(),
            mouseCurrentY = MouseInfo.getPointerInfo().getLocation().getY();

    /**
     * Creates a ControlManager object with a JavaFX Node.
     * 
     * @param focusNode the JavaFX node via which controls will be recorded.
     */
    public ControlManager(Node focusNode) {
        this.focusNode = focusNode;
        setUpKeyboardControls();
        setUpMouseControls();
    }

    /**
     * Sets up keyboard controls callback.
     */
    private void setUpKeyboardControls() {
        focusNode.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();

            if (keyCode == KeyCode.getKeyCode(forwardKey)) {
                forwardPressed = 1;
            }
            if (keyCode == KeyCode.getKeyCode(leftKey)) {
                leftPressed = 1;
            }
            if (keyCode == KeyCode.getKeyCode(backwardKey)) {
                backwardPressed = 1;
            }
            if (keyCode == KeyCode.getKeyCode(rightKey)) {
                rightPressed = 1;
            }
            if (keyCode == KeyCode.getKeyCode(upKey)) {
                upPressed = 1;
            }
            if (keyCode == KeyCode.getKeyCode(downKey)) {
                downPressed = 1;
            }
        });

        focusNode.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            KeyCode keyCode = event.getCode();

            if (keyCode == KeyCode.getKeyCode(forwardKey)) {
                forwardPressed = 0;
            }
            if (keyCode == KeyCode.getKeyCode(leftKey)) {
                leftPressed = 0;
            }
            if (keyCode == KeyCode.getKeyCode(backwardKey)) {
                backwardPressed = 0;
            }
            if (keyCode == KeyCode.getKeyCode(rightKey)) {
                rightPressed = 0;
            }
            if (keyCode == KeyCode.getKeyCode(upKey)) {
                upPressed = 0;
            }
            if (keyCode == KeyCode.getKeyCode(downKey)) {
                downPressed = 0;
            }
        });
    }

    /**
     * Sets up mouse controls callback.
     */
    private void setUpMouseControls() {
        focusNode.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                focusButtonPressed = 1;
                focusNode.requestFocus();
            }
        });

        focusNode.setOnMouseReleased(event -> {
            if (!event.isPrimaryButtonDown()) {
                focusButtonPressed = 0;
            }
        });

        focusNode.setOnScroll(event -> {
            scrollDeltaY = event.getDeltaY();
        });
    }

    /**
     * Checks if the JavaFX Node is no longer focused; if so, reset all control
     * values except for focusButtonPressed to 0. This prevents inadvertently
     * persisting the active state of bindings when the node is no longer focused.
     */
    public void handleUnfocus() {
        if (!focusNode.isFocused()) {
            forwardPressed = 0;
            leftPressed = 0;
            backwardPressed = 0;
            rightPressed = 0;
            upPressed = 0;
            downPressed = 0;
        }
    }

    /**
     * Updates all values related to the mouse if the JavaFX Node is focused.
     */
    public void updateMouse() {
        if (focusNode.isFocused()) {
            double mouseNewX = MouseInfo.getPointerInfo().getLocation().getX(),
                    mouseNewY = MouseInfo.getPointerInfo().getLocation().getY();

            mouseDeltaX = mouseNewX - mouseCurrentX;
            mouseDeltaY = mouseNewY - mouseCurrentY;

            mouseCurrentX = mouseNewX;
            mouseCurrentY = mouseNewY;

            mouseDeltaXNormalized = mouseDeltaX / focusNode.getScaleX();
            mouseDeltaYNormalized = mouseDeltaY / focusNode.getScaleY();
        }
    }

    /**
     * @return mouseDeltaX.
     */
    public double getMouseDeltaX() {
        return this.mouseDeltaX;
    }

    /**
     * @return mouseDeltaY.
     */
    public double getMouseDeltaY() {
        return this.mouseDeltaY;
    }

    /**
     * @return mouseDeltaXNormalized.
     */
    public double getMouseDeltaXNormalized() {
        return this.mouseDeltaXNormalized;
    }

    /**
     * @return mouseDeltaYNormalized.
     */
    public double getMouseDeltaYNormalized() {
        return this.mouseDeltaYNormalized;
    }

    /**
     * @return mouseCurrentX.
     */
    public double getMouseCurrentX() {
        return this.mouseCurrentX;
    }

    /**
     * @return mouseCurrentY.
     */
    public double getMouseCurrentY() {
        return this.mouseCurrentY;
    }

    /**
     * @return scrollDeltaY.
     */
    public double getScrollDeltaY() {
        return this.scrollDeltaY;
    }

    /**
     * Sets the scrollDetltaY value.
     * 
     * @param scrollDeltaY value to which scrollDeltaY should be set.
     */
    public void setScrollDeltaY(double scrollDeltaY) {
        this.scrollDeltaY = scrollDeltaY;
    }

    /**
     * @return forwardKey value.
     */
    public String getForwardKey() {
        return this.forwardKey;
    }

    /**
     * Sets the forwardKey value.
     * 
     * @param forwardKey value to which forwardKey should be set.
     */
    public void setForwardKey(String forwardKey) {
        this.forwardKey = forwardKey;
    }

    /**
     * @return leftKey value.
     */
    public String getLeftKey() {
        return this.leftKey;
    }

    /**
     * Sets the leftKey value.
     * 
     * @param leftKey value to which leftKey should be set.
     */
    public void setLeftKey(String leftKey) {
        this.leftKey = leftKey;
    }

    /**
     * @return backwardKey value.
     */
    public String getBackwardKey() {
        return this.backwardKey;
    }

    /**
     * Sets the backwardKey value.
     * 
     * @param backwardKey value to which backwardKey should be set.
     */
    public void setBackwardKey(String backwardKey) {
        this.backwardKey = backwardKey;
    }

    /**
     * @return rightKey value.
     */
    public String getRightKey() {
        return this.rightKey;
    }

    /**
     * Sets the rightKey value.
     * 
     * @param rightKey value to which rightKey should be set.
     */
    public void setRightKey(String rightKey) {
        this.rightKey = rightKey;
    }

    /**
     * @return downKey value.
     */
    public String getDownKey() {
        return this.downKey;
    }

    /**
     * Sets the downKey value.
     * 
     * @param downKey value to which downKey should be set.
     */
    public void setDownKey(String downKey) {
        this.downKey = downKey;
    }

    /**
     * @return upKey value.
     */
    public String getUpKey() {
        return this.upKey;
    }

    /**
     * Sets the upKey value.
     * 
     * @param upKey value to which upKey should be set.
     */
    public void setUpKey(String upKey) {
        this.upKey = upKey;
    }

    /**
     * @return forwardPressed.
     */
    public int isForwardPressed() {
        return this.forwardPressed;
    }

    /**
     * @return leftPressed.
     */
    public int isLeftPressed() {
        return this.leftPressed;
    }

    /**
     * @return backwardPressed.
     */
    public int isBackwardPressed() {
        return this.backwardPressed;
    }

    /**
     * @return rightPressed.
     */
    public int isRightPressed() {
        return this.rightPressed;
    }

    /**
     * @return downPressed.
     */

    public int isDownPressed() {
        return this.downPressed;
    }

    /**
     * @return upPressed.
     */
    public int isUpPressed() {
        return this.upPressed;
    }

    /**
     * @return focusButtonPressed.
     */
    public int isFocusButtonPressed() {
        return this.focusButtonPressed;
    }

    /**
     * @return focusNode.
     */
    public Node getFocusNode() {
        return focusNode;
    }
}
