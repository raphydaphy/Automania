using UnityEngine;

public static class Noise 
{
    public static float[,] GenerateNoiseMap(int width, int height, int seed, float scale, int octaves, float persistance, float lacunarity, Vector2 offset)
    {
        if (scale <= 0)
        {
            scale = 0.0001f;
        }
        
        var rand = new System.Random();
        var octaveOffsets = new Vector2[octaves];

        for (var i = 0; i < octaves; i++)
        {
            var offX = rand.Next(-100000, 100000) + offset.x;
            var offY = rand.Next(-100000, 100000) + offset.y;
            octaveOffsets[i] = new Vector2(offX, offY);
        }
        
        var noiseMap = new float[width, height];
        var maxNoiseHeight = float.MinValue;
        var minNoiseHeight = float.MaxValue;

        var halfWidth = width / 2f;
        var halfHeight = height / 2f;

        for (var y = 0; y < height; y++)
        {
            for (var x = 0; x < width; x++)
            {
                float amplitude = 1;
                float frequency = 1;
                float noiseHeight = 0;
                
                for (var i = 0; i < octaves; i++)
                {
                    var sampleX = (x-halfWidth) / scale * frequency + octaveOffsets[i].x;
                    var sampleY = (y-halfHeight) / scale * frequency + octaveOffsets[i].y;
                    var perlinValue =  Mathf.PerlinNoise(sampleX, sampleY) * 2 - 1;
                    noiseHeight += perlinValue * amplitude;

                    amplitude *= persistance;
                    frequency *= lacunarity;
                }

                if (noiseHeight > maxNoiseHeight)
                {
                    maxNoiseHeight = noiseHeight;
                }
                else if (noiseHeight < minNoiseHeight)
                {
                    minNoiseHeight = noiseHeight;
                }
                
                noiseMap[x, y] = noiseHeight;
            }
        }

        for (var y = 0; y < height; y++)
        {
            for (var x = 0; x < width; x++)
            {
                noiseMap[x, y] = Mathf.InverseLerp(minNoiseHeight, maxNoiseHeight, noiseMap[x, y]);
            }
        }

        return noiseMap;
    }
}
