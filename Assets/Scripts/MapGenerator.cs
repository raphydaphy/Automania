using System;
using System.Collections.Generic;
using UnityEngine;

public class MapGenerator : MonoBehaviour
{
	public int Size = 32;
	public long Seed;
	public float Surface = 0.5f;
	public Vector3 Offset;

	public Renderer textureRenderer;

	private OpenSimplexNoise noise;

	private const int MaxVertsPerMesh = 30000;

	public enum DrawMode
	{
		PointCloud,
		Mesh
	};

	public DrawMode Mode = DrawMode.Mesh;
	
	private List<GameObject> _meshes = new List<GameObject>();

	private float Density(int x, int y, int z, int octaves, float scale, float persistance, float lacunarity, IList<Vector3> octaveOffsets)
	{
		var density = -y / 2f + 6f;
		var halfSize = Size / 2f;
		var amplitude = 1f;
		var frequency = 1f;

		for (var i = 0; i < octaves; i++)
		{
			var sampleX = (x - halfSize + octaveOffsets[i].x) / scale * frequency;
			var sampleY = (y - halfSize + octaveOffsets[i].y) / scale * frequency;
			var sampleZ = (z - halfSize + octaveOffsets[i].z) / scale * frequency;
			var perlinValue = (float)noise.Evaluate(sampleX, sampleY, sampleZ) * 2 - 1;

			density += perlinValue * amplitude;

			amplitude *= persistance;
			frequency *= lacunarity;
		}
		
		return density * halfSize;
	}


	private IList<Vector3> GenOctaveOffsets(int octaves, float persistance, Vector3 offset)
	{
		var rand = new System.Random(OpenSimplexNoise.FastFloor(Seed));
		var octaveOffsets = new Vector3[octaves];

		float maxHeight = 0;
		float amplitude = 1;
        
		for (var i = 0; i < octaves; i++)
		{
			var offX = rand.Next(-100000, 100000) + offset.x;
			var offY = rand.Next(-100000, 100000) - offset.y;
			var offZ = rand.Next(-100000, 100000) + offset.z;
			octaveOffsets[i] = new Vector3(offX, offY, offZ);

			maxHeight += amplitude;
			amplitude *= persistance;
		}

		return octaveOffsets;
	}
	private void Start ()
	{
		noise = new OpenSimplexNoise(Seed);
		
		var voxels = new float[Size * Size * Size];

		const int octaves = 12;
		const float persistance = 0.5f;
		
		var octaveOffsets = GenOctaveOffsets(octaves, persistance, Offset);
		for (var x = 0; x < Size; x++)
		{
			for (var y = 0; y < Size; y++)
			{
				for (var z = 0; z < Size; z++)
				{
					var noiseVal = Density(x, y, z, octaves, 14, persistance, 2, octaveOffsets);
		
					if (Mode == DrawMode.PointCloud)
					{
						if (noiseVal > Surface)
						{
							MakeCube(x, y, z, noiseVal);
						}
					}
					else
					{
						voxels[x + y * Size + z * Size * Size] = noiseVal;
					}
				}
			}
		}

		if (Mode != DrawMode.Mesh)
		{
			return;
		}

		var vertices = new List<Vector3>();
		var indices = new List<int>();
		
		var generator = new MarchingCubesGenerator(Surface);
		
		generator.Generate(voxels, Size, Size, Size, vertices, indices);
		var numMeshes = vertices.Count / MaxVertsPerMesh + 1;

		for (var i = 0; i < numMeshes; i++)
		{

			var splitVerts = new List<Vector3>();
			var splitIndices = new List<int>();

			for (var j = 0; j < MaxVertsPerMesh; j++)
			{
				var idx = i * MaxVertsPerMesh + j;

				if (idx < vertices.Count)
				{
					splitVerts.Add(vertices[idx]);
					splitIndices.Add(j);
				}
			}

			if (splitVerts.Count == 0) continue;

			var mesh = new Mesh();
			mesh.SetVertices(splitVerts);
			mesh.SetTriangles(splitIndices, 0);
			mesh.RecalculateBounds();
			mesh.RecalculateNormals();

			var go = new GameObject("Mesh");
			go.transform.parent = transform;
			go.AddComponent<MeshFilter>();
			go.AddComponent<MeshRenderer>();
			go.GetComponent<Renderer>().material.color = new Color(38 / 255f, 194 / 255f, 129 / 255f);
			go.GetComponent<MeshFilter>().mesh = mesh;
			go.transform.localPosition = new Vector3(-Size / 2, -Size / 2, -Size / 2);

			_meshes.Add(go);
		}
	}

	private void MakeCube(float gridX, float gridY, float gridZ, float scale)
	{
		scale *= 100;
		var plane = GameObject.CreatePrimitive(PrimitiveType.Cube);
		plane.transform.parent = gameObject.transform;
		plane.name = "Voxel";
		plane.GetComponent<Renderer>().material.color = Color.blue;
		plane.transform.position = new Vector3(gridX * 100, gridY * 100, gridZ * 100);
		plane.transform.localScale = new Vector3(scale, scale, scale);
	}
}
