using UnityEngine;

public static class MeshGenerator 
{
    public static MeshData GenerateTerrainMesh(float[,] heightMap, float heightMultiplier, AnimationCurve borderedSizeCurve, int detailLevel)
    {
        var safeHeightCurve = new AnimationCurve(borderedSizeCurve.keys);
        var simplificationIncrement = (detailLevel == 0) ? 1 : detailLevel * 2;
        
        var borderedSize = heightMap.GetLength(0);
        var meshSize = borderedSize - 2 * simplificationIncrement;
        var meshSizeUnsimplified = borderedSize - 2;
        
        var topLeftX = (meshSizeUnsimplified - 1) / -2f;
        var topLeftZ = (meshSizeUnsimplified - 1) / 2f;
        
        var verticesPerLine = (meshSize - 1) / simplificationIncrement + 1;
        var data = new MeshData(verticesPerLine);

        var vertexIndicesMap = new int[borderedSize, borderedSize];
        var meshVertexIndex = 0;
        var borderVertexIndex = -1;

        for (var y = 0; y < borderedSize; y += simplificationIncrement)
        {
            for (var x = 0; x < borderedSize; x += simplificationIncrement)
            {
                var isBorderVertex = y == 0 || y == borderedSize - 1 || x == 0 || x == borderedSize - 1;

                if (isBorderVertex)
                {
                    vertexIndicesMap[x, y] = borderVertexIndex;
                    borderVertexIndex--;
                }
                else
                {
                    vertexIndicesMap[x, y] = meshVertexIndex;
                    meshVertexIndex++;
                }
            }
        }

        for (var y = 0; y < borderedSize; y += simplificationIncrement)
        {
            for (var x = 0; x < borderedSize; x += simplificationIncrement)
            {
                var vertexIndex = vertexIndicesMap[x, y];

                var percent = new Vector2((x - simplificationIncrement) / (float) meshSize,
                    (y - simplificationIncrement) / (float) meshSize);
                var height = safeHeightCurve.Evaluate(heightMap[x, y]) * heightMultiplier;
                var vertexPos = new Vector3(topLeftX + percent.x * meshSizeUnsimplified, height, topLeftZ - percent.y * meshSizeUnsimplified);
                
                data.AddVertex(vertexPos, percent, vertexIndex);
                    
                if (x < borderedSize - 1 && y < borderedSize - 1)
                {
                    var a = vertexIndicesMap[x, y];
                    var b = vertexIndicesMap[x + simplificationIncrement, y];
                    var c = vertexIndicesMap[x, y + simplificationIncrement];
                    var d = vertexIndicesMap[x + simplificationIncrement, y + simplificationIncrement];

                    data.AddTriangle(a, d, c);
                    data.AddTriangle(d,a,b);
                }
                
                vertexIndex++;
            }
        }
        
        data.BakeNormals();

        return data;
    }
}

public class MeshData
{
    private Vector3[] _vertices;
    private int[] _triangles;
    private Vector2[] _uvs;
    private Vector3[] _bakedNormals;

    private Vector3[] _borderVertices;
    private int[] _borderTriangles;

    private int _triangleIndex;
    private int _borderTriangleIndex;

    public MeshData(int verticesPerLine)
    {
        _vertices = new Vector3[(verticesPerLine * verticesPerLine)];
        _triangles = new int[(verticesPerLine - 1) * (verticesPerLine - 1) * 6];
        _uvs = new Vector2[verticesPerLine * verticesPerLine];

        _borderVertices = new Vector3[verticesPerLine * 4 + 4];
        _borderTriangles = new int[24 * verticesPerLine];
    }

    public void AddTriangle(int a, int b, int c)
    {
        if (a < 0 || b < 0 || c < 0)
        {
            _borderTriangles[_borderTriangleIndex] = a;
            _borderTriangles[_borderTriangleIndex + 1] = b;
            _borderTriangles[_borderTriangleIndex + 2] = c;

            _borderTriangleIndex += 3;
        }
        else
        {
            _triangles[_triangleIndex] = a;
            _triangles[_triangleIndex + 1] = b;
            _triangles[_triangleIndex + 2] = c;

            _triangleIndex += 3;
        }
        
    }

    public void AddVertex(Vector3 position, Vector2 uv, int index)
    {
        if (index < 0)
        {
            _borderVertices[-index - 1] = position;
        }
        else
        {
            _vertices[index] = position;
            _uvs[index] = uv;
        }
    }

    private Vector3[] CalculateNormals()
    {
        var newNormals = new Vector3[_vertices.Length];
        var triangleCount = _triangles.Length / 3;
        var borderTriangleCount = _borderTriangles.Length / 3;
        
        for (var i = 0; i < triangleCount; i++)
        {
            var normalTriangleIndex = i * 3;
            var vertexIndexA = _triangles[normalTriangleIndex];
            var vertexIndexB = _triangles[normalTriangleIndex + 1];
            var vertexIndexC = _triangles[normalTriangleIndex + 2];

            var triangleNormal = GetNormalFromIndices(vertexIndexA, vertexIndexB, vertexIndexC);

            newNormals[vertexIndexA] += triangleNormal;
            newNormals[vertexIndexB] += triangleNormal;
            newNormals[vertexIndexC] += triangleNormal;
        }

        for (var i = 0; i < borderTriangleCount; i++)
        {
            var normalTriangleIndex = i * 3;
            var vertexIndexA = _borderTriangles[normalTriangleIndex];
            var vertexIndexB = _borderTriangles[normalTriangleIndex + 1];
            var vertexIndexC = _borderTriangles[normalTriangleIndex + 2];

            var triangleNormal = GetNormalFromIndices(vertexIndexA, vertexIndexB, vertexIndexC);

            if (vertexIndexA >= 0)
            {
                newNormals[vertexIndexA] += triangleNormal;
            }

            if (vertexIndexB >= 0)
            {
                newNormals[vertexIndexB] += triangleNormal;
            }

            if (vertexIndexC >= 0)
            {
                newNormals[vertexIndexC] += triangleNormal;
            }
        }

        for (var i = 0; i < newNormals.Length; i++)
        {
            newNormals[i].Normalize();
        }

        return newNormals;
    }

    private Vector3 GetNormalFromIndices(int indexA, int indexB, int indexC)
    {
        var pointA = (indexA < 0) ? _borderVertices[-indexA - 1] : _vertices[indexA];
        var pointB = (indexB < 0) ? _borderVertices[-indexB - 1] : _vertices[indexB];
        var pointC = (indexC < 0) ? _borderVertices[-indexC - 1] : _vertices[indexC];

        var sideAB = pointB - pointA;
        var sideAC = pointC - pointA;

        return Vector3.Cross(sideAB, sideAC).normalized;
    }

    public void BakeNormals()
    {
        //FlatShading();
        _bakedNormals = CalculateNormals();
    }

    private void FlatShading()
    {
        var flatShadedVertices = new Vector3[_triangles.Length];
        var flatShadedUvs = new Vector2[_triangles.Length];

        for (var i = 0; i < _triangles.Length; i++)
        {
            flatShadedVertices[i] = _vertices[_triangles[i]];
            flatShadedUvs = _uvs[_triangles[i]]
            _triangles[i] = i;
        }

        _vertices = flatShadedVertices;
        _uvs = flatShadedUvs;
    }

    public Mesh CreateMesh()
    {
        var mesh = new Mesh();

        mesh.vertices = _vertices;
        mesh.triangles = _triangles;
        mesh.uv = _uvs;

        mesh.normals = _bakedNormals;

        return mesh;
    }
}
