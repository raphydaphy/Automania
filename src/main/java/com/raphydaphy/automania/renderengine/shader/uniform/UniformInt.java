package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.opengl.GL20;

public class UniformInt extends Uniform
{
	private boolean used;
	private int value;

	public UniformInt(String name)
	{
		super(name);
	}

	public void load(int value)
	{
		if (!used || this.value != value)
		{
			GL20.glUniform1i(getLocation(), value);
			used = true;
			this.value = value;
		}
	}
}
