package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformMatrices;

public class AnimatedObjectShader extends WorldShader
{
	private static final String name = "src/main/resources/shaders/animated_object";

	private static final int MAX_JOINTS = 50;

	public UniformMatrices jointTransforms = new UniformMatrices("joint_transforms", MAX_JOINTS);

	public AnimatedObjectShader()
	{
		super(name, "tex_coords", "joint_indices", "joint_weights");
		storeAllUniformLocations(jointTransforms);
	}
}
