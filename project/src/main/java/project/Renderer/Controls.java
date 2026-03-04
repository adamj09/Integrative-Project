package project.Renderer;

import org.lwjgl.glfw.GLFW;

public class Controls {
    private Renderer renderer;

    /**
     * Camera Controls
     */
    private int forwardKey = GLFW.GLFW_KEY_W;
    private int leftKey = GLFW.GLFW_KEY_A;
    private int backwardKey = GLFW.GLFW_KEY_S;
    private int rightKey = GLFW.GLFW_KEY_D;
    private int downKey = GLFW.GLFW_KEY_LEFT_SHIFT;
    private int upKey = GLFW.GLFW_KEY_SPACE;

    private boolean forwardKeyPressed = false;
    private boolean leftKeyPressed = false;
    private boolean backwardKeyPressed = false;
    private boolean rightKeyPressed = false;
    private boolean downKeyPressed = false;
    private boolean upKeyPressed = false;


    public Controls(Renderer renderer) {
        setUpKeyboardControls();
    }

    private void setUpKeyboardControls() {
        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            if(event.getKey().getCode() == forwardKey) {
                forwardKeyPressed = true;
            }
        });

        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            if(event.getKey().getCode() == leftKey) {
                leftKeyPressed = true;
            }
        });

        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            if(event.getKey().getCode() == backwardKey) {
                backwardKeyPressed = true;
            }
        });

        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            if(event.getKey().getCode() == rightKey) {
                rightKeyPressed = true;
            }
        });

        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            if(event.getKey().getCode() == downKey) {
                downKeyPressed = true;
            }
        });

        renderer.getCanvas().getWindow().getKeyPressedListeners().add(event -> {
            if(event.getKey().getCode() == upKey) {
                upKeyPressed = true;
            }
        });
    }

    public int getForwardKey() {
        return this.forwardKey;
    }

    public void setForwardKey(int forwardKey) {
        this.forwardKey = forwardKey;
    }

    public int getLeftKey() {
        return this.leftKey;
    }

    public void setLeftKey(int leftKey) {
        this.leftKey = leftKey;
    }

    public int getBackwardKey() {
        return this.backwardKey;
    }

    public void setBackwardKey(int backwardKey) {
        this.backwardKey = backwardKey;
    }

    public int getRightKey() {
        return this.rightKey;
    }

    public void setRightKey(int rightKey) {
        this.rightKey = rightKey;
    }

    public int getDownKey() {
        return this.downKey;
    }

    public void setDownKey(int downKey) {
        this.downKey = downKey;
    }

    public int getUpKey() {
        return this.upKey;
    }

    public void setUpKey(int upKey) {
        this.upKey = upKey;
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
