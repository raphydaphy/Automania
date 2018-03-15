using System.Security.Policy;
using UnityEditor.Graphs;
using UnityEngine;

public static class LayeredNoise 
{
    private static OpenSimplexNoise noise = new OpenSimplexNoise(0);

    public static float[,,] GeneratePointCloud(int size, long seed, float scale, int octaves, float persistance,
        float lacunarity, Vector3 offset)
    {
        if (seed != noise.GetSeed())
        {
            noise = new OpenSimplexNoise(seed);
        }
        if (scale <= 0)
        {
            scale = 0.0001f;
        }
        
        var rand = new System.Random(OpenSimplexNoise.FastFloor(seed));
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
        
        var noiseMap = new float[size, size, size];

        var halfSize = size / 2f;

        for (var z = 0; z < size; z++)
        {
            for (var y = 0; y < size; y++)
            {
                for (var x = 0; x < size; x++)
                {
                    amplitude = 1;
                    float frequency = 1;
                    float noiseHeight = 0;

                    for (var i = 0; i < octaves; i++)
                    {
                        var sampleX = (x - halfSize + octaveOffsets[i].x) / scale * frequency;
                        var sampleY = (y - halfSize + octaveOffsets[i].y) / scale * frequency;
                        var sampleZ = (z - halfSize + octaveOffsets[i].z) / scale * frequency;
                        var perlinValue = (float) noise.Evaluate(sampleX, sampleY, sampleZ) * 2 - 1;
                        noiseHeight += perlinValue * amplitude;

                        amplitude *= persistance;
                        frequency *= lacunarity;
                    }

                    noiseMap[x, y, z] = noiseHeight;
                }
            }
        }

        for (var z = 0; z < size; z++)
        {
            for (var y = 0; y < size; y++)
            {
                for (var x = 0; x < size; x++)
                {
                    var normalizedSize = (noiseMap[x, y, z] + 1) / maxHeight;
                    noiseMap[x, y, z] = Mathf.Clamp(normalizedSize, 0, int.MaxValue);
                }
            }
        }

        return noiseMap;
    }
    
    public static float[,] GenerateNoiseMap(int size, long seed, float scale, int octaves, float persistance, float lacunarity, Vector2 offset)
    {
        if (seed != noise.GetSeed())
        {
            noise = new OpenSimplexNoise(seed);
        }
        if (scale <= 0)
        {
            scale = 0.0001f;
        }
        
        var rand = new System.Random(OpenSimplexNoise.FastFloor(seed));
        var octaveOffsets = new Vector2[octaves];

        float maxHeight = 0;
        float amplitude = 1;
        
        for (var i = 0; i < octaves; i++)
        {
            var offX = rand.Next(-100000, 100000) + offset.x;
            var offY = rand.Next(-100000, 100000) - offset.y;
            octaveOffsets[i] = new Vector2(offX, offY);

            maxHeight += amplitude;
            amplitude *= persistance;
        }
        
        var noiseMap = new float[size, size];

        var halfWidth = size / 2f;
        var halfHeight = size / 2f;

        for (var y = 0; y < size; y++)
        {
            for (var x = 0; x < size; x++)
            {
                amplitude = 1;
                float frequency = 1;
                float noiseHeight = 0;
                
                for (var i = 0; i < octaves; i++)
                {
                    var sampleX = (x - halfWidth + octaveOffsets[i].x) / scale * frequency;
                    var sampleY = (y - halfHeight + octaveOffsets[i].y) / scale * frequency;
                    var perlinValue =  (float)Mathf.PerlinNoise(sampleX, sampleY) * 2 - 1;
                    noiseHeight += perlinValue * amplitude;

                    amplitude *= persistance;
                    frequency *= lacunarity;
                }
                
                noiseMap[x, y] = noiseHeight;
            }
        }

        for (var y = 0; y < size; y++)
        {
            for (var x = 0; x < size; x++)
            {
                var normalizedHeight = (noiseMap[x, y] + 1) / maxHeight;
                noiseMap[x, y] = Mathf.Clamp(normalizedHeight, 0, int.MaxValue);
            }
        }

        return noiseMap;
    }
}