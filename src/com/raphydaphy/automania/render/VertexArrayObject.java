package com.raphydaphy.automania.render;

import org.lwjgl.opengl.GL30;

public class VertexArrayObject
{
	private final int id;

	public VertexArrayObject()
	{
		id = GL30.glGenVertexArrays();
	}

	public void bind()
	{
		GL30.glBindVertexArray(id);
	}

	public void delete()
	{
		GL30.glDeleteVertexArrays(id);
	}

	public int getID()
	{
		return id;
	}
}
