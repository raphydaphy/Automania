using System.Collections.Generic;
using UnityEngine;

public class MapGenerator : MonoBehaviour
{
	public int Size = 32;
	public long Seed;
	public float Surface = 0.5f;
	public bool UseFalloff = true;
	public Vector3 Offset;

	public Renderer textureRenderer;

	private const int MaxVertsPerMesh = 30000;

	public enum DrawMode
	{
		PointCloud,
		FalloffMap,
		Mesh
	};

	public DrawMode Mode = DrawMode.Mesh;
	
	private List<GameObject> _meshes = new List<GameObject>();
	
	private void Start ()
	{
		if (Mode == DrawMode.FalloffMap)
		{
			var texture = TextureGenerator.TextureFrom3DHeightMap(FalloffGenerator.GenerateFalloffMap(Size), 2);
			textureRenderer.sharedMaterial.mainTexture = texture;
			textureRenderer.transform.localScale = new Vector3(texture.width, 1, texture.height);
			return;
		}
		var pointCloud = LayeredNoise.GeneratePointCloud(Size, Seed, 10, 5, 0.2f, 2, Offset);
		var voxels = new float[Size * Size * Size];
		var falloff = FalloffGenerator.GenerateFalloffMap(Size);
		for (var x = 0; x < Size; x++)
		{
			for (var y = 0; y < Size; y++)
			{
				for (var z = 0; z < Size; z++)
				{
					if (UseFalloff)
					{
						pointCloud[x, y, z] = Mathf.Clamp(pointCloud[x, y, z] - falloff[x, y, z], 0, 1);
					}
					var noiseVal = pointCloud[x, y, z];

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
			go.GetComponent<Renderer>().material.color = Color.blue;
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
