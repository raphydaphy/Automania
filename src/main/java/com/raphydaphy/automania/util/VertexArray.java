package main.java.com.raphydaphy.automania.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class VertexArray
{
	private static final int FLOAT_BYTES = 4;
	private static final int INT_BYTES = 4;

	public final int id;
	private List<VertexBuffer> buffers = new ArrayList<>();

	private VertexBuffer indicesBuffer;
	private int indicesLength;

	public static VertexArray create()
	{
		int id = GL30.glGenVertexArrays();
		return new VertexArray(id);
	}

	public VertexArray(int id)
	{
		this.id = id;
	}

	public VertexArray bind(int... attributes)
	{
		bind();

		for (int attribute : attributes)
		{
			GL20.glEnableVertexAttribArray(attribute);
		}

		return this;
	}

	public VertexArray bind()
	{
		GL30.glBindVertexArray(id);
		return this;
	}

	public VertexArray unbind(int... attributes)
	{
		unbind();

		for (int attribute : attributes)
		{
			GL20.glDisableVertexAttribArray(attribute);
		}

		return this;
	}

	public VertexArray unbind()
	{
		GL30.glBindVertexArray(0);
		return this;
	}

	public void delete()
	{
		GL30.glDeleteVertexArrays(id);
		for (VertexBuffer buffer : buffers)
		{
			buffer.delete();
		}
		indicesBuffer.delete();
	}

	public VertexArray storeIndices(int[] indices)
	{
		indicesBuffer = VertexBuffer.create(GL15.GL_ELEMENT_ARRAY_BUFFER).bind().store(indices);
		indicesLength = indices.length;

		return this;
	}

	public VertexArray createAttribute(int index, float[] data, int size)
	{
		VertexBuffer vbo = VertexBuffer.create(GL15.GL_ARRAY_BUFFER).bind().store(data);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, size * FLOAT_BYTES, 0);
		buffers.add(vbo.unbind());

		return this;
	}

	public VertexArray createAttribute(int index, int[] data, int size)
	{
		VertexBuffer vbo = VertexBuffer.create(GL15.GL_ARRAY_BUFFER).bind().store(data);
		GL20.glVertexAttribPointer(index, size, GL11.GL_INT, false,size * INT_BYTES, 0);
		buffers.add(vbo.unbind());

		return this;
	}

	public int getIndicesLength()
	{
		return indicesLength;
	}
}
