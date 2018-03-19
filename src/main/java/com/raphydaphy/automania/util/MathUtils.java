package main.java.com.raphydaphy.automania.util;

import main.java.com.raphydaphy.automania.render.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MathUtils
{
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rotX, float rotY, float rotZ, float scale)
	{
		// Make a new matrix to store the transformation
		Matrix4f matrix = new Matrix4f();

		// Identity is essentially a empty matrix
		matrix.setIdentity();

		// Apply transformation based on the position we want to use to the matrix
		Matrix4f.translate(translation, matrix, matrix);

		// Rotate around each of the x, y and z axes
		Matrix4f.rotate((float) Math.toRadians(rotX), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotY), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotZ), new Vector3f(0, 0, 1), matrix, matrix);

		// Scale the matrix on all axes by the factor provided
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera)
	{
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), matrix, matrix);

		Matrix4f.translate(new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z), matrix, matrix);

		return matrix;
	}

	public static double clamp(double value, double min, double max)
	{
		if (value < min)
		{
			value = min;
		}
		if (value > max)
		{
			value = max;
		}

		return value;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, float x, float z) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);

		float l1 = ((p2.z - p3.z) * (x - p3.x) + (p3.x - p2.x) * (z - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (x - p3.x) + (p1.x - p3.x) * (z - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;

		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
}
