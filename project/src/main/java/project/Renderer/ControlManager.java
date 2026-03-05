package project.Renderer;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.MouseInfo;

public class ControlManager {
    private Scene scene;

    /**
     * Camera Controls
     */
    public String forwardKey = "W",
            leftKey = "A",
            backwardKey = "S",
            rightKey = "D",
            upKey = "Space",
            downKey = "Shift";

    private boolean forwardPressed = false,
            leftPressed = false,
            backwardPressed = false,
            rightPressed = false,
            upPressed = false,
            downPressed = false;

    private float mouseDeltaX, mouseDeltaY;
    private float mouseDeltaXNormalized, mouseDeltaYNormalized;

    private float mouseCurrentX = (float) MouseInfo.getPointerInfo().getLocation().getX(),
            mouseCurrentY = (float) MouseInfo.getPointerInfo().getLocation().getY();

    public ControlManager(Scene scene) {
        this.scene = scene;
        setUpKeyboardControls();
    }

    /**
     * Set up keyboard controls callback
     */
    private void setUpKeyboardControls() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode keyCode = event.getCode();

            if (keyCode == KeyCode.getKeyCode(forwardKey)) {
                forwardPressed = true;
            } else if (keyCode == KeyCode.getKeyCode(leftKey)) {
                leftPressed = true;
            } else if (keyCode == KeyCode.getKeyCode(backwardKey)) {
                backwardPressed = true;
            } else if (keyCode == KeyCode.getKeyCode(rightKey)) {
                rightPressed = true;
            } else if (keyCode == KeyCode.getKeyCode(upKey)) {
                upPressed = true;
            } else if (keyCode == KeyCode.getKeyCode(downKey)) {
                downPressed = true;
            }
        });

        scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            KeyCode keyCode = event.getCode();

            if (keyCode == KeyCode.getKeyCode(forwardKey)) {
                forwardPressed = false;
            } else if (keyCode == KeyCode.getKeyCode(leftKey)) {
                leftPressed = false;
            } else if (keyCode == KeyCode.getKeyCode(backwardKey)) {
                backwardPressed = false;
            } else if (keyCode == KeyCode.getKeyCode(rightKey)) {
                rightPressed = false;
            } else if (keyCode == KeyCode.getKeyCode(upKey)) {
                upPressed = false;
            } else if (keyCode == KeyCode.getKeyCode(downKey)) {
                downPressed = false;
            }
        });
    }

    public void updateMousePosition() {
        float mouseNewX = (float) MouseInfo.getPointerInfo().getLocation().getX(),
                mouseNewY = (float) MouseInfo.getPointerInfo().getLocation().getY();

        mouseDeltaX = mouseNewX - mouseCurrentX;
        mouseDeltaY = mouseNewY - mouseCurrentY;

        mouseCurrentX = mouseNewX;
        mouseCurrentY = mouseNewY;

        mouseDeltaXNormalized = mouseDeltaX / (float)scene.getWidth();
        mouseDeltaYNormalized = mouseDeltaY / (float)scene.getHeight();
    }

    public float getMouseDeltaX() {
        return this.mouseDeltaX;
    }

    public float getMouseDeltaY() {
        return this.mouseDeltaY;
    }

    public float getMouseDeltaXNormalized() {
        return this.mouseDeltaXNormalized;
    }

    public float getMouseDeltaYNormalized() {
        return this.mouseDeltaYNormalized;
    }

    public float getMouseCurrentX() {
        return this.mouseCurrentX;
    }

    public float getMouseCurrentY() {
        return this.mouseCurrentY;
    }

    public String getForwardKey() {
        return this.forwardKey;
    }

    public void setForwardKey(String forwardKey) {
        this.forwardKey = forwardKey;
    }

    public String getLeftKey() {
        return this.leftKey;
    }

    public void setLeftKey(String leftKey) {
        this.leftKey = leftKey;
    }

    public String getBackwardKey() {
        return this.backwardKey;
    }

    public void setBackwardKey(String backwardKey) {
        this.backwardKey = backwardKey;
    }

    public String getRightKey() {
        return this.rightKey;
    }

    public void setRightKey(String rightKey) {
        this.rightKey = rightKey;
    }

    public String getDownKey() {
        return this.downKey;
    }

    public void setDownKeyCode(String downKey) {
        this.downKey = downKey;
    }

    public String getUpKeyCode() {
        return this.upKey;
    }

    public void setUpKey(String upKey) {
        this.upKey = upKey;
    }

    public boolean isForwardPressed() {
        return this.forwardPressed;
    }

    public boolean isLeftPressed() {
        return this.leftPressed;
    }

    public boolean isBackwardPressed() {
        return this.backwardPressed;
    }

    public boolean isRightPressed() {
        return this.rightPressed;
    }

    public boolean isDownPressed() {
        return this.downPressed;
    }

    public boolean isUpPressed() {
        return this.upPressed;
    }
}
