package main.java.com.raphydaphy.automania.terrain;

import main.java.com.raphydaphy.automania.models.TexturedModel;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.render.Transform;
import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import main.java.com.raphydaphy.automania.util.OpenSimplexNoise;
import main.java.com.raphydaphy.automania.util.Pos3;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public class World
{
	private static final int VIEW_DISTANCE = 10;
	public static final int MAX_TERRAINS_PER_FRAME = 1;
	private Map<Pos3, Terrain> chunks;
	private Loader loader;

	public final OpenSimplexNoise noise;
	public final Random rand;
	public final long seed;

	public World(long seed, Loader loader)
	{
		this.loader = loader;
		this.noise = new OpenSimplexNoise(seed);
		this.rand = new Random(seed);
		this.seed = seed;

		chunks = new HashMap<>();
	}

	public void updateWorld(List<ModelTransform> trees, TexturedModel treeModel)
	{
		int processed = 0;
		for (Terrain terrain : chunks.values())
		{
			if (terrain.received && !terrain.populated)
			{
				for (int i = 0; i < Terrain.SIZE / 8; i++)
				{
					Vector3f treePos = new Vector3f(rand.nextInt(Terrain.SIZE) + terrain.getX(), -1f, rand.nextInt(Terrain.SIZE) + terrain.getZ());
					treePos.y = terrain.getExactHeight(treePos.x, treePos.z) - 1;

					if (treePos.y > 0)
					{
						Transform treeTransform = new Transform(treePos, 0, rand.nextInt(360), 0, rand.nextInt(5) + 5);
						trees.add(new ModelTransform(treeTransform, treeModel));
					}
				}
				terrain.populated = true;
			} else if (processed < World.MAX_TERRAINS_PER_FRAME && !terrain.received && terrain.meshesUnprocessed != null)
			{
				List<TerrainMesh> meshes = new ArrayList<>();
				for (TerrainMeshData meshData : terrain.meshesUnprocessed)
				{
					meshes.add(meshData.generateMesh(loader));
				}
				terrain.setMeshes(meshes);
				terrain.received = true;
				processed++;
			}
		}
	}

	public Terrain getChunkFromWorldCoords(Vector3f worldCoords)
	{
		return getChunkFromWorldCoords(worldCoords.x, worldCoords.y, worldCoords.z);
	}

	public Terrain getChunkFromWorldCoords(float worldX, float worldY, float worldZ)
	{
		Pos3 gridPos = new Pos3((int) Math.floor(worldX / (Terrain.SIZE - 1)), (int) Math.floor(worldY / (Terrain.SIZE - 1)), (int) Math.floor(worldZ / (Terrain.SIZE - 1)));
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
		Terrain newChunk = new Terrain(noise, rand, pos.x, pos.y, pos.z, loader);
		chunks.put(pos, newChunk);
	}

	public void updateVisibleChunks(Vector3f viewPosition)
	{
		for (int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++)
		{
			for (int z = -VIEW_DISTANCE; z < VIEW_DISTANCE; z++)
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
