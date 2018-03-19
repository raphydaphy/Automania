package main.java.com.raphydaphy.automania.renderengine.shader;

public class ObjectShader extends WorldShader
{
	private static final String name = "src/main/resources/shaders/object";
	private int artificialLightingLocation;

	public ObjectShader()
	{
		super(name);
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttributes();
		super.bindAttribute(2, "tex_coords");
	}

	@Override
	protected void getAllUniformLocations()
	{
		super.getAllUniformLocations();
		artificialLightingLocation = super.getUniformLocation("artificial_lighting");
	}

	public void setArtificialLighting(boolean useArtificialLighting)
	{
		super.uniformInt(artificialLightingLocation, useArtificialLighting ? 1 : 0);
	}
}
