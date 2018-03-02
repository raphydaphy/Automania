using UnityEngine;

[CreateAssetMenu()]
public class NoiseData : UpdatableData 
{
    public float NoiseScale = 0.3f;

    public int Octaves = 4;
    [Range(0, 1)]
    public float Persistance = 0.5f;
    public float Lacunarity = 2;

    public int Seed = 10;
    public Vector2 Offset = new Vector2(0, 0);

    protected override void OnValidate()
    {
        if (Lacunarity < 1)
        {
            Lacunarity = 1;
        }

        if (Octaves <= 0)
        {
            Octaves = 1;
        }
        
        base.OnValidate();
    }
}
