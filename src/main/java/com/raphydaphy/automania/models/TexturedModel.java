package main.java.com.raphydaphy.automania.models;

import main.java.com.raphydaphy.automania.renderengine.shader.Material;

public class TexturedModel implements IModel
{
    private RawModel rawModel;
    private Material texture;

    public TexturedModel(RawModel rawModel, Material texture)
    {
        this.rawModel = rawModel;
        this.texture = texture;
    }

    public RawModel getRawModel()
    {
        return rawModel;
    }

    @Override
    public Material getTexture()
    {
        return texture;
    }

    @Override
    public int getVAOID()
    {
        return rawModel.getVAOID();
    }

	@Override
	public int getVertexCount()
	{
		return rawModel.getVertexCount();
	}

	@Override
	public int[] getAttribArrays()
	{
		return new int[] { 0, 2 };
	}
}
