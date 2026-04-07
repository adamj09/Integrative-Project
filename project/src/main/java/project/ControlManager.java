package project;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.MouseInfo;

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

    private int forwardPressed = 0,
            leftPressed = 0,
            backwardPressed = 0,
            rightPressed = 0,
            upPressed = 0,
            downPressed = 0;

    private int focusButtonPressed = 0;

    private float mouseDeltaX, mouseDeltaY;
    private float mouseDeltaXNormalized, mouseDeltaYNormalized;

    private float scrollDeltaY;

    private float mouseCurrentX = (float) MouseInfo.getPointerInfo().getLocation().getX(),
            mouseCurrentY = (float) MouseInfo.getPointerInfo().getLocation().getY();

    public ControlManager(Node focusNode) {
        this.focusNode = focusNode;
        setUpKeyboardControls();
        setUpMouseControls();
    }

    /**
     * Set up keyboard controls callback
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
            scrollDeltaY = (float) event.getDeltaY();
        });
    }

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

    public void updateMouse() {
        float mouseNewX = (float) MouseInfo.getPointerInfo().getLocation().getX(),
                mouseNewY = (float) MouseInfo.getPointerInfo().getLocation().getY();

        mouseDeltaX = mouseNewX - mouseCurrentX;
        mouseDeltaY = mouseNewY - mouseCurrentY;

        mouseCurrentX = mouseNewX;
        mouseCurrentY = mouseNewY;

        mouseDeltaXNormalized = mouseDeltaX / (float) focusNode.getScaleX();
        mouseDeltaYNormalized = mouseDeltaY / (float) focusNode.getScaleY();
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

    public float getScrollDeltaY() {
        return this.scrollDeltaY;
    }

    public void setScrollDeltaY(float scrollDeltaY) {
        this.scrollDeltaY = scrollDeltaY;
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

    public int isForwardPressed() {
        return this.forwardPressed;
    }

    public int isLeftPressed() {
        return this.leftPressed;
    }

    public int isBackwardPressed() {
        return this.backwardPressed;
    }

    public int isRightPressed() {
        return this.rightPressed;
    }

    public int isDownPressed() {
        return this.downPressed;
    }

    public int isUpPressed() {
        return this.upPressed;
    }

    public int isFocusButtonPressed() {
        return this.focusButtonPressed;
    }

    public Node getFocusNode() {
        return focusNode;
    }
}
