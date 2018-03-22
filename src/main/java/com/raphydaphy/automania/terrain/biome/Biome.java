package main.java.com.raphydaphy.automania.terrain.biome;

import main.java.com.raphydaphy.automania.terrain.Terrain;
import main.java.com.raphydaphy.automania.util.OpenSimplexNoise;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public class Biome
{

	private int id;

	public final float maxHeight;
	public final float heightMultiplier;
	public final float baseHeight;

	public final int noiseOctaves;
	public final float noiseScale;
	public final float noisePersistance;
	public final float noiseLacunarity;

	private final List<BiomeRegion> regions;

	Biome(float maxHeight, float heightMultiplier, float baseHeight, int noiseOctaves, float noiseScale, float noisePersistance, float noiseLacunarity, BiomeRegion... regions)
	{
		this.maxHeight = maxHeight;
		this.heightMultiplier = heightMultiplier;
		this.baseHeight = baseHeight;

		this.regions = new ArrayList<>();

		for (int reg = 0; reg < regions.length; reg++)
		{
			BiomeRegion region = regions[reg];
			region.setID(reg);
			this.regions.add(region);
		}

		this.noiseOctaves = noiseOctaves;
		this.noiseScale = noiseScale;
		this.noisePersistance = noisePersistance;
		this.noiseLacunarity = noiseLacunarity;

		BiomeRegistry.register(this);
	}

	public void setID(int id)
	{
		this.id = id;
	}

	public int getID()
	{
		return id;
	}

	public BiomeRegion getRegionFromId(int id)
	{
		if (id >= 0 && id < regions.size())
		{
			return regions.get(id);
		}

		return regions.get(regions.size() - 1);
	}

	public BiomeRegion getRegionFromHeight(float height)
	{
		for (BiomeRegion region : regions)
		{
			if (region.maxHeight > height)
			{
				return region;
			}
		}

		return regions.get(regions.size() - 1);
	}

	public float genTerrainDensity(OpenSimplexNoise noise, int x, int y, int z, int octaves, float scale, float persistance, float lacunarity, float baseHeight, Vector3f[] octaveOffsets)
	{
		float density = getBaseDensity(x,y,z);
		float halfSize = Terrain.SIZE / 2f;

		float amplitude = 2f; //  Increasing this makes the terrain more hilly
		float frequency = 1.5f; //  This makes the terrain smoother

		for (int octave = 0; octave < octaves; octave++)
		{
			float sampleX = (x - halfSize + octaveOffsets[octave].x) / scale * frequency;
			float sampleY = (y - halfSize + octaveOffsets[octave].y) / scale * frequency;
			float sampleZ = (z - halfSize + octaveOffsets[octave].z) / scale * frequency;

			float noiseValue = evaluateOctave(noise, sampleX, sampleY, sampleZ);

			density += noiseValue * amplitude;

			amplitude *= persistance;
			frequency *= lacunarity;
		}

		return evaluateNoise(noise, x, y, z, density, halfSize);
	}

	protected float evaluateOctave(OpenSimplexNoise noise, float sampleX, float sampleY, float sampleZ)
	{
		return (float) noise.eval(sampleX, sampleY, sampleZ) * 2 - 1;
	}

	protected float evaluateNoise(OpenSimplexNoise noise, int x, int y, int z, float density, float halfSize)
	{
		return density * halfSize;
	}

	protected float getBaseDensity(int x, int y, int z)
	{
		return -y / 2f + baseHeight;
	}

	public static class BiomeRegion
	{
		public final String name;
		public final float maxHeight;
		public final Vector3f color;

		private int id;

		BiomeRegion(String name, float maxHeight, float r, float g, float b)
		{
			this.name = name;
			this.maxHeight = maxHeight;
			this.color = new Vector3f(r,g,b);
		}

		public void setID(int id)
		{
			this.id = id;
		}

		public int getID()
		{
			return id;
		}
	}
}