package main.java.com.raphydaphy.automania.render;


import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformVector;
import main.java.com.raphydaphy.automania.util.VertexArray;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxGenerator
{

	private static final int VERTEX_COUNT = 8;
	private static final int[] INDICES = {0, 1, 3, 1, 2, 3, 1, 5, 2, 2, 5, 6, 4, 7, 5, 5, 7, 6, 0, 3, 4, 4, 3, 7, 7, 3, 6, 6, 3, 2, 4, 5, 0, 0, 5, 1};

	private final int segmentCount;
	private final float segmentTheta;
	private final float factor;

	private static float[] getVertexPositions(float size)
	{
		return new float[]{-size, size, size, size, size, size, size, -size, size, -size, -size, size, -size, size, -size, size, size, -size, size, -size, -size, -size, -size, -size};
	}

	public SkyboxGenerator(int segmentCount, float totalAngle)
	{
		this.segmentCount = segmentCount;
		this.segmentTheta = totalAngle / segmentCount;
		this.factor = 1f / totalAngle;
	}

	public VertexArray generateMeshVao()
	{
		Vector2f[] xzPositions = getVertexPositions();
		float[] vertices = new float[xzPositions.length * 2 * 3];
		int pointer = 0;
		for (Vector2f pos : xzPositions)
		{
			pointer = storeVertex(pos, 1, vertices, pointer);
			pointer = storeVertex(pos, -1, vertices, pointer);
		}
		return VertexArray.create().bind().createAttribute(0, vertices, xzPositions.length * 2);
	}

	public static VertexArray generateCube(float size)
	{
		return VertexArray.create().bind().storeIndices(INDICES).createAttribute(0, getVertexPositions(size), VERTEX_COUNT);
	}

	private int storeVertex(Vector2f pos, float height, float[] vertices, int pointer)
	{
		vertices[pointer++] = pos.x * factor;
		vertices[pointer++] = height;
		vertices[pointer++] = pos.y * factor;
		return pointer;
	}

	private Vector2f[] getVertexPositions()
	{
		float startPoint = segmentCount / 2f;
		float startingTheta = -startPoint * segmentTheta;
		Vector2f[] points = new Vector2f[segmentCount + 1];
		for (int i = 0; i < points.length; i++)
		{
			points[i] = pointOnCircle(startingTheta + segmentTheta * i);
		}
		return points;
	}

	private Vector2f pointOnCircle(float theta)
	{
		float x = (float) Math.sin(theta);
		float z = (float) -Math.cos(theta) + 1;
		return new Vector2f(x, z);
	}

}

