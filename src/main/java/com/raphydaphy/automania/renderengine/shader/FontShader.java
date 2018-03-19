package main.java.com.raphydaphy.automania.renderengine.shader;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontShader extends ShaderProgram
{
	private static final String name = "src/main/resources/shaders/font";

	private int colorLocation;
	private int translationLocation;

	public FontShader()
	{
		super(name);
	}

	@Override
	protected void getAllUniformLocations()
	{
		colorLocation = super.getUniformLocation("color");
		translationLocation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes()
	{
		super.bindAttribute(0, "position");
		super.bindAttribute(2, "uvs");
	}

	public void loadColor(Vector3f color)
	{
		super.uniformVector3(colorLocation, color);
	}

	public void loadTranslation(Vector2f translation)
	{
		super.uniformVector2(translationLocation, translation);
	}

}
