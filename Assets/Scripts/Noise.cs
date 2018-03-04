using UnityEditor.Graphs;
using UnityEngine;

public static class Noise 
{
    public static float Perlin3D(float x, float y, float z)
    {
        var AB = Mathf.PerlinNoise(x, y);
        var BC = Mathf.PerlinNoise(y, z);
        var AC = Mathf.PerlinNoise(x, z);

        var BA = Mathf.PerlinNoise(y, x);
        var CB = Mathf.PerlinNoise(z, y);
        var CA = Mathf.PerlinNoise(z, x);

        var ABC = AB + BC + AC + BA + CB + CA;

        return ABC / 6f;
    }
    
    public static float[,,] Generate3DNoiseMap(int width, int height, int depth,  int seed, float scale, int octaves, float persistance, float lacunarity, Vector3 offset)
    {
        if (scale <= 0)
        {
            scale = 0.0001f;
        }
        
        var rand = new System.Random(seed);
        var octaveOffsets = new Vector3[octaves];

        float maxHeight = 0;
        float amplitude = 1;
        float frequency = 1;
        
        for (var i = 0; i < octaves; i++)
        {
            var offX = rand.Next(-100000, 100000) + offset.x;
            var offY = rand.Next(-100000, 100000) - offset.y;
            var offZ = rand.Next(-100000, 100000) + offset.z;
            octaveOffsets[i] = new Vector3(offX, offY, offZ);

            maxHeight += amplitude;
            amplitude *= persistance;
        }
        
        var noiseMap = new float[width, height, depth];

        var halfWidth = width / 2f;
        var halfHeight = height / 2f;
        var halfDepth = depth / 2f;

        for (var z = 0; z < depth; z++)
        {
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
                        var sampleZ = (z - halfDepth + octaveOffsets[i].z) / scale * frequency;
                        var perlinValue = Perlin3D(sampleX, sampleY, sampleZ) * 2 - 1;
                        noiseHeight += perlinValue * amplitude;

                        amplitude *= persistance;
                        frequency *= lacunarity;
                    }

                    noiseMap[x, y, z] = noiseHeight;
                }
            }
        }

        for (var z = 0; z < depth; z++)
        {
            for (var y = 0; y < height; y++)
            {
                for (var x = 0; x < width; x++)
                {
                    var normalizedHeight = (noiseMap[x,y,z] + 1) / maxHeight;
                    noiseMap[x, y, z] = Mathf.Clamp(normalizedHeight, 0, int.MaxValue);
                }
            }
        }

        return noiseMap;
    }
    
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
