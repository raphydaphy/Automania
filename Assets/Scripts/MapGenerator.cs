using System;
using System.Collections;
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
        Mesh,
        FalloutMap
    };

    public const int ChunkSize = 239;
    [Range(0, 6)]    
    public int EditorDetailLevel;
    
    public float NoiseScale = 0.3f;

    public int Octaves = 4;
    [Range(0, 1)]
    public float Persistance = 0.5f;
    public float Lacunarity = 2;

    public int Seed = 10;
    public Vector2 Offset = new Vector2(0, 0);

    public bool UseFalloff;
    
    public float MeshHeightMultiplier;
    public AnimationCurve MeshHeightCurve;

    public bool AutoUpdate;

    public DrawMode Mode;
    public TerrainType[] Regions;

    private float[,] _falloffMap;
    
    private Queue<MapThreadInfo<MapData>> _mapDataQueue = new Queue<MapThreadInfo<MapData>>();
    private Queue<MapThreadInfo<MeshData>> _meshDataQueue = new Queue<MapThreadInfo<MeshData>>();

    private void Awake()
    {
        _falloffMap = FalloffGenerator.GenerateFalloff(ChunkSize);
    }
    
    public void DrawMapInEditor()
    {
        var data = GenerateMapData(Vector2.zero);
        
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
            case DrawMode.FalloutMap:
                display.DrawTexture(TextureGenerator.TextureFromHeightMap(FalloffGenerator.GenerateFalloff((ChunkSize))));
                break;
        }
    }

    public void RequestMapData(Vector2 center, Action<MapData> callback)
    {
        ThreadStart threadStart = delegate { MapDataThread(center, callback); };
        
        new Thread(threadStart).Start();
    }

    private void MapDataThread(Vector2 center, Action<MapData> callback)
    {
        var data = GenerateMapData(center);
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

    private MapData GenerateMapData(Vector2 center)
    {
        var noiseMap = Noise.GenerateNoiseMap(ChunkSize + 2, ChunkSize + 2, Seed, NoiseScale, Octaves, Persistance, Lacunarity,
            center + Offset);

        var colorMap = new Color[ChunkSize * ChunkSize];

        for (var y = 0; y < ChunkSize; y++)
        {
            for (var x = 0; x < ChunkSize; x++) 
            {
                if (UseFalloff)
                {
                    noiseMap[x, y] = Mathf.Clamp01(noiseMap[x, y] - _falloffMap[x, y]);
                }
                var currentHeight = noiseMap[x, y];

                for (var i = 0; i < Regions.Length; i++)
                {
                    if (currentHeight >= Regions[i].Height)
                    {
                        colorMap[y * ChunkSize + x] = Regions[i].Color;
                    }
                    else
                    {
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
        
        _falloffMap = FalloffGenerator.GenerateFalloff(ChunkSize);
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