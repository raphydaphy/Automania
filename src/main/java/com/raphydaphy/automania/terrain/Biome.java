package main.java.com.raphydaphy.automania.terrain;

import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public enum Biome
{
	BEACH(20f, 1f, 250, 0.5f, 2),
	FOREST(80f, 2f, 250, 0.6f, 2),
	MOUNTAIN(100, 5f, 10, 0.5f, 2.01f);

	private int id;
	private static Map<Integer, Biome> biomes;

	public final float maxHeight;
	public final float heightMultiplier;

	public final float noiseScale;
	public final float noisePersistance;
	public final float noiseLacunarity;

	static
	{
		int nextID = 0;
		for (Biome region : Biome.values())
		{
			region.id = nextID++;
		}

		biomes = new HashMap<>();

		for (Biome region : Biome.values())
		{
			biomes.put(region.id, region);
		}
	}

	Biome(float maxHeight, float heightMultiplier, float noiseScale, float noisePersistance, float noiseLacunarity)
	{
		this.maxHeight = maxHeight;
		this.heightMultiplier = heightMultiplier;

		this.noiseScale = noiseScale;
		this.noisePersistance = noisePersistance;
		this.noiseLacunarity = noiseLacunarity;
	}

	public int getID()
	{
		return id;
	}

	public static Biome getByID(int id)
	{
		if (biomes.containsKey(id))
		{
			return biomes.get(id);
		}

		return biomes.get(biomes.size() - 1);
	}

	// Height ranges between -1 and 1
	public static Biome getByHeight(float height)
	{
		for (Biome region : Biome.values())
		{
			if (height < region.maxHeight)
			{
				return region;
			}
		}

		return Biome.MOUNTAIN;
	}
}