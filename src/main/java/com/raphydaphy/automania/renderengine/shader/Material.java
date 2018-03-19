package main.java.com.raphydaphy.automania.renderengine.shader;

public class Material
{
    private int textureID;

    private boolean hasTransparency = false;
    private boolean artificialLighting = false;

    public boolean usesArtificialLighting()
    {
        return artificialLighting;
    }

    public void setArtificialLighting(boolean artificialLighting)
    {
        this.artificialLighting = artificialLighting;
    }

    public Material(int id)
    {
        this.textureID = id;
    }

    public boolean isTransparent()
    {
        return hasTransparency;
    }

    public void setTransparent(boolean hasTransparency)
    {
        this.hasTransparency = hasTransparency;
    }

    public int getID()

    {
        return textureID;
    }
}
