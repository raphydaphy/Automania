using System;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;

public class TerrainLoader : MonoBehaviour
{
    
    private const float MinMovementForUpdate = 25f;
    private const float SqrMinMovementForUpdate = MinMovementForUpdate * MinMovementForUpdate;
    
    public LODInfo[] DetailLevels;
    public static float MaxView = 450;
    
    public Transform Viewer;
    public Material MeshMaterial;
    
    public static Vector2 ViewPosition;
    private Vector2 OldViewPosition;
    
    public static MapGenerator Generator;
    
    private int _chunkSize;
    private int _chunksVisible;
    
    private Dictionary<Vector2, TerrainChunk> _chunkDictionary = new Dictionary<Vector2, TerrainChunk>();
    private static List<TerrainChunk> _lastVisibleChunks = new List<TerrainChunk>();
    private void Start()
    {
        Generator = FindObjectOfType<MapGenerator>();

        MaxView = DetailLevels[DetailLevels.Length - 1].VisibleDistanceThreshold;
        _chunkSize = MapGenerator.ChunkSize - 1;
        _chunksVisible = Mathf.RoundToInt(MaxView / _chunkSize);
        
        UpdateVisibleChunks();
    }

    private void Update()
    {
        ViewPosition = new Vector2(Viewer.position.x, Viewer.position.z) / Generator.TerrainData.UniformScale;

        if ((OldViewPosition - ViewPosition).SqrMagnitude() > SqrMinMovementForUpdate)
        {
            OldViewPosition = ViewPosition;
            UpdateVisibleChunks();
        }
    }

    public TerrainChunk GetChunk(float worldX, float worldY)
    {
        worldX /= 188;
        worldY /= 188;

        var pos = new Vector2(worldX, worldY);
        
        print("Looking for chunk at " + pos.x + ", " + pos.y);

        if (_chunkDictionary.ContainsKey(pos))
        {
            var chunk = _chunkDictionary[pos];
            return chunk;
        }

        return null;
    }

    private void UpdateVisibleChunks()
    {
        for (var i = 0; i < _lastVisibleChunks.Count; i++)
        {
            _lastVisibleChunks[i].SetVisible(false);
        }
        
        _lastVisibleChunks.Clear();
        
        var chunkCoordX = Mathf.RoundToInt(ViewPosition.x / _chunkSize);
        var chunkCoordY = Mathf.RoundToInt(ViewPosition.y / _chunkSize);

        for (var yOffset = -_chunksVisible; yOffset <= _chunksVisible; yOffset++)
        {
            for (var xOffset = -_chunksVisible; xOffset <= _chunksVisible; xOffset++)
            {
                var viewedChunkCoord = new Vector2(chunkCoordX + xOffset, chunkCoordY + yOffset);

                if (_chunkDictionary.ContainsKey(viewedChunkCoord))
                {
                    _chunkDictionary[viewedChunkCoord].UpdateChunk();
                }
                else
                {
                    _chunkDictionary.Add(viewedChunkCoord, new TerrainChunk(viewedChunkCoord, _chunkSize, DetailLevels, transform, MeshMaterial));
                }
            }
        }
    }

    public class TerrainChunk
    {
        private GameObject _meshObject;
        private Vector2 _position;
        private Bounds _bounds;

        private MeshRenderer _renderer;
        private MeshFilter _filter;
        private MeshCollider _collider;

        private LODInfo[] _detailLevels;
        private LODMesh[] _detailMeshes;
        private LODMesh _collisionLODMesh;

        private MapData _mapData;
        private bool _mapDataRecieved;
        private int _previousLODIndex = -1;
        
        public TerrainChunk(Vector2 coord, int size, LODInfo[] detailLevels, Transform parent, Material material)
        {
            _detailLevels = detailLevels;
            _position = coord * size;
            _bounds = new Bounds(_position, Vector2.one * size);
            
            var pos3 = new Vector3(_position.x, 0, _position.y);
            
            _meshObject = new GameObject("Terrain Chunk @ " + coord.x + ", " + coord.y);
            _renderer = _meshObject.AddComponent<MeshRenderer>();
            _filter = _meshObject.AddComponent<MeshFilter>();
            _collider = _meshObject.AddComponent<MeshCollider>();
            _renderer.material = material;
            
            _meshObject.transform.position = pos3 * Generator.TerrainData.UniformScale;
            _meshObject.transform.parent = parent;
            _meshObject.transform.localScale = Vector3.one * Generator.TerrainData.UniformScale;
            
            SetVisible(false);
            
            _detailMeshes = new LODMesh[_detailLevels.Length];
            for (var i = 0; i < _detailLevels.Length; i++)
            {
                _detailMeshes[i] = new LODMesh(_detailLevels[i].LOD, UpdateChunk);
                if (_detailLevels[i].useForCollider)
                {
                    _collisionLODMesh = _detailMeshes[i];
                }
            }
            
            Generator.RequestMapData(_position, OnMapDataRecieved);
        }

        private void OnMapDataRecieved(MapData data)
        {
            _mapData = data;
            _mapDataRecieved = true;

            var texture =
                TextureGenerator.TextureFromColorMap(data.ColorMap, MapGenerator.ChunkSize, MapGenerator.ChunkSize);
            _renderer.material.mainTexture = texture;
            
            UpdateChunk();
        }

        public List<Vector3> GetVertices()
        {
            return _filter.mesh.vertices.ToList();
        }

        public List<Vector3> GetNormals()
        {
            return _filter.mesh.normals.ToList();
        }

        public void SetVertices(List<Vector3> vertices)
        {
            _filter.mesh.SetVertices(vertices);
        }

        public void UpdateChunk()
        {
            if (_mapDataRecieved)
            {
                var viewDistFromEdge = Mathf.Sqrt(_bounds.SqrDistance(ViewPosition));
                var visible = viewDistFromEdge <= MaxView;

                if (visible)
                {
                    var lodIndex = 0;

                    for (var i = 0; i < _detailLevels.Length - 1; i++)
                    {
                        if (viewDistFromEdge > _detailLevels[i].VisibleDistanceThreshold)
                        {
                            lodIndex = i + 1;
                        }
                        else
                        {
                            break;
                        }
                    }

                    if (lodIndex != _previousLODIndex)
                    {
                        var lodMesh = _detailMeshes[lodIndex];
                        if (lodMesh.HasRecieved)
                        {
                            _previousLODIndex = lodIndex;
                            _filter.mesh = lodMesh.Mesh;
                        }
                        else if (!lodMesh.HasRequested)
                        {
                            lodMesh.RequestMesh(_mapData);
                        }
                    }

                    if (lodIndex == 0)
                    {
                        if (_collisionLODMesh.HasRecieved)
                        {
                            _collider.sharedMesh = _collisionLODMesh.Mesh;
                        }
                        else if (!_collisionLODMesh.HasRequested)
                        {
                            _collisionLODMesh.RequestMesh(_mapData);
                        }
                    }
                    
                    _lastVisibleChunks.Add(this);
                }
                SetVisible(visible);
            }
        }

        public void SetVisible(bool visible)
        {
            _meshObject.SetActive(visible);
        }

        public bool IsVisible()
        {
            return _meshObject.activeSelf;
        }
    }

    private class LODMesh
    {
        public Mesh Mesh;
        public bool HasRequested;
        public bool HasRecieved;
        private readonly int _lod;
        private readonly Action _updateCallback;
        
        public LODMesh(int lod, Action updateCallback)
        {
            _lod = lod;
            _updateCallback = updateCallback;
        }

        private void OnMeshDataRecieved(MeshData data)
        {
            Mesh = data.CreateMesh();
            HasRecieved = true;

            _updateCallback();
        }

        public void RequestMesh(MapData data)
        {
            HasRequested = true;
            Generator.RequestMeshData(data, _lod, OnMeshDataRecieved);
        }
    }

    [System.Serializable]
    public struct LODInfo
    {
        public int LOD;
        public float VisibleDistanceThreshold;
        public bool useForCollider;
    }
}
