using UnityEngine;

public class MapDisplay : MonoBehaviour
{
    public Renderer TextureRenderer;
    public MeshFilter MeshFilter;
    public MeshRenderer MeshRenderer;

    public void DrawTexture(Texture2D texture)
    {
        TextureRenderer.sharedMaterial.mainTexture = texture;
        TextureRenderer.transform.localScale = new Vector3(texture.width, 1, texture.height);
    }

    public void DrawMesh(MeshData data, Texture2D texture)
    {
        MeshFilter.sharedMesh = data.CreateMesh();
        MeshRenderer.sharedMaterial.mainTexture = texture;
    }
}
