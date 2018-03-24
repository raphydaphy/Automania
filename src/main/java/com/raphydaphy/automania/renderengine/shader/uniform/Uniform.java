package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.opengl.GL20;

public abstract class Uniform
{
	private static final int GL_NOT_FOUND = -1;

	private final String name;
	private int location;

	public Uniform(String name)
	{
		this.name = name;
	}

	public void storeUniformLocation(int program)
	{
		location = GL20.glGetUniformLocation(program, name);
		if (location == GL_NOT_FOUND)
		{
			System.err.println("No uniform variable called " + name + " was found in program #" + program);
		}
	}

	public int getLocation()
	{
		return location;
	}
}
