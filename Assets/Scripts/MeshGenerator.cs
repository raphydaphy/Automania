using System;
using UnityEngine;

public static class MeshGenerator 
{
    public static MeshData GenerateTerrainMesh(float[,] heightMap, float heightMultiplier, AnimationCurve heightSizeCurve, int detailLevel)
    {
        var safeHeightCurve = new AnimationCurve(heightSizeCurve.keys);
        var simplificationIncrement = (detailLevel == 0) ? 1 : detailLevel * 2;
        
        var size = heightMap.GetLength(0);
        
        var topLeftX = (size - 1) / -2f;
        var topLeftZ = (size - 1) / 2f;
        
        var verticesPerLine = (size - 1) / simplificationIncrement + 1;
        var data = new MeshData(verticesPerLine);

        var vertexIndicesMap = new int[size, size];
        var meshVertexIndex = 0;

        for (var y = 0; y < size; y += simplificationIncrement)
        {
            for (var x = 0; x < size; x += simplificationIncrement)
            {
                vertexIndicesMap[x, y] = meshVertexIndex;
                meshVertexIndex++;
            }
        }

        for (var y = 0; y < size; y += simplificationIncrement)
        {
            for (var x = 0; x < size; x += simplificationIncrement)
            {
                var vertexIndex = vertexIndicesMap[x, y];

                var percent = new Vector2(x / (float)size, y / (float)size);
                var height = safeHeightCurve.Evaluate(heightMap[x, y]) * heightMultiplier;
                var vertexPos = new Vector3(topLeftX + percent.x * size, height, topLeftZ - percent.y * size);
                
                data.AddVertex(vertexPos, percent, vertexIndex);

                if (x < size - 1 && y < size - 1)
                {
                    data.AddTriangle(vertexIndex, vertexIndex + verticesPerLine + 1, vertexIndex + verticesPerLine);
                    data.AddTriangle(vertexIndex + verticesPerLine + 1, vertexIndex, vertexIndex + 1);
                }
                
                
                vertexIndex++;
            }
        }
        
        data.FlatShading();

        return data;
    }
}

public class MeshData
{
    private Vector3[] _vertices;
    private int[] _triangles;
    private Vector2[] _uvs;
    private Vector3[] _bakedNormals;

    private int _triangleIndex;
    private int _borderTriangleIndex;

    public MeshData(int verticesPerLine)
    {
        _vertices = new Vector3[(verticesPerLine * verticesPerLine)];
        _triangles = new int[(verticesPerLine - 1) * (verticesPerLine - 1) * 6];
        _uvs = new Vector2[verticesPerLine * verticesPerLine];

    }

    public void AddTriangle(int a, int b, int c)
    {
        _triangles[_triangleIndex] = a;
        _triangles[_triangleIndex + 1] = b;
        _triangles[_triangleIndex + 2] = c;

        _triangleIndex += 3;
        
    }

    public void AddVertex(Vector3 position, Vector2 uv, int index)
    {
        _vertices[index] = position;
        _uvs[index] = uv;
    }
    
    public void FlatShading()
    {
        var flatShadedVertices = new Vector3[_triangles.Length];
        var flatShadedUvs = new Vector2[_triangles.Length];

        for (var i = 0; i < _triangles.Length; i++)
        {
            flatShadedVertices[i] = _vertices[_triangles[i]];
            flatShadedUvs[i] = _uvs[_triangles[i]];
            _triangles[i] = i;
        }

        _vertices = flatShadedVertices;
        _uvs = flatShadedUvs;
    }

    public Mesh CreateMesh()
    {
        var mesh = new Mesh
        {
            vertices = _vertices,
            triangles = _triangles,
            uv = _uvs
        };
        
        mesh.RecalculateNormals();

        return mesh;
    }
}
