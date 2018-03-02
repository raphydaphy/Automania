using System.Collections.Generic;
using System.Runtime.InteropServices;
using UnityEngine;

public class TerrainLoader : MonoBehaviour
{
    public LODInfo[] DetailLevels;
    public static float MaxView = 450;
    
    public Transform Viewer;
    public Material MeshMaterial;
    
    public static Vector2 ViewPosition;
    public static MapGenerator Generator;
    
    private int _chunkSize;
    private int _chunksVisible;
    
    private Dictionary<Vector2, TerrainChunk> _chunkDictionary = new Dictionary<Vector2, TerrainChunk>();
    private List<TerrainChunk> _lastVisibleChunks = new List<TerrainChunk>();
    private void Start()
    {
        Generator = FindObjectOfType<MapGenerator>();

        MaxView = DetailLevels[DetailLevels.Length - 1].VisibleDistanceThreshold;
        _chunkSize = MapGenerator.ChunkSize - 1;
        _chunksVisible = Mathf.RoundToInt(MaxView / _chunkSize);
    }

    private void Update()
    {
        ViewPosition = new Vector2(Viewer.position.x, Viewer.position.z);
        UpdateVisibleChunks();
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
                    if (_chunkDictionary[viewedChunkCoord].IsVisible())
                    {
                        _lastVisibleChunks.Add(_chunkDictionary[viewedChunkCoord]);
                    }
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

        private LODInfo[] _detailLevels;
        private LODMesh[] _detailMeshes;

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
            _renderer.material = material;
            
            _meshObject.transform.position = pos3;
            _meshObject.transform.parent = parent;
            
            SetVisible(false);
            
            _detailMeshes = new LODMesh[_detailLevels.Length];
            for (var i = 0; i < _detailLevels.Length; i++)
            {
                _detailMeshes[i] = new LODMesh(_detailLevels[i].LOD);
            }
            
            Generator.RequestMapData(OnMapDataRecieved);
        }

        private void OnMapDataRecieved(MapData data)
        {
            _mapData = data;
            _mapDataRecieved = true;
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
        private int _lod;

        public LODMesh(int lod)
        {
            _lod = lod;
        }

        private void OnMeshDataRecieved(MeshData data)
        {
            Mesh = data.CreateMesh();
            HasRecieved = true;
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
    }
}
