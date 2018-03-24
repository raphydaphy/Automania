package main.java.com.raphydaphy.automania.renderengine.animation;

import org.lwjgl.util.vector.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Joint
{
	public final int id;
	public final String name;
	public final List<Joint> children = new ArrayList<>();

	private Matrix4f animatedTransform = new Matrix4f();

	private final Matrix4f localBindTrasnform;
	private Matrix4f inverseBindTransform = new Matrix4f();

	public Joint(int id, String name, Matrix4f localBindTrasnform)
	{
		this.id = id;
		this.name = name;
		this.localBindTrasnform = localBindTrasnform;
	}

	public void addChild(Joint child)
	{
		children.add(child);
	}

	public Matrix4f getAnimatedTransform()
	{
		return animatedTransform;
	}

	public void setAnimatedTransform(Matrix4f animatedTransform)
	{
		this.animatedTransform = animatedTransform;
	}

	public Matrix4f getInverseBindTransform()
	{
		return inverseBindTransform;
	}

	public void calculateInverseBindTransform(Matrix4f parentBindTransform)
	{
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, localBindTrasnform, null);
		Matrix4f.invert(bindTransform, inverseBindTransform);

		for (Joint joint : children)
		{
			joint.calculateInverseBindTransform(bindTransform);
		}
	}
}
