package main.java.com.raphydaphy.automania.terrain;

import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import main.java.com.raphydaphy.automania.util.MathUtils;
import main.java.com.raphydaphy.automania.util.OpenSimplexNoise;
import main.java.com.raphydaphy.automania.util.Pos3;
import org.lwjgl.util.vector.Vector3f;

import java.util.*;

public class Terrain
{
	public static final int SIZE = 33;
	private static final int MAX_VERTS_PER_MESH = 30000;

	private final float x;
	private final float y;
	private final float z;

	private float[] voxels;

	private List<TerrainMesh> meshes;

	private Map<Pos3, List<Vector3f[]>> triangles;

	public boolean received = false;
	public boolean populated = false;

	public List<TerrainMeshData> meshesUnprocessed = null;

	private OpenSimplexNoise noise;
	private Random rand = new Random(0);

	public Terrain(OpenSimplexNoise noise, long seed, int gridX, int gridY, int gridZ, Loader loader)
	{
		this.noise = noise;
		this.rand = new Random(seed);

		this.x = gridX * (SIZE - 1);
		this.y = gridY * (SIZE - 1);
		this.z = gridZ * (SIZE - 1);

		new Thread(new Runnable() {
			@Override
			public void run()
			{
				meshesUnprocessed = generateMeshData();
			}
		}, "Terrain Generator").start();
	}

	public float getX()
	{
		return x;
	}

	public float getZ()
	{
		return z;
	}

	public List<TerrainMesh> getMeshes()
	{
		return meshes;
	}

	public void setMeshes(List<TerrainMesh> meshes)
	{
		this.meshes = meshes;
	}

	private float genDensity(int x, int y, int z, int octaves, float scale, float persistance, float lacunarity, Vector3f[] octaveOffsets)
	{
		float density = -y / 2f + 10f;
		float halfSize = SIZE / 2f;
		float amplitude = 2f; //  Increasing this makes the terrain more hilly
		float frequency = 1.5f; //  This makes the terrain smoother

		for (int octave = 0; octave < octaves; octave++)
		{
			float sampleX = (x - halfSize + octaveOffsets[octave].x) / scale * frequency;
			float sampleY = (y - halfSize + octaveOffsets[octave].y) / scale * frequency;
			float sampleZ = (z - halfSize + octaveOffsets[octave].z) / scale * frequency;

			float noiseValue = (float)noise.eval(sampleX, sampleY, sampleZ) * 2 - 1;

			density += noiseValue * amplitude;

			amplitude *= persistance;
			frequency *= lacunarity;
		}

		return density * halfSize;
	}

	public void regenerateTerrain(Loader loader)
	{
		List<TerrainMesh> meshes = new ArrayList<>();

		for (TerrainMeshData meshData : generateMeshData())
		{
			meshes.add(meshData.generateMesh(loader));
		}

		this.meshes = meshes;
	}

	public float getDensity(int x, int y, int z)
	{
		if (x >= 0 && y >= 0 && z >= 0 && x < SIZE - 1 && y < SIZE - 1 && z < SIZE - 1)
		{
			return voxels[x + y * SIZE + z * SIZE * SIZE];
		}

		return 0;
	}

	public boolean setDensity(int x, int y, int z, float density)
	{
		if (x >= 0 && y >= 0 && z >= 0 && x < SIZE - 1 && y < SIZE - 1 && z < SIZE - 1)
		{
			voxels[x + y * SIZE + z * SIZE * SIZE] = density;
			return true;
		}
		return false;
	}

	private Vector3f[] generateOctaveOffsets(int octaves, float persistance, Vector3f offset)
	{
		Vector3f[] octaveOffsets = new Vector3f[octaves];

		float maxHeight = 0;
		float amplitude = 1;

		for (int octave = 0; octave < octaves; octave++)
		{
			float offsetX = rand.nextInt(200000) - 100000 + offset.x;
			float offsetY = rand.nextInt(200000) - 100000 - offset.y;
			float offsetZ = rand.nextInt(200000) - 100000 + offset.z;

			octaveOffsets[octave] = new Vector3f(offsetX, offsetY, offsetZ);

			maxHeight += amplitude;
			amplitude *= persistance;
		}

		return octaveOffsets;
	}


	private List<TerrainMeshData> generateMeshData()
	{
		Vector3f offset = new Vector3f(x,y,z);
		if (voxels == null)
		{
			voxels = new float[SIZE * SIZE * SIZE];
			final int octaves = 12;
			final float persistance = 0.6f;

			Vector3f[] octaveOffsets = generateOctaveOffsets(octaves, persistance, offset);

			for (int x = 0; x < SIZE; x++)
			{
				for (int y = 0; y < SIZE; y++)
				{
					for (int z = 0; z < SIZE; z++)
					{
						float density = genDensity(x, y, z, octaves, 250, persistance, 2.01f, octaveOffsets);

						voxels[x + y * SIZE + z * SIZE * SIZE] = density;
					}
				}
			}
		}
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Vector3f> colors = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();

		MarchingCubesGenerator generator = new MarchingCubesGenerator();

		// triangles should have SIZE - 1 ^ 3 entries in it, one for every voxel except the last x, y and z rows
		triangles = new HashMap<>();
		generator.generateMesh(voxels, SIZE, SIZE, SIZE, vertices, normals, colors, indices, triangles);

		int numMeshes = vertices.size() / MAX_VERTS_PER_MESH + 1;

		List<TerrainMeshData> models = new ArrayList<>();

		for (int mesh = 0; mesh < numMeshes; mesh++)
		{
			List<Vector3f> splitVertices = new ArrayList<>();
			List<Vector3f> splitNormals = new ArrayList<>();
			List<Vector3f> splitColors = new ArrayList<>();
			List<Integer> splitIndices = new ArrayList<>();

			for (int vertex = 0; vertex < MAX_VERTS_PER_MESH; vertex++)
			{
				int index = mesh * MAX_VERTS_PER_MESH + vertex;

				if (index < vertices.size())
				{
					splitVertices.add(vertices.get(index));
					splitNormals.add(normals.get(index));
					splitColors.add(colors.get(index));
					splitIndices.add(indices.get(index));
				}
			}

			if (splitVertices.size() == 0)
			{
				continue;
			}

			float[] splitVerticesArray = new float[splitVertices.size() * 3];
			float[] splitNormalsArray = new float[splitNormals.size() * 3];
			float[] splitColorsArray = new float[splitColors.size() * 3];
			int[] splitIndicesArray = new int[splitIndices.size()];

			for (int index = 0; index < splitIndices.size(); index++)
			{
				splitVerticesArray[index * 3] = splitVertices.get(index).x;
				splitVerticesArray[index * 3 + 1] = splitVertices.get(index).y;
				splitVerticesArray[index * 3 + 2] = splitVertices.get(index).z;

				splitNormalsArray[index * 3] = splitNormals.get(index).x;
				splitNormalsArray[index * 3 + 1] = splitNormals.get(index).y;
				splitNormalsArray[index * 3 + 2] = splitNormals.get(index).z;

				splitColorsArray[index * 3] = splitColors.get(index).x;
				splitColorsArray[index * 3 + 1] = splitColors.get(index).y;
				splitColorsArray[index * 3 + 2] = splitColors.get(index).z;

				splitIndicesArray[index] = indices.get(index);
			}
			if (models.size() < mesh)
			{
				//models.get(mesh).updateTerrain(splitVerticesArray, splitNormalsArray, splitColorsArray, splitIndicesArray, loader);
			}
			else
			{
				models.add(new TerrainMeshData(splitVerticesArray,  splitNormalsArray, splitColorsArray,splitIndicesArray));
			}
		}

		return models;
	}

	public float getExactHeight(float worldX, float worldZ)
	{
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;


		if (terrainX < 0 || terrainZ < 0 || terrainX >= SIZE - 1 || terrainZ >= SIZE - 1)
		{
			return 0;
		}
		float gridSquareSize = SIZE / ((float) SIZE - 2);
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

		for (int worldY = (int)y + SIZE - 1; worldY >= y; worldY--)
		{
			float closest2D = Float.MAX_VALUE;
			Vector3f[] closestTri2D = null;
			Pos3 currentPos = new Pos3(Math.round(terrainX), Math.round(worldY), Math.round(terrainZ));
			if (!triangles.containsKey(currentPos))
			{
				continue;
			}
			for (Vector3f[] triangle : triangles.get(currentPos))
			{
				float avgX = 0;
				float avgZ = 0;

				for (Vector3f vertex : triangle)
				{
					avgX += vertex.x;
					avgZ += vertex.z;
				}

				avgX /= 3f;
				avgZ /= 3f;

				float dist = Math.abs(avgX - terrainX) + Math.abs(avgZ - terrainZ);

				if (closest2D > dist)
				{
					closest2D = dist;
					closestTri2D = triangle;
				}
			}

			if (closestTri2D != null)
			{
				return MathUtils.barryCentric(new Vector3f(0, closestTri2D[0].y, 0), new Vector3f(0, closestTri2D[1].y, 1), new Vector3f(1, closestTri2D[2].y, 0), xCoord, zCoord);
			}
		}

		return 0;
	}

	public int getHighestVoxelCoord(int innerX, int innerZ)
	{
		for (int y = Terrain.SIZE - 2; y > 0; y--)
		{
			float density = getDensity(innerX, y, innerZ);
			if (density > 0.5f)
			{
				return y;
			}
		}
		return Integer.MAX_VALUE;
	}
}