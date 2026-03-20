package project.Renderer;

import static org.lwjgl.opengl.GL41.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferTools {
    public static void bindUniformBufferObject(int glBuffer, int size, int usage, int location) {
        glBindBuffer(GL_UNIFORM_BUFFER, glBuffer);
        glBufferData(GL_UNIFORM_BUFFER, size, usage);
        glBindBuffer(GL_UNIFORM_BUFFER, location);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, glBuffer, 0, size);
    }

    public static void updateUniformBufferFloatData(int glBuffer, int offset, FloatBuffer data, int location) {
        glBindBuffer(GL_UNIFORM_BUFFER, glBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, offset, data);
        glBindBuffer(GL_UNIFORM_BUFFER, location);
    }

    public static void bindVertexBuffer(int bufferType, int usage, int location, int valueCount, int stride, int glBuffer, FloatBuffer data) {
        glBindBuffer(bufferType, glBuffer);
        glBufferData(bufferType, data, usage);
        glVertexAttribPointer(location, valueCount, GL_FLOAT, false, valueCount * Float.BYTES, 0);
        glEnableVertexAttribArray(location);
    }

    public static void bindElementBuffer(int glBuffer, IntBuffer data, int usage) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, usage);
    }
}
