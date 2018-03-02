using System;
using System.Collections.Generic;
using System.Net.Mime;
using System.Threading;
using System.Xml;
using UnityEditor;
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
    public int EditorDetailLevel;
    
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
    
    private Queue<MapThreadInfo<MapData>> _mapDataQueue = new Queue<MapThreadInfo<MapData>>();
    private Queue<MapThreadInfo<MeshData>> _meshDataQueue = new Queue<MapThreadInfo<MeshData>>();
    
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
                display.DrawMesh(MeshGenerator.GenerateTerrainMesh(data.HeightMap, MeshHeightMultiplier, MeshHeightCurve, EditorDetailLevel),
                    TextureGenerator.TextureFromColorMap(data.ColorMap, ChunkSize, ChunkSize));
                break;
            default:
                break;
        }
    }

    public void RequestMapData(Action<MapData> callback)
    {
        ThreadStart threadStart = delegate { MapDataThread(callback); };
        
        new Thread(threadStart).Start();
    }

    private void MapDataThread(Action<MapData> callback)
    {
        var data = GenerateMapData();
        lock (_mapDataQueue)
        {
            _mapDataQueue.Enqueue(new MapThreadInfo<MapData>(callback, data));
        }
    }
    
    public void RequestMeshData(MapData mapData, int lod, Action<MeshData> callback)
    {
        ThreadStart threadStart = delegate { MeshDataThread(mapData, lod, callback); };
        
        new Thread(threadStart).Start();
    }

    private void MeshDataThread(MapData mapData, int lod, Action<MeshData> callback)
    {
        var meshData =
            MeshGenerator.GenerateTerrainMesh(mapData.HeightMap, MeshHeightMultiplier, MeshHeightCurve, lod);
        lock (_meshDataQueue)
        {
            _meshDataQueue.Enqueue(new MapThreadInfo<MeshData>(callback, meshData));
        }
    }

    private void Update()
    {
        if (_mapDataQueue.Count > 0)
        {
            for (var i = 0; i < _mapDataQueue.Count; i++)
            {
                var threadInfo = _mapDataQueue.Dequeue();
                threadInfo.callback(threadInfo.parameter);
            }
        }
        
        if (_meshDataQueue.Count > 0)
        {
            for (var i = 0; i < _meshDataQueue.Count; i++)
            {
                var threadInfo = _meshDataQueue.Dequeue();
                threadInfo.callback(threadInfo.parameter);
            }
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

    private struct MapThreadInfo<T>
    {
        public readonly Action<T> callback;
        public readonly T parameter;

        public MapThreadInfo(Action<T> callback, T parameter)
        {
            this.callback = callback;
            this.parameter = parameter;
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
    public readonly float[,] HeightMap;
    public readonly Color[] ColorMap;

    public MapData(float[,] heightMap, Color[] colorMap)
    {
        HeightMap = heightMap;
        ColorMap = colorMap;
    }
}