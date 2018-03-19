package main.java.com.raphydaphy.automania.models;

import main.java.com.raphydaphy.automania.renderengine.shader.Material;

public class TexturedModel
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

    public Material getTexture()
    {
        return texture;
    }
}
