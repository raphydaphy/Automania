using System;
using System.Collections.Generic;
using System.Threading;
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

    public const int ChunkSize = 95;

    public TerrainTypeData TerrainData;
    public NoiseData NoiseData;
    
    [Range(0, 6)]    
    public int EditorDetailLevel;
    
    public bool AutoUpdate;

    public DrawMode Mode;

    private float[,] _falloffMap;

    public static MapGenerator instance;
    
    private Queue<MapThreadInfo<MapData>> _mapDataQueue = new Queue<MapThreadInfo<MapData>>();
    private Queue<MapThreadInfo<MeshData>> _meshDataQueue = new Queue<MapThreadInfo<MeshData>>();

    private void Awake()
    {
        instance = this;
        _falloffMap = FalloffGenerator.GenerateFalloff(ChunkSize);
    }

    private void OnValuesUpdated()
    {
        if (!Application.isPlaying)
        {
            DrawMapInEditor();
        }
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
                //display.DrawMesh(MeshGenerator.GenerateTerrainMesh(data.HeightMap, TerrainData.MeshHeightMultiplier, TerrainData.MeshHeightCurve, EditorDetailLevel),
                //    TextureGenerator.TextureFromColorMap(data.ColorMap, ChunkSize, ChunkSize));
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
            MeshGenerator.GenerateTerrainMesh(mapData.HeightMap, TerrainData.MeshHeightMultiplier, TerrainData.MeshHeightCurve, lod);
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
        var noiseMap = Noise.GenerateNoiseMap(ChunkSize, ChunkSize, NoiseData.Seed, NoiseData.NoiseScale, NoiseData.Octaves, NoiseData.Persistance, NoiseData.Lacunarity,
            center + NoiseData.Offset);

        var colorMap = new Color[ChunkSize * ChunkSize];

        for (var y = 0; y < ChunkSize; y++)
        {
            for (var x = 0; x < ChunkSize; x++) 
            {
                if (TerrainData.UseFalloff)
                {
                    noiseMap[x, y] = Mathf.Clamp01(noiseMap[x, y] - _falloffMap[x, y]);
                }
                var currentHeight = noiseMap[x, y];

                for (var i = 0; i < TerrainData.Regions.Length; i++)
                {
                    if (currentHeight >= TerrainData.Regions[i].Height)
                    {
                        colorMap[y * ChunkSize + x] = TerrainData.Regions[i].Color;
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
        if (TerrainData != null)
        {
            TerrainData.OnValuesUpdated -= OnValuesUpdated;
            TerrainData.OnValuesUpdated += OnValuesUpdated;
        }

        if (NoiseData != null)
        {
            NoiseData.OnValuesUpdated -= OnValuesUpdated;
            NoiseData.OnValuesUpdated += OnValuesUpdated;
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
    public float[,] HeightMap;
    public Color[] ColorMap;

    public MapData(float[,] heightMap, Color[] colorMap)
    {
        HeightMap = heightMap;
        ColorMap = colorMap;
    }
}