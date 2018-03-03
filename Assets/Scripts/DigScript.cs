using System;
using System.Collections;
using System.Collections.Generic;
using NUnit.Framework.Constraints;
using UnityEngine;

public class DigScript : MonoBehaviour
{
    public Camera Camera;
    public TerrainLoader World;

    private void Update()
    {
        if (Input.GetMouseButtonDown(0)) 
        {
            var ray = Camera.ScreenPointToRay(Input.mousePosition);
            RaycastHit hit;
            if (Physics.Raycast(ray, out hit))
            {
                var pos = hit.transform.position;

                var chunk = World.GetChunk(pos.x, pos.z);

                if (chunk != null && chunk.IsVisible())
                {

                    var vertices = chunk.GetVertices();
                    var normals = chunk.GetNormals();

                    var chunkHitX = Mathf.RoundToInt(Mathf.Abs((Mathf.RoundToInt(hit.point.x) / 188f + 0.5f) * 188f));
                    var chunkHitY =Mathf.RoundToInt( Mathf.Abs(Mathf.Abs((Mathf.RoundToInt(hit.point.z) / 188f + 0.5f) * 180f) - 188));
                    
                    print("Hit chunk @ " + pos.x + ", " + pos.y + " with inner coord " + chunkHitX + ", " + chunkHitY);
                    print(vertices.Count);

                    for (var y = -2; y < 2; y++)
                    {
                        for (var x = -2; x < 2; x++)
                        {
                            for (var i = 0; i < 3; i++)
                            {
                                var index = ((chunkHitY + y) * (MapGenerator.ChunkSize - 2) + (chunkHitX + 2)) * (i + 1);
                                vertices[index] += Vector3.down * 5 * Time.deltaTime;
                            }
                        }
                    }
                    
                    
                    
                    
                    
                    chunk.SetVertices(vertices);
                }
            }
        }
    }
}
