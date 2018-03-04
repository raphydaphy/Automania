using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MarchingMapGenerator : MonoBehaviour 
{
    // The size of each chunk, which can be split up again into multiple meshes after being generated
    public const int ChunkSize = 64;
    
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
    public TerrainTypeData Terrain;

    private List<GameObject> Meshes;
    
    private void Awake()
    {
        instance = this;
    }

    private void Start()
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
                var height = heightMap[x, z];
                
                for (var y = 0; y < ChunkSize; y++)
                {
                    // 3D noise values, if we want to implement generation on more than two axes
                    //var fx = x / (ChunkSize - 1f);
                    //var fy = y / (ChunkSize - 1f);
                    //var fz = z / (ChunkSize - 1f);

                    var id = x + y * ChunkSize + z * ChunkSize * ChunkSize;
                    
                    if (y >= Terrain.MeshHeightCurve.Evaluate(height) * Terrain.MeshHeightMultiplier)
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
        
        return new MarchingMapData(heightMap, voxelMap);
    }

    public void GenerateMeshes(MarchingMapData data)
    {
        Meshes = new List<GameObject>();
        
        var vertices = new List<Vector3>();
        var indices = new List<int>();
        
        // Generates an entire chunk mesh, which will likely contain far more than the max vertices in a Unity mesh
        data.Generator.Generate(data.Voxels, ChunkSize, vertices, indices);

        // Calculate the number of meshes we will need to safely draw the vertices
        var numMeshes = vertices.Count / VerticesPerMesh + 1;

        for (var i = 0; i < numMeshes; i++)
        {

            var splitVerts = new List<Vector3>();
            var splitIndices = new List<int>();

            for (int j = 0; j < VerticesPerMesh; j++)
            {
                int idx = i * VerticesPerMesh + j;

                if (idx < vertices.Count)
                {
                    splitVerts.Add(vertices[idx]);
                    splitIndices.Add(j);
                }
            }

            if (splitVerts.Count == 0) continue;

            var splitMesh = new Mesh();
            splitMesh.SetVertices(splitVerts);
            splitMesh.SetTriangles(splitIndices, 0);
            splitMesh.RecalculateBounds();
            splitMesh.RecalculateNormals();
            
            var colors = new Color[splitVerts.Count];

            for (var vert = 0; vert < Mathf.FloorToInt(splitVerts.Count / 3); vert++)
            {
                var height = data.HeightMap[Mathf.RoundToInt(splitVerts[vert * 3].x), Mathf.RoundToInt(splitVerts[vert * 3].z)];
                for (var terrain = 0; terrain < Terrain.Regions.Length; terrain++)
                {
                    if (height >= Terrain.Regions[terrain].Height)
                    {
                        colors[vert * 3] = Terrain.Regions[terrain].Color;
                        colors[vert * 3 + 1] = Terrain.Regions[terrain].Color;
                        colors[vert * 3 + 2 ] = Terrain.Regions[terrain].Color;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            

            // assign the array of colors to the Mesh.
            splitMesh.colors = colors;

            var meshObject = new GameObject("Mesh #" + i);
            meshObject.transform.parent = transform;
            meshObject.AddComponent<MeshFilter>();
            meshObject.AddComponent<MeshRenderer>();
            meshObject.AddComponent<MeshCollider>();
            meshObject.GetComponent<Renderer>().material = MeshMaterial;
            meshObject.GetComponent<MeshFilter>().mesh = splitMesh;
            meshObject.GetComponent<MeshCollider>().sharedMesh = splitMesh;
            meshObject.transform.localPosition = new Vector3(-ChunkSize / 2, -ChunkSize / 2, -ChunkSize / 2);

            Meshes.Add(meshObject);
        }
    }
}

public struct MarchingMapData
{
    public float[,] HeightMap;
    public float[] Voxels;
    public MarchingGenerator Generator;

    public MarchingMapData(float[,] heightMap, float[] voxels)
    {
        HeightMap = heightMap;
        Voxels = voxels;
        
        Generator = new MarchingGenerator();
    }
}