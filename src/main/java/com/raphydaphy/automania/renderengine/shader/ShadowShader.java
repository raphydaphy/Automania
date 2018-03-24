package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformMatrix;

public class ShadowShader extends ShaderProgram
{

	private static final String name = "src/main/resources/shaders/shadow";

	public UniformMatrix mvpMatrix = new UniformMatrix("mvpMatrix");

	public ShadowShader()
	{
		super(name, "in_position", EMPTY_ATTRIBUTE, "in_textures");
		storeAllUniformLocations(mvpMatrix);
	}

}
