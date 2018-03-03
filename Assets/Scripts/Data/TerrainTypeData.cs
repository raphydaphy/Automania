using UnityEngine;

[CreateAssetMenu()]
public class TerrainTypeData : UpdatableData 
{
	public float UniformScale = 2f;
	public bool UseFalloff;
    
	public float MeshHeightMultiplier;
	public AnimationCurve MeshHeightCurve;
	
	public TerrainType[] Regions;
}
