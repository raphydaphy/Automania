package main.java.com.raphydaphy.learnlwjgl2.terrain;

import main.java.com.raphydaphy.learnlwjgl2.renderengine.load.Loader;
import main.java.com.raphydaphy.learnlwjgl2.util.Pos3;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class World
{
	private static final int VIEW_DISTANCE = 3;
	private Map<Pos3, Terrain> chunks;
	private Loader loader;

	public World(Loader loader)
	{
		this.loader = loader;

		chunks = new HashMap<>();
	}

	public Terrain getChunkFromWorldCoords(Vector3f worldCoords)
	{
		return getChunkFromWorldCoords(worldCoords.x, worldCoords.y, worldCoords.z);
	}

	public Terrain getChunkFromWorldCoords(float worldX, float worldY, float worldZ)
	{
		Pos3 gridPos = new Pos3( (int)Math.floor(worldX / Terrain.SIZE), (int) Math.floor(worldY / Terrain.SIZE), (int) Math.floor(worldZ / Terrain.SIZE));
		gridPos.y = 0;
		if (chunks.containsKey(gridPos))
		{
			return chunks.get(gridPos);
		}
		Terrain newChunk = new Terrain(gridPos.x, gridPos.y, gridPos.z, loader);
		chunks.put(gridPos, newChunk);
		return newChunk;
	}

	public void updateVisibleChunks(Vector3f viewPosition)
	{
		for (int x = - VIEW_DISTANCE; x <= VIEW_DISTANCE; x++)
		{
			for (int z = - VIEW_DISTANCE; z < VIEW_DISTANCE; z++)
			{
				Pos3 chunk = new Pos3(x * (Terrain.SIZE - 1), 0, z * (Terrain.SIZE - 1));
				getChunkFromWorldCoords(chunk.x + viewPosition.x, viewPosition.y, chunk.z + viewPosition.z);
			}
		}
	}

	public Map<Pos3, Terrain> getChunks()
	{
		return chunks;
	}
}
