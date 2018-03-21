package main.java.com.raphydaphy.automania.terrain;

public class TerrainVoxel
{
	private float density;
	public final Biome biome;
	public final float biomeEdge;

	public TerrainVoxel(float density, Biome biome, float biomeEdge)
	{
		this.density = density;
		this.biome = biome;
		this.biomeEdge = biomeEdge;
	}

	public void setDensity(float density)
	{
		this.density = density;
	}

	public float getDensity()
	{
		return density;
	}
}
