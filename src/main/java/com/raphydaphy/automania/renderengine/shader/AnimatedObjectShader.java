package main.java.com.raphydaphy.automania.renderengine.shader;

import org.lwjgl.util.vector.Matrix4f;


public class AnimatedObjectShader extends WorldShader
{
	private static final String name = "src/main/resources/shaders/animated_object";

	private static final int MAX_JOINTS = 50;

	private int[] jointTransformsLocations;

	public AnimatedObjectShader()
	{
		super(name);
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttributes();
		super.bindAttribute(2, "tex_coords");
		super.bindAttribute(3, "joint_indices");
		super.bindAttribute(4, "joint_weights");
	}

	@Override
	protected void getAllUniformLocations()
	{
		super.getAllUniformLocations();
		jointTransformsLocations = new int[MAX_JOINTS];
		for (int jointTransformsLocation = 0; jointTransformsLocation < MAX_JOINTS; jointTransformsLocation++)
		{
			jointTransformsLocations[jointTransformsLocation] = super.getUniformLocation("joint_transforms[" + jointTransformsLocation + "]");
		}

	}

	public void loadJointTransforms(Matrix4f[] jointTransforms)
	{
		for (int jointTransform = 0; jointTransform < jointTransforms.length; jointTransform++)
		{
			Matrix4f matrix = jointTransforms[jointTransform];

			if (matrix == null)
			{
				continue;
			}

			super.uniformMatrix4(jointTransformsLocations[jointTransform], matrix);
		}
	}
}
