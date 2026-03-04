package project.Renderer;

import org.lwjgl.glfw.*;

public class Controls {
    private Renderer renderer;

    /**
     * Camera Controls
     */
    public int forwardKeyCode = GLFW.GLFW_KEY_W;
    public int leftKeyCode = GLFW.GLFW_KEY_A;
    public int backwardKeyCode = GLFW.GLFW_KEY_S;
    public int rightKeyCode = GLFW.GLFW_KEY_D;
    public int upKeyCode = GLFW.GLFW_KEY_SPACE;
    public int downKeyCode = GLFW.GLFW_KEY_LEFT_SHIFT;

    private boolean forwardKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean backwardKeyPressed = false;
    private boolean rightKeyPressed = false;
    private boolean upKeyPressed = false;
    private boolean downKeyPressed = false;

    public Controls(Renderer renderer) {
        this.renderer = renderer;
        setUpKeyboardControls();
    }

    /**
     * Reset control scheme to defaults.
     */
    private void defaultControlScheme() {
        this.forwardKeyCode = GLFW.GLFW_KEY_W;
        this.leftKeyCode = GLFW.GLFW_KEY_A;
        this.backwardKeyCode = GLFW.GLFW_KEY_S;
        this.rightKeyCode = GLFW.GLFW_KEY_D;
        this.upKeyCode = GLFW.GLFW_KEY_SPACE;
        this.downKeyCode = GLFW.GLFW_KEY_LEFT_SHIFT;
    }

    /**
     * Set up keyboard controls callback
     */
    private void setUpKeyboardControls() {
        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            int keyCode = event.getKey().getCode();

            if(keyCode == forwardKeyCode) {
                forwardKeyPressed = true;
            } else if (keyCode == leftKeyCode) {
                leftKeyPressed = true;
            } else if (keyCode == backwardKeyCode) {
                backwardKeyPressed = true;
            } else if (keyCode == rightKeyCode) {
                rightKeyPressed = true;
            } else if (keyCode == upKeyCode) {
                upKeyPressed = true;
            } else if (keyCode == downKeyCode) {
                downKeyPressed = true;
            }
        });

        renderer.getCanvas().getWindow().getKeyReleasedListeners().add(event -> {
            int keyCode = event.getKey().getCode();

            if(keyCode == forwardKeyCode) {
                forwardKeyPressed = false;
            } else if (keyCode == leftKeyCode) {
                leftKeyPressed = false;
            } else if (keyCode == backwardKeyCode) {
                backwardKeyPressed = false;
            } else if (keyCode == rightKeyCode) {
                rightKeyPressed = false;
            } else if (keyCode == upKeyCode) {
                upKeyPressed = false;
            } else if (keyCode == downKeyCode) {
                downKeyPressed = false;
            }
        });
    }

    public int getForwardKeyCode() {
        return this.forwardKeyCode;
    }

    public void setForwardKeyCode(int forwardKeyCode) {
        this.forwardKeyCode = forwardKeyCode;
    }

    public int getLeftKeyCode() {
        return this.leftKeyCode;
    }

    public void setLeftKeyCode(int leftKeyCode) {
        this.leftKeyCode = leftKeyCode;
    }

    public int getBackwardKeyCode() {
        return this.backwardKeyCode;
    }

    public void setBackwardKeyCode(int backwardKeyCode) {
        this.backwardKeyCode = backwardKeyCode;
    }

    public int getRightKeyCode() {
        return this.rightKeyCode;
    }

    public void setRightKeyCode(int rightKeyCode) {
        this.rightKeyCode = rightKeyCode;
    }

    public int getDownKeyCode() {
        return this.downKeyCode;
    }

    public void setDownKeyCode(int downKeyCode) {
        this.downKeyCode = downKeyCode;
    }

    public int getUpKeyCode() {
        return this.upKeyCode;
    }

    public void setUpKeyCode(int upKeyCode) {
        this.upKeyCode = upKeyCode;
    }

    public boolean isForwardKeyPressed() {
        return this.forwardKeyPressed;
    }

    public boolean isLeftKeyPressed() {
        return this.leftKeyPressed;
    }

    public boolean isBackwardKeyPressed() {
        return this.backwardKeyPressed;
    }

    public boolean isRightKeyPressed() {
        return this.rightKeyPressed;
    }

    public boolean isDownKeyPressed() {
        return this.downKeyPressed;
    }

    public boolean isUpKeyPressed() {
        return this.upKeyPressed;
    }
}
