using System.Net.Mime;
using System.Xml;
using UnityEngine;

public class MapGenerator : MonoBehaviour
{
    public enum DrawMode
    {
        NoiseMap,
        ColorMap,
        Mesh
    };

    public const int ChunkSize = 241;
    [Range(0, 6)]    
    public int DetailLevel;
    
    public float NoiseScale = 0.3f;

    public int Octaves = 4;
    [Range(0, 1)]
    public float Persistance = 0.5f;
    public float Lacunarity = 2;

    public int Seed = 10;
    public Vector2 Offset = new Vector2(0, 0);
    
    public float MeshHeightMultiplier;
    public AnimationCurve MeshHeightCurve;

    public bool AutoUpdate;

    public DrawMode Mode;
    public TerrainType[] Regions;

    public void DrawMapInEditor()
    {
        MapData data = GenerateMapData();
        
        var display = FindObjectOfType<MapDisplay>();

        switch(Mode)
        {
            case DrawMode.NoiseMap:
                display.DrawTexture(TextureGenerator.TextureFromHeightMap(data.HeightMap));
                break;
            case DrawMode.ColorMap:
                display.DrawTexture(TextureGenerator.TextureFromColorMap(data.ColorMap, ChunkSize, ChunkSize));
                break;
            case DrawMode.Mesh:
                display.DrawMesh(MeshGenerator.GenerateTerrainMesh(data.HeightMap, MeshHeightMultiplier, MeshHeightCurve, DetailLevel),
                    TextureGenerator.TextureFromColorMap(data.ColorMap, ChunkSize, ChunkSize));
                break;
            default:
                break;
        }
    }
    
    private MapData GenerateMapData()
    {
        var noiseMap = Noise.GenerateNoiseMap(ChunkSize, ChunkSize, Seed, NoiseScale, Octaves, Persistance, Lacunarity,
            Offset);

        var colorMap = new Color[ChunkSize * ChunkSize];

        for (var y = 0; y < ChunkSize; y++)
        {
            for (var x = 0; x < ChunkSize; x++)
            {
                var currentHeight = noiseMap[x, y];

                for (var i = 0; i < Regions.Length; i++)
                {
                    if (currentHeight <= Regions[i].Height)
                    {
                        colorMap[y * ChunkSize + x] = Regions[i].Color;
                        break;
                    }
                }
            }
        }

        return new MapData(noiseMap, colorMap);
    }

    private void OnValidate()
    {
        if (Octaves < 0)
        {
            Octaves = 0;
        }

        if (Lacunarity < 1)
        {
            Lacunarity = 1;
        }
    }
}

[System.Serializable]
public struct TerrainType
{
    public string Name;
    public float Height;
    public Color Color;
}

public struct MapData
{
    public float[,] HeightMap;
    public Color[] ColorMap;

    public MapData(float[,] heightMap, Color[] colorMap)
    {
        HeightMap = heightMap;
        ColorMap = colorMap;
    }
}