using System.Deployment.Internal;
using UnityEngine;

public static class TextureGenerator {

	public static Texture2D TextureFromColourMap(Color[] colourMap, int width, int height) {
		Texture2D texture = new Texture2D(width, height)
		{
			filterMode = FilterMode.Point,
			wrapMode = TextureWrapMode.Clamp
		};
		texture.SetPixels (colourMap);
		texture.Apply ();
		return texture;
	}

	// missingAxis: 0 = x, 1 = y, 2 = z
	public static Texture2D TextureFrom3DHeightMap(float[,,] heightMap, int missingAxis)
	{
		var width = 0;
		var height = 0;

		switch (missingAxis)
		{
			case 0:
				width = heightMap.GetLength(1);
				height = heightMap.GetLength(2);
				break;
			case 1:
				width = heightMap.GetLength(0);
				height = heightMap.GetLength(2);
				break;
			case 2:
				width = heightMap.GetLength(0);
				height = heightMap.GetLength(1);
				break;
		}

		var heightMap2D = new float[width, height];

		for (var x = 0; x < width; x++)
		{
			for (var y = 0; y < height; y++)
			{
				var index = Get2D(heightMap, x, y, missingAxis);
				heightMap2D[x, y] = heightMap[index.x, index.y, index.z];
			}
		}

		return TextureFromHeightMap(heightMap2D);
	}

	private static Vector3Int Get2D(float[,,] array3D, int i, int j, int missingAxis)
	{
		switch (missingAxis)
		{
			case 0:
				return new Vector3Int(0, i, j);
			case 1:
				return new Vector3Int(i, 0, j);
			case 2:
				return new Vector3Int(i, j, 0);
		}
		return new Vector3Int(i, j, 0);
	}

	public static Texture2D TextureFromHeightMap(float[,] heightMap) {
		var width = heightMap.GetLength (0);
		var height = heightMap.GetLength (1);

		var colourMap = new Color[width * height];
		for (var y = 0; y < height; y++) {
			for (var x = 0; x < width; x++) {
				colourMap [y * width + x] = Color.Lerp (Color.black, Color.white, heightMap [x, y]);
			}
		}

		return TextureFromColourMap (colourMap, width, height);
	}

}