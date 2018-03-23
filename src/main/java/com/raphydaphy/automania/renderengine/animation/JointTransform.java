package main.java.com.raphydaphy.automania.renderengine.animation;

import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class JointTransform
{
	private final Vector3f position;
	private final Quaternion rotation;

	public JointTransform(Vector3f position, Quaternion rotation)
	{
		this.position = position;
		this.rotation = rotation;
	}

	protected Matrix4f getLocalTransform()
	{
		Matrix4f matrix = new Matrix4f();
		matrix.translate(position);
		Matrix4f.mul(matrix, MathUtils.quatToMatrix(rotation), matrix);
		return matrix;
	}



	protected static JointTransform interpolate(JointTransform start, JointTransform end, float progress)
	{
		Vector3f pos = MathUtils.interpolate(start.position, end.position, progress);
		Quaternion rot = MathUtils.interpolate(start.rotation, end.rotation, progress);
		return new JointTransform(pos, rot);
	}
}
