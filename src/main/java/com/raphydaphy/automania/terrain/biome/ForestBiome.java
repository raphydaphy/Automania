package main.java.com.raphydaphy.automania.terrain.biome;

public class ForestBiome extends Biome
{
	public ForestBiome()
	{
		super(80f, 2f, 10f, 10, 250, 0.6f, 2,
				new BiomeRegion("Grass", 10, 0.0431372549f, 0.91764705882f, 0.23921568627f),
				new BiomeRegion("Forest", 13, 0.0431372549f, 0.91764705882f, 0.23921568627f));
	}
}
