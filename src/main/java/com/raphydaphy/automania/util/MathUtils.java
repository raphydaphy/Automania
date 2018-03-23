package main.java.com.raphydaphy.automania.util;

import main.java.com.raphydaphy.automania.render.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
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

	public static float interpolate(float a, float b, float alpha)
	{
		return a + alpha * (b - a);
	}

	public static Vector3f interpolate(Vector3f a, Vector3f b, float alpha)
	{
		return new Vector3f(interpolate(a.x, b.x, alpha), interpolate(a.y, b.y, alpha), interpolate(a.z, b.z, alpha));
	}

	public static Quaternion interpolate(Quaternion a, Quaternion b, float alpha)
	{
		Quaternion result = new Quaternion(0, 0, 0, 1);
		float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
		float blendI = 1f - alpha;
		if (dot < 0) {
			result.w = blendI * a.w + alpha * -b.w;
			result.x = blendI * a.x + alpha * -b.x;
			result.y = blendI * a.y + alpha * -b.y;
			result.z = blendI * a.z + alpha * -b.z;
		} else {
			result.w = blendI * a.w + alpha * b.w;
			result.x = blendI * a.x + alpha * b.x;
			result.y = blendI * a.y + alpha * b.y;
			result.z = blendI * a.z + alpha * b.z;
		}
		normalizeQuat(result);
		return result;
	}

	public static Matrix4f quatToMatrix(Quaternion quat) {
		Matrix4f matrix = new Matrix4f();

		final float xy = quat.x * quat.y;
		final float xz = quat.x * quat.z;
		final float xw = quat.x * quat.w;
		final float yz = quat.y * quat.z;
		final float yw = quat.y * quat.w;
		final float zw = quat.z * quat.w;
		final float xSquared = quat.x * quat.x;
		final float ySquared = quat.y * quat.y;
		final float zSquared = quat.z * quat.z;

		matrix.m00 = 1 - 2 * (ySquared + zSquared);
		matrix.m01 = 2 * (xy - zw);
		matrix.m02 = 2 * (xz + yw);
		matrix.m03 = 0;
		matrix.m10 = 2 * (xy + zw);
		matrix.m11 = 1 - 2 * (xSquared + zSquared);
		matrix.m12 = 2 * (yz - xw);
		matrix.m13 = 0;
		matrix.m20 = 2 * (xz - yw);
		matrix.m21 = 2 * (yz + xw);
		matrix.m22 = 1 - 2 * (xSquared + ySquared);
		matrix.m23 = 0;
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		matrix.m33 = 1;

		return matrix;
	}

	public static void normalizeQuat(Quaternion quat) {
		float mag = (float) Math.sqrt(quat.w * quat.w + quat.x * quat.x + quat.y * quat.y + quat.z * quat.z);
		quat.w /= mag;
		quat.x /= mag;
		quat.y /= mag;
		quat.z /= mag;
	}
}
