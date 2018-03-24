package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class UniformVector extends Uniform
{
	private Class<? extends Vector> type;

	public UniformVector(String name, Class<? extends Vector> type)
	{
		super(name);
		this.type = type;
	}

	public void load(Vector value)
	{
		if (!(value.getClass().equals(type)))
		{
			System.err.println("Trying to load invalid vector type " + value.getClass().getName() + " to a uniform variable that only accepts vectors of type " + type.getName());
		}
		
		if (value instanceof Vector4f)
		{
			load(((Vector4f)value).x, ((Vector4f)value).y, ((Vector4f)value).z, ((Vector4f)value).w);
		}
		else if (value instanceof Vector3f)
		{
			load(((Vector3f)value).x, ((Vector3f)value).y, ((Vector3f)value).z);
		}
		else if (value instanceof Vector2f)
		{
			load(((Vector2f)value).x, ((Vector2f)value).y);
		}
	}

	public void load(float... values)
	{
		if (values.length >= 4)
		{
			GL20.glUniform4f(getLocation(), values[0], values[1], values[2], values[3]);
		}
		else if (values.length >= 3)
		{
			GL20.glUniform3f(getLocation(), values[0], values[1], values[2]);
		}
		else if (values.length >= 2)
		{
			GL20.glUniform2f(getLocation(), values[0], values[1]);
		}
	}
}
