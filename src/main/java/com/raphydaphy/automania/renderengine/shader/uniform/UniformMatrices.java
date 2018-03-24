package main.java.com.raphydaphy.automania.renderengine.shader.uniform;

import org.lwjgl.util.vector.Matrix4f;

public class UniformMatrices extends Uniform
{
	private UniformMatrix[] matrices;

	public UniformMatrices(String name, int size)
	{
		super(name);

		matrices = new UniformMatrix[size];

		for (int matrix = 0; matrix < size; matrix++)
		{
			matrices[matrix] = new UniformMatrix(name + "[" + matrix + "]");
		}
	}

	@Override
	public void storeUniformLocation(int program)
	{
		for (UniformMatrix matrix : matrices)
		{
			matrix.storeUniformLocation(program);
		}
	}

	public void load(Matrix4f... matrices)
	{
		for (int matrix = 0; matrix < matrices.length; matrix++)
		{
			this.matrices[matrix].load(matrices[matrix]);
		}
	}
}
