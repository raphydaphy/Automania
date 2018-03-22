package main.java.com.raphydaphy.automania.terrain.biome;

public class DesertBiome extends Biome
{
	public DesertBiome()
	{
		super(20f, 1f, 10f, 12, 250, 0.5f, 2,
				new BiomeRegion("Sand", 10, 210 / 256f, 219 / 256f, 111 / 256f));
	}
}
