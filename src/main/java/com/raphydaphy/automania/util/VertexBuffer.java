package main.java.com.raphydaphy.automania.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBuffer
{
	private final int id;
	private final int type;

	private VertexBuffer(int id, int type)
	{
		this.id = id;
		this.type = type;
	}

	public static VertexBuffer create(int type)
	{
		int id = GL15.glGenBuffers();
		return new VertexBuffer(id, type);
	}

	public VertexBuffer bind()
	{
		GL15.glBindBuffer(type, id);
		return this;
	}

	public VertexBuffer unbind()
	{
		GL15.glBindBuffer(type, 0);
		return this;
	}

	public void delete()
	{
		GL15.glDeleteBuffers(id);
	}

	public VertexBuffer store(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		store(buffer);

		return this;
	}

	public VertexBuffer store(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		store(buffer);

		return this;
	}

	public VertexBuffer store(Buffer data)
	{
		if (data instanceof IntBuffer)
		{
			GL15.glBufferData(type, (IntBuffer) data, GL15.GL_STATIC_DRAW);
		}
		else if (data instanceof FloatBuffer)
		{
			GL15.glBufferData(type, (FloatBuffer) data, GL15.GL_STATIC_DRAW);
		}
		else
		{
			System.err.println("Tried to store an invalid buffer: " + data.getClass());
		}

		return this;
	}
}
