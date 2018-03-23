package main.java.com.raphydaphy.automania.renderengine.animation;

import main.java.com.raphydaphy.automania.renderengine.load.Texture;
import main.java.com.raphydaphy.automania.util.VertexArray;
import org.lwjgl.util.vector.Matrix4f;

public class AnimatedModel
{
	public final Joint root;
	public final int joints;

	private final VertexArray vao;
	private final Texture texture;

	private final Animator animator;

	public AnimatedModel(VertexArray vao, Texture texture, Joint root, int joints, Animator animator)
	{
		this.root = root;
		this.joints = joints;

		this.vao = vao;
		this.texture = texture;

		this.animator = animator;

		root.calculateInverseBindTransform(new Matrix4f());
	}

	public void delete()
	{
		vao.delete();
	}

	public void doAnimation(Animation animation)
	{
		animator.doAnimation(animation);
	}

	public void update()
	{
		animator.update();
	}

	public Matrix4f[] getJointTransforms()
	{
		Matrix4f[] jointMatrices = new Matrix4f[joints];
		addJoints(root, jointMatrices);
		return jointMatrices;
	}

	private void addJoints(Joint headJoint, Matrix4f[] jointMatrices)
	{
		jointMatrices[headJoint.id] = headJoint.getAnimatedTransform();

		for (Joint joint : headJoint.children)
		{
			addJoints(joint, jointMatrices);
		}
	}
}
