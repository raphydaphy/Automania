using UnityEngine;

public static class MeshGenerator 
{
    public static MeshData GenerateTerrainMesh(float[,] heightMap, float heightMultiplier, AnimationCurve heightCurve, int detailLevel)
    {
        var width = heightMap.GetLength(0);
        var height = heightMap.GetLength(1);

        var topLeftX = (width - 1) / -2f;
        var topLeftZ = (height - 1) / 2f;

        var simplificationIncrement = (detailLevel == 0) ? 1 : detailLevel * 2;
        var verticesPerLine = (width - 1) / simplificationIncrement + 1;

        var data = new MeshData(verticesPerLine, verticesPerLine);
        var vertexIndex = 0;
        
        for (var y = 0; y < height; y += simplificationIncrement)
        {
            for (var x = 0; x < width; x += simplificationIncrement)
            {
                data.Vertices[vertexIndex] = new Vector3(topLeftX + x, heightCurve.Evaluate(heightMap[x, y]) * heightMultiplier, topLeftZ - y);
                data.UVs[vertexIndex] = new Vector2(x / (float)width, y / (float)height);
                    
                if (x < width - 1 && y < height - 1)
                {
                    data.AddTriangle(vertexIndex, vertexIndex + verticesPerLine + 1, vertexIndex + verticesPerLine);
                    data.AddTriangle(vertexIndex + verticesPerLine + 1, vertexIndex, vertexIndex + 1);
                }
                
                vertexIndex++;
            }
        }

        return data;
    }
}

public class MeshData
{
    public Vector3[] Vertices;
    public int[] Triangles;
    public Vector2[] UVs;

    private int _triangleIndex;

    public MeshData(int meshWidth, int meshHeight)
    {
        Vertices = new Vector3[(meshWidth * meshHeight)];
        Triangles = new int[(meshWidth - 1) * (meshHeight - 1) * 6];
        UVs = new Vector2[meshWidth * meshHeight];
    }

    public void AddTriangle(int a, int b, int c)
    {
        Triangles[_triangleIndex] = a;
        Triangles[_triangleIndex + 1] = b;
        Triangles[_triangleIndex + 2] = c;

        _triangleIndex += 3;
    }

    public Mesh CreateMesh()
    {
        var mesh = new Mesh();

        mesh.vertices = Vertices;
        mesh.triangles = Triangles;
        mesh.uv = UVs;
        
        mesh.RecalculateBounds();

        return mesh;
    }
}
