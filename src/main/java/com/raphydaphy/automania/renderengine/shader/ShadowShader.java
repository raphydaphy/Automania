package main.java.com.raphydaphy.automania.renderengine.shader;

import org.lwjgl.util.vector.Matrix4f;

public class ShadowShader extends ShaderProgram
{

	private static final String name = "src/main/resources/shaders/shadow";

	private int location_mvpMatrix;

	public ShadowShader()
	{
		super(name);
	}

	@Override
	protected void getAllUniformLocations()
	{
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");

	}

	public void loadMvpMatrix(Matrix4f mvpMatrix)
	{
		super.uniformMatrix4(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "in_position");
		super.bindAttribute(2, "in_textures");
	}

}
