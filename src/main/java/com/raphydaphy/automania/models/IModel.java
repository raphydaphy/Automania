package main.java.com.raphydaphy.automania.models;

import main.java.com.raphydaphy.automania.renderengine.load.Material;

public interface IModel
{
	public int getVAOID();

	public int getVertexCount();

	public int[] getAttribArrays();

	public Material getTexture();
}
