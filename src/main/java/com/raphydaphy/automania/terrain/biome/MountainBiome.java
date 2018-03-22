package main.java.com.raphydaphy.automania.terrain.biome;

import main.java.com.raphydaphy.automania.util.OpenSimplexNoise;

import java.util.Random;

public class MountainBiome extends Biome
{
	public MountainBiome()
	{
		super(100, 5f, 10f, 8, 200, 0.5f, 2.01f,
				new BiomeRegion("Stone", 15, 112 / 255f, 112 / 255f, 112 / 255f),
				new BiomeRegion("Cliff", 18, 68 / 255f, 68 / 255f, 68 / 255f));
	}

	@Override
	protected float evaluateOctave(OpenSimplexNoise noise, float sampleX, float sampleY, float sampleZ)
	{
		return (float) noise.eval(sampleX, sampleY, sampleZ);
	}

	@Override
	protected float evaluateNoise(OpenSimplexNoise noise, int x, int y, int z, float density, float halfSize)
	{

		return density * halfSize;
	}

	@Override
	protected float getBaseDensity(int x, int y, int z)
	{
		return -y / 2f + baseHeight + 5 * (y / 20f);
	}
}
