package main.java.com.raphydaphy.automania.terrain;

import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import main.java.com.raphydaphy.automania.util.Pos3;
import org.lwjgl.util.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class World
{
	private static final int VIEW_DISTANCE = 10;
	public static final int MAX_TERRAINS_PER_FRAME = 1;
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
		Pos3 gridPos = new Pos3( (int)Math.floor(worldX / (Terrain.SIZE - 1)), (int) Math.floor(worldY / (Terrain.SIZE - 1)), (int) Math.floor(worldZ / (Terrain.SIZE - 1)));
		gridPos.y = 0;
		if (chunks.containsKey(gridPos))
		{
			return chunks.get(gridPos);
		}
		requestChunk(gridPos, loader);
		return null;
	}

	public void requestChunk(Pos3 pos, Loader loader)
	{
		Terrain newChunk = new Terrain(pos.x, pos.y, pos.z, loader);
		chunks.put(pos, newChunk);
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
