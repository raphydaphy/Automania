using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MarchingMapGenerator : MonoBehaviour 
{
    // The size of each chunk, which can be split up again into multiple meshes after being generated
    public const int ChunkSize = 32;
    
    // This number must be divisible by 3 since three vertices are needed to make up a triangle
    public const int VerticesPerMesh = 30000;
    
    public static MarchingMapGenerator instance;

    public int Seed = 0;
    public float Scale = 10;
    public int Octaves = 8;
    public float Persistance = 0.5f;
    public float Lacunarity = 2;
    public Vector2 Offset = new Vector2(0, 0);

    public Material MeshMaterial;

    private List<GameObject> Meshes;
    
    private void Awake()
    {
        instance = this;
    }

    public void Start()
    {
        GenerateMeshes(GenerateMapData(Offset));
    }

    public MarchingMapData GenerateMapData(Vector2 center)
    {
        var heightMap = Noise.GenerateNoiseMap(ChunkSize, ChunkSize, Seed, Scale, Octaves, Persistance, Lacunarity,
            center + Offset);

        var voxelMap = new float[ChunkSize * ChunkSize * ChunkSize];

        for (var x = 0; x < ChunkSize; x++)
        {
            for (var z = 0; z < ChunkSize; z++)
            {
                var height = heightMap[x, z] * ChunkSize;
                for (var y = 0; y < ChunkSize; y++)
                {
                    var fx = x / (ChunkSize - 1f);
                    var fy = y / (ChunkSize - 1f);
                    var fz = z / (ChunkSize - 1f);

                    var id = x + y * ChunkSize + z * ChunkSize * ChunkSize;
                    
                    if (y <= height)
                    {
                        voxelMap[id] = 1f;
                    }
                    else
                    {
                        voxelMap[id] = 0f;
                    }
                }
            }
        }
        
        return new MarchingMapData(voxelMap);
    }

    public void GenerateMeshes(MarchingMapData data)
    {
        Meshes = new List<GameObject>();
        
        var vertices = new List<Vector3>();
        var indices = new List<int>();
        
        // Generates an entire chunk mesh, which will likely contain far more than the max vertices in a Unity mesh
        data.Generator.Generate(data.Voxels, ChunkSize, vertices, indices);

        // Calculate the number of meshes we will need to safely draw the vertices
        var meshCount = vertices.Count / VerticesPerMesh + 1;

        for (var i = 0; i < meshCount; i++)
        {
            var splitVertices = new List<Vector3>();
            var splitIndices = new List<int>();

            for (var vertex = 0; vertex < VerticesPerMesh; vertex++)
            {
                var index = i * VerticesPerMesh + vertex;

                if (index < vertices.Count)
                {
                    splitVertices.Add(vertices[vertex]);
                    splitIndices.Add(vertex);
                }
            }

            if (splitVertices.Count == 0)
            {
                continue;
            }

            var splitMesh = new Mesh();
            
            splitMesh.SetVertices(splitVertices);
            splitMesh.SetTriangles(splitIndices, 0);
            
            splitMesh.RecalculateBounds();
            splitMesh.RecalculateNormals();
            
            var meshObject = new GameObject("Sub-Mesh #" + i);
            meshObject.transform.parent = transform;
            var filter = meshObject.AddComponent<MeshFilter>();
            var renderer = meshObject.AddComponent<MeshRenderer>();

            renderer.sharedMaterial = MeshMaterial;
            filter.sharedMesh = splitMesh;
            
            meshObject.transform.localPosition = new Vector3(-ChunkSize / 2, -ChunkSize / 2, -ChunkSize / 2);

            Meshes.Add(meshObject);
        }
    }

}

public struct MarchingMapData
{
    public float[] Voxels;
    public MarchingGenerator Generator;

    public MarchingMapData(float[] voxels)
    {
        Voxels = voxels;
        
        Generator = new MarchingGenerator();
    }
}