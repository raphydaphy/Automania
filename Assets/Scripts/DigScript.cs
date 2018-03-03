using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using NUnit.Framework.Constraints;
using UnityEngine;
using Debug = UnityEngine.Debug;

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
                if (Vector3.Distance(gameObject.transform.position, hit.point) < 25)
                {

                    var pos = hit.transform.position;

                    var chunk = World.GetChunk(pos.x, pos.z);

                    if (chunk != null && chunk.IsVisible())
                    {
                        var chunkHitX =
                            Mathf.RoundToInt(Mathf.Abs((Mathf.RoundToInt(hit.point.x) / ((MapGenerator.ChunkSize - 2f) * 2f) + 0.5f) * ((MapGenerator.ChunkSize - 2f) * 2f)));
                        var chunkHitY =
                            Mathf.RoundToInt(Mathf.Abs((Mathf.RoundToInt(hit.point.z) / ((MapGenerator.ChunkSize - 2f) * 2f) + 0.5f) * ((MapGenerator.ChunkSize - 2f) * 2f)));

                        chunkHitX = (chunkHitX / 2) % MapGenerator.ChunkSize;
                        chunkHitY = (chunkHitY / 2) % MapGenerator.ChunkSize;
                        
                        for (var i = -2; i < 2; i++)
                        {
                            for (var j = -2; j < 2; j++)
                            {
                                var x = chunkHitX + i;
                                var z = Mathf.Abs(chunkHitY - MapGenerator.ChunkSize) + j;

                                if (x > 0 && x < MapGenerator.ChunkSize && z > 0 && z < MapGenerator.ChunkSize)
                                {
                                    var curHeight = chunk._mapData.HeightMap[x, z];
                                    
                                    for (var region = 0; region < MapGenerator.instance.TerrainData.Regions.Length; region++)
                                    {
                                        if (curHeight >= MapGenerator.instance.TerrainData.Regions[region].Height)
                                        {
                                            chunk._mapData.ColorMap[z * MapGenerator.ChunkSize + x] = MapGenerator.instance.TerrainData.Regions[region].Color;
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }

                                    chunk._mapData.HeightMap[x, z] = Math.Min(curHeight,
                                        curHeight - 0.03f + (Mathf.Abs(i) / 100f) + (Mathf.Abs(j) / 100f));
                                }
                            }
                        }
                        
                        
                        
                        var mesh =
                            MeshGenerator.GenerateTerrainMesh(chunk._mapData.HeightMap,
                                MapGenerator.instance.TerrainData.MeshHeightMultiplier,
                                MapGenerator.instance.TerrainData.MeshHeightCurve, 0).CreateMesh();
                        
                        var texture =
                            TextureGenerator.TextureFromColorMap(chunk._mapData.ColorMap, MapGenerator.ChunkSize, MapGenerator.ChunkSize);
                        
                        hit.transform.gameObject.GetComponent<MeshRenderer>().sharedMaterial.mainTexture = texture;
                        hit.transform.gameObject.GetComponent<MeshFilter>().sharedMesh = mesh;
                        hit.transform.gameObject.GetComponent<MeshCollider>().sharedMesh = mesh;
                    }
                }
            }
        }
    }
}
