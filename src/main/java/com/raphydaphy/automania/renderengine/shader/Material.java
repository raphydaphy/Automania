package main.java.com.raphydaphy.automania.renderengine.shader;

public class Material
{
    private int textureID;

    private float shineDamper = 1f;
    private float reflectivity = 0f;

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

    public float getShineDamper()
    {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper)
    {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity()
    {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity)
    {
        this.reflectivity = reflectivity;
    }

    public int getID()

    {
        return textureID;
    }
}
