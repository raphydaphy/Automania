package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;

public class UniformMatrix extends Uniform
{
	private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

	public UniformMatrix(String name)
	{
		super(name);
	}

	public void load(Matrix4f value)
	{
		value.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(getLocation(), false, buffer);
	}
}
