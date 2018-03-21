package main.java.com.raphydaphy.automania.terrain;

import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public enum Biome
{
	BEACH(20f, 1f, 250, 0.5f, 2, new BiomeRegion("Sand", 10, 210 / 256f, 219 / 256f, 111 / 256f)), FOREST(80f, 2f, 250, 0.6f, 2, new BiomeRegion("Grass", 10, 0.0431372549f, 0.91764705882f, 0.23921568627f), new BiomeRegion("Forest", 13, 0.0431372549f, 0.91764705882f, 0.23921568627f)), MOUNTAIN(100, 5f, 10, 0.5f, 2.01f, new BiomeRegion("Stone", 15, 112 / 255f, 112 / 255f, 112 / 255f), new BiomeRegion("Cliff", 18, 68 / 255f, 68 / 255f, 68 / 255f));

	private int id;
	private static Map<Integer, Biome> biomes;

	public final float maxHeight;
	public final float heightMultiplier;

	public final float noiseScale;
	public final float noisePersistance;
	public final float noiseLacunarity;

	private final List<BiomeRegion> regions;

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

	Biome(float maxHeight, float heightMultiplier, float noiseScale, float noisePersistance, float noiseLacunarity, BiomeRegion... regions)
	{
		this.maxHeight = maxHeight;
		this.heightMultiplier = heightMultiplier;

		this.regions = new ArrayList<>();

		for (int reg = 0; reg < regions.length; reg++)
		{
			BiomeRegion region = regions[reg];
			region.setID(reg);
			this.regions.add(region);
		}

		this.noiseScale = noiseScale;
		this.noisePersistance = noisePersistance;
		this.noiseLacunarity = noiseLacunarity;
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