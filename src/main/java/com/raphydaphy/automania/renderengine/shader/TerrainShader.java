package main.java.com.raphydaphy.automania.renderengine.shader;

import org.lwjgl.util.vector.Matrix4f;

public class TerrainShader extends WorldShader
{
	private static final String name = "src/main/resources/shaders/terrain";

	private int toShadowMapSpaceLocation;
	private int shadowMapLocation;

	public TerrainShader()
	{
		super(name);
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttributes();
		super.bindAttribute(2, "color");
	}

	@Override
	protected void getAllUniformLocations()
	{
		super.getAllUniformLocations();
		toShadowMapSpaceLocation = super.getUniformLocation("to_shadow_map_space");
		shadowMapLocation = super.getUniformLocation("shadow_map");
	}

	public void bindShadowMapSampler()
	{
		super.uniformInt(shadowMapLocation, 5);
	}

	public void loadShadowMapSpaceMatrix(Matrix4f toShadowMapSpace)
	{
		super.uniformMatrix4(toShadowMapSpaceLocation, toShadowMapSpace);
	}
}
