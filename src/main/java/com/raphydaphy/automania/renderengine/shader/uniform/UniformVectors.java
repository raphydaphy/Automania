package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.util.vector.Vector;

public class UniformVectors<T extends Vector> extends Uniform
{
	private final UniformVector[] vectors;

	public UniformVectors(String name, int size)
	{
		super(name);

		vectors = new UniformVector[size];

		for (int vector = 0; vector < size; vector++)
		{
			vectors[vector] = new UniformVector<T>(name + "[" + vector + "]");
		}
	}

	@Override
	public void storeUniformLocation(int program)
	{
		for (UniformVector vector : vectors)
		{
			vector.storeUniformLocation(program);
		}
	}

	public void load(T... vectors)
	{
		for (int vector = 0; vector < vectors.length; vector++)
		{
			// This will work despite the warning because we initialize all the vector arrays using T
			this.vectors[vector].load(vectors[vector]);
		}
	}

	public void load(T vector, int id)
	{
		this.vectors[id].load(vector);
	}
}
