package main.java.com.raphydaphy.automania.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBuffer
{
    private int id;
    public int target;

    public VertexBuffer init()
    {
        id = GL15.glGenBuffers();
        return this;
    }

    public VertexBuffer bind(int target)
    {
        GL15.glBindBuffer(target, id);
        this.target = target;
        return this;
    }

    public VertexBuffer unbind()
    {
        GL15.glBindBuffer(target, 0);
        this.target = 0;
        return this;
    }

    public VertexBuffer upload(float[] data, int usage)
    {
        FloatBuffer buf = BufferUtils.createFloatBuffer(data.length);
        buf.put(data);
        buf.flip();

        return upload(buf, usage);
    }

    public VertexBuffer upload(FloatBuffer data, int usage)
    {
        GL15.glBufferData(target, data, usage);
        return this;
    }

    public VertexBuffer upload(int[] data, int usage)
    {
        IntBuffer buf = BufferUtils.createIntBuffer(data.length);
        buf.put(data);
        buf.flip();

        return upload(buf, usage);
    }

    public VertexBuffer upload(IntBuffer data, int usage)
    {
        GL15.glBufferData(target, data, usage);
        return this;
    }

    public VertexBuffer delete()
    {
        GL15.glDeleteBuffers(id);
        return this;
    }
}
