using UnityEditor.Graphs;
using UnityEngine;

public static class Noise 
{
    public static float[,] GenerateNoiseMap(int width, int height, int seed, float scale, int octaves, float persistance, float lacunarity, Vector2 offset)
    {
        if (scale <= 0)
        {
            scale = 0.0001f;
        }
        
        var rand = new System.Random(seed);
        var octaveOffsets = new Vector2[octaves];

        float maxHeight = 0;
        float amplitude = 1;
        float frequency = 1;
        
        for (var i = 0; i < octaves; i++)
        {
            var offX = rand.Next(-100000, 100000) + offset.x;
            var offY = rand.Next(-100000, 100000) - offset.y;
            octaveOffsets[i] = new Vector2(offX, offY);

            maxHeight += amplitude;
            amplitude *= persistance;
        }
        
        var noiseMap = new float[width, height];

        var halfWidth = width / 2f;
        var halfHeight = height / 2f;

        for (var y = 0; y < height; y++)
        {
            for (var x = 0; x < width; x++)
            {
                amplitude = 1;
                frequency = 1;
                float noiseHeight = 0;
                
                for (var i = 0; i < octaves; i++)
                {
                    var sampleX = (x - halfWidth + octaveOffsets[i].x) / scale * frequency;
                    var sampleY = (y - halfHeight + octaveOffsets[i].y) / scale * frequency;
                    var perlinValue =  Mathf.PerlinNoise(sampleX, sampleY) * 2 - 1;
                    noiseHeight += perlinValue * amplitude;

                    amplitude *= persistance;
                    frequency *= lacunarity;
                }
                
                noiseMap[x, y] = noiseHeight;
            }
        }

        for (var y = 0; y < height; y++)
        {
            for (var x = 0; x < width; x++)
            {
                var normalizedHeight = (noiseMap[x, y] + 1) / maxHeight;
                noiseMap[x, y] = Mathf.Clamp(normalizedHeight, 0, int.MaxValue);
            }
        }

        return noiseMap;
    }
}
