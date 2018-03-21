package main.java.com.raphydaphy.automania.terrain;

import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public enum Region
{
	SAND(6, new Vector3f(210 / 256f, 219 / 256f, 111 / 256f)),
	GRASS(10, new Vector3f(0.0431372549f, 0.91764705882f, 0.23921568627f)),
	FOREST(13, new Vector3f(13 / 255f, 132 / 255f, 21 / 255f)),
	STONE(15, new Vector3f(112 / 255f, 112 / 255f, 112 / 255f)),
	CLIFF(18, new Vector3f(68 / 255f, 68 / 255f, 68 / 255f)),
	SNOW(30, new Vector3f(1, 1, 1));

	private int id;
	private static Map<Integer, Region> regions;
	public final float maxHeight;
	public final Vector3f color;

	static
	{
		int nextID = 0;
		for (Region region : Region.values())
		{
			region.id = nextID++;
		}

		regions = new HashMap<>();

		for (Region region : Region.values())
		{
			regions.put(region.id, region);
		}
	}

	Region(float maxHeight, Vector3f color)
	{
		this.maxHeight = maxHeight;
		this.color = color;
	}

	public int getID()
	{
		return id;
	}

	public static Region getByID(int id)
	{
		if (regions.containsKey(id))
		{
			return regions.get(id);
		}

		return regions.get(regions.size() - 1);
	}

	public static Region getByHeight(float height)
	{
		for (Region region : Region.values())
		{
			if (height < region.maxHeight)
			{
				return region;
			}
		}

		return Region.SNOW;
	}
}