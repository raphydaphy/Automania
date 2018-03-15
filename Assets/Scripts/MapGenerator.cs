using System.Collections.Generic;
using JetBrains.Annotations;
using UnityEngine;

public class MapGenerator : MonoBehaviour
{
	public int Size = 32;
	public long Seed;
	public float Surface = 0.5f;

	public bool PointCloudMode;
	
	private List<GameObject> _meshes = new List<GameObject>();
	
	private void Start ()
	{
		var pointCloud = LayeredNoise.GeneratePointCloud(Size, Seed, 10, 5, 0.2f, 2, new Vector3(0, 0, 0));
		var voxels = new float[Size * Size * Size];
		for (var x = 0; x < Size; x++)
		{
			for (var y = 0; y < Size; y++)
			{
				for (var z = 0; z < Size; z++)
				{
					var noiseVal = pointCloud[x, y, z];

					if (PointCloudMode)
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

		if (PointCloudMode)
		{
			return;
		}

		var vertices = new List<Vector3>();
		var indices = new List<int>();
		
		var generator = new MarchingCubesGenerator(Surface);
		
		generator.Generate(voxels, Size, Size, Size, vertices, indices);
		
		var maxVertsPerMesh = 30000; //must be divisible by 3, ie 3 verts == 1 triangle
		var numMeshes = vertices.Count / maxVertsPerMesh + 1;

		for (var i = 0; i < numMeshes; i++)
		{

			var splitVerts = new List<Vector3>();
			var splitIndices = new List<int>();

			for (var j = 0; j < maxVertsPerMesh; j++)
			{
				var idx = i * maxVertsPerMesh + j;

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
