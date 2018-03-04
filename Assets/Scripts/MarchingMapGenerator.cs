using System.Collections;
using System.Collections.Generic;
using System.Security.Policy;
using UnityEngine;
using UnityEngine.Experimental.UIElements.StyleEnums;

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
        var heightMap2D = Noise.GenerateNoiseMap(ChunkSize, ChunkSize, Seed, Scale, Octaves, Persistance, Lacunarity,
            center + Offset);
        var heightMap3D = Noise.Generate3DNoiseMap(ChunkSize, ChunkSize, ChunkSize, Seed, Scale, Octaves, Persistance,
            Lacunarity, new Vector3(center.x + Offset.x, 0, center.y + Offset.y));
        var colorMap = new Color[ChunkSize * ChunkSize];

        var voxelMap = new float[ChunkSize * ChunkSize * ChunkSize];

        for (var x = 0; x < ChunkSize; x++)
        {
            for (var z = 0; z < ChunkSize; z++)
            {
                
                var height2D = heightMap2D[x, z];
                var modifiedHeight = Mathf.Max(Terrain.MeshHeightCurve.Evaluate(height2D) * Terrain.MeshHeightMultiplier, 0) + 50;
                
                for (var terrain = 0; terrain < Terrain.Regions.Length; terrain++)
                {
                    if (height2D >= Terrain.Regions[terrain].Height)
                    {
                        colorMap[z * ChunkSize + x] = Terrain.Regions[terrain].Color;
                    }
                    else
                    {
                        break;
                    }
                }
                
                for (var y = 0; y < ChunkSize; y++)
                {
                    var height3D = heightMap3D[x, y, z];
                    var id = x + y * ChunkSize + z * ChunkSize * ChunkSize;

                    if (y >= modifiedHeight)
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
        
        return new MarchingMapData(heightMap2D, colorMap, voxelMap);
    }

    public void GenerateMeshes(MarchingMapData mapData)
    {
        Meshes = new List<GameObject>();
        
        var meshData = new MarchingMeshData();
        
        // Generates an entire chunk mesh, which will likely contain far more than the max vertices in a Unity mesh
        mapData.Generator.Generate(mapData.Voxels, ChunkSize, meshData);

        meshData.Flatten();
        
        // Calculate the number of meshes we will need to safely draw the vertices
        var numMeshes = meshData.FlatVertices.Length / VerticesPerMesh + 1;
        

        for (var i = 0; i < numMeshes; i++)
        {

            var splitVerts = new Vector3[meshData.FlatTriangles.Length];
            var splitIndices = new int[meshData.FlatTriangles.Length];
            var splitUVs = new Vector2[meshData.FlatTriangles.Length];

            for (var j = 0; j < VerticesPerMesh; j++)
            {
                var idx = i * VerticesPerMesh + j;

                if (idx < meshData.FlatVertices.Length)
                {
                    splitVerts[j] = meshData.FlatVertices[idx];
                    splitUVs[j] = meshData.FlatUVs[idx];
                    splitIndices[j] = j;
                }
            }

            if (splitVerts.Length == 0) continue;

            var splitMesh = new Mesh
            {
                vertices = splitVerts,
                triangles = splitIndices,
                uv = splitUVs
            };
            
            splitMesh.RecalculateBounds();
            splitMesh.RecalculateNormals();
            /*
            var colors = new Color[splitVerts.Length];

           

            for (var vert = 0; vert < splitVerts.Length; vert++)
            {
                var height = mapData.HeightMap[Mathf.RoundToInt(splitVerts[vert].x), Mathf.RoundToInt(splitVerts[vert].z)];
                for (var terrain = 0; terrain < Terrain.Regions.Length; terrain++)
                {
                    if (height >= Terrain.Regions[terrain].Height)
                    {
                        colors[vert] = Terrain.Regions[terrain].Color;
                    }
                    else
                    {
                        break;
                    }
                }
            }

            // assign the array of colors to the Mesh.
            splitMesh.colors = colors;
*/
            var meshObject = new GameObject("Mesh #" + i);
            meshObject.transform.parent = transform;
            meshObject.AddComponent<MeshFilter>();
            meshObject.AddComponent<MeshRenderer>();
            meshObject.AddComponent<MeshCollider>();
            meshObject.GetComponent<Renderer>().material = MeshMaterial;
            meshObject.GetComponent<MeshFilter>().mesh = splitMesh;
            meshObject.GetComponent<MeshRenderer>().material.mainTexture =
                TextureGenerator.TextureFromColorMap(mapData.ColorMap, ChunkSize, ChunkSize);
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
    public Color[] ColorMap;
    public MarchingGenerator Generator;

    public MarchingMapData(float[,] heightMap, Color[] colorMap, float[] voxels)
    {
        HeightMap = heightMap;
        Voxels = voxels;
        ColorMap = colorMap;
        
        Generator = new MarchingGenerator();
    }
}

public class MarchingMeshData
{
    public List<Vector3> Vertices;
    public List<int> Triangles;
    public List<Vector2> UVs;

    public Vector3[] FlatVertices;
    public int[] FlatTriangles;
    public Vector2[] FlatUVs;

    public MarchingMeshData()
    {
        Vertices = new List<Vector3>();
        Triangles = new List<int>();
        UVs = new List<Vector2>();

    }

    public void AddTriangle(int a, int b, int c)
    {
        Triangles.Add(a);
        Triangles.Add(b);
        Triangles.Add(c);
        
    }

    public void AddVertex(Vector3 position, Vector2 uv)
    {
        Vertices.Add(position);
        UVs.Add(uv);
        Triangles.Add(Vertices.Count - 1);
    }

    public void Flatten()
    {
        FlatVertices = new Vector3[Triangles.Count];
        FlatUVs = new Vector2[Triangles.Count];
        FlatTriangles = new int[Triangles.Count];

        for (var i = 0; i < Triangles.Count; i++)
        {
            FlatVertices[i] = Vertices[Triangles[i]];
            FlatUVs[i] = UVs[Triangles[i]];
            FlatTriangles[i] = i;
        }
    }
}