package main.java.com.raphydaphy.automania.terrain.biome;

import java.util.HashMap;
import java.util.Map;

public class BiomeRegistry
{
	public static Biome DESERT;
	public static Biome FOREST;
	public static Biome MOUNTAIN;

	private static Map<Integer, Biome> REGISTRY;

	private static Biome highestBiome = null;
	private static int highestOctaveCount;
	private static int nextID;

	public static void init()
	{
		REGISTRY = new HashMap<>();

		DESERT = new DesertBiome();
		FOREST = new ForestBiome();
		MOUNTAIN = new MountainBiome();
	}

	public static void register(Biome biome)
	{
		if (highestBiome == null || biome.maxHeight > highestBiome.maxHeight)
		{
			highestBiome = biome;
		}

		if (biome.noiseOctaves > highestOctaveCount)
		{
			highestOctaveCount = biome.noiseOctaves;
		}
		biome.setID(nextID++);
		REGISTRY.put(biome.getID(), biome);
	}

	public static Biome getByHeight(float height)
	{
		for (Biome region : REGISTRY.values())
		{
			if (height < region.maxHeight)
			{
				return region;
			}
		}

		return highestBiome;
	}

	public static Biome getByID(int id)
	{
		if (REGISTRY.containsKey(id))
		{
			return REGISTRY.get(id);
		}
		return highestBiome;
	}

	public static Biome getHighestBiome()
	{
		return highestBiome;
	}

	public static int getHighestOctaveCount()
	{
		return highestOctaveCount;
	}

}
