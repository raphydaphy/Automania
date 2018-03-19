package main.java.com.raphydaphy.automania.terrain;

import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import org.lwjgl.opengl.GL30;

public class TerrainMesh
{
	private final int vao;
	private final int indicesVBO;
	private final int positionsVBO;
	private final int normalsVBO;
	private final int colorsVBO;

	private int vertexCount;

	public TerrainMesh(float[] positions, float[] normals, float[] colors, int[] indices, Loader loader)
	{
		vao = loader.createVAO();

		indicesVBO = loader.bindIndexBuffer(indices);

		positionsVBO = loader.storeDataInAttributeList(0,3, positions);
		normalsVBO = loader.storeDataInAttributeList(1,3, normals);
		colorsVBO = loader.storeDataInAttributeList(2,3, colors);

		// Unbind the VAO to prevent accidental modification
		loader.unbindVAO();

		vertexCount = indices.length;
	}

	// Put new values in the buffers without having to recreate the model
	public void updateTerrain(float[] positions, float[] normals, float[] colors, int[] indices, Loader loader)
	{
		System.out.println("bad");
		GL30.glBindVertexArray(vao);

		loader.bindIndexBuffer(indicesVBO, indices);

		loader.storeDataInAttributeList(positionsVBO, 0, 3, positions);
		loader.storeDataInAttributeList(normalsVBO, 1, 3, normals);
		loader.storeDataInAttributeList(colorsVBO, 2, 3, colors);

		loader.unbindVAO();

		vertexCount = indices.length;
	}

	public int getVAOID()
	{
		return vao;
	}

	public int getVertexCount()
	{
		return vertexCount;
	}
}
