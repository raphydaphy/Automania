package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform
{
	private float value;
	private boolean used = false;

	public UniformFloat(String name)
	{
		super(name);
	}

	public void load(float value)
	{
		if (!used || this.value != value)
		{
			GL20.glUniform1f(getLocation(), value);
			used = true;
			this.value = value;
		}
	}
}
