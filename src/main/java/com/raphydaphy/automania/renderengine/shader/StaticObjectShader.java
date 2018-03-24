package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformInt;

public class StaticObjectShader extends WorldShader
{
	private static final String name = "src/main/resources/shaders/static_object";

	public UniformInt artificialLighting = new UniformInt("artificial_lighting");

	public StaticObjectShader()
	{
		super(name, "tex_coords");
		super.storeAllUniformLocations(artificialLighting);
	}
}
