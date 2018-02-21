package com.raphydaphy.automania.render;

import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexBufferObject
{
	private final int id;

	public VertexBufferObject()
	{
		id = GL15.glGenBuffers();
	}

	public void bind(int target)
	{
		GL15.glBindBuffer(target, id);
	}

	public void delete()
	{
		GL15.glDeleteBuffers(id);
	}

	public void uploadData(int target, long size, int usage)
	{
		GL15.glBufferData(target, size, usage);
	}

	public void uploadData(int target, IntBuffer data, int usage)
	{
		GL15.glBufferData(target, data, usage);
	}

	public void uploadSubData(int target, long offset, FloatBuffer data)
	{
		GL15.glBufferSubData(target, offset, data);
	}

	public int getID()
	{
		return id;
	}
}
