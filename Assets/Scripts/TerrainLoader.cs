using System.Collections.Generic;
using UnityEngine;

public class TerrainLoader : MonoBehaviour
{
    public const float MaxView = 300;
    public Transform Viewer;

    public static Vector2 ViewPosition;

    private int _chunkSize;
    private int _chunksVisible;
    
    private Dictionary<Vector2, TerrainChunk> _chunkDictionary = new Dictionary<Vector2, TerrainChunk>();
    private List<TerrainChunk> _lastVisibleChunks = new List<TerrainChunk>();

    private void Start()
    {
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
                    _chunkDictionary.Add(viewedChunkCoord, new TerrainChunk(viewedChunkCoord, _chunkSize, transform));
                }
            }
        }
    }

    public class TerrainChunk
    {
        private GameObject meshObject;
        private Vector2 Position;
        private Bounds Bounds;
        
        public TerrainChunk(Vector2 coord, int size, Transform parent)
        {
            Position = coord * size;
            Bounds = new Bounds(Position, Vector2.one * size);
            
            var pos3 = new Vector3(Position.x, 0, Position.y);
            
            meshObject = GameObject.CreatePrimitive(PrimitiveType.Plane);
            meshObject.transform.position = pos3;
            meshObject.transform.localScale = Vector3.one * size / 10f;
            meshObject.transform.parent = parent;
            
            SetVisible(false);
        }

        public void UpdateChunk()
        {
            var viewDistFromEdge = Mathf.Sqrt(Bounds.SqrDistance(ViewPosition));
            SetVisible(viewDistFromEdge <= MaxView);
        }

        public void SetVisible(bool visible)
        {
            meshObject.SetActive(visible);
        }

        public bool IsVisible()
        {
            return meshObject.activeSelf;
        }
    }
}
