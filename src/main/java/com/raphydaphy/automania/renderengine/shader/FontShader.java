package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformVector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontShader extends ShaderProgram
{
	private static final String name = "src/main/resources/shaders/font";

	public UniformVector<Vector3f> color = new UniformVector<>("color");
	public UniformVector<Vector2f> translation = new UniformVector<>("translation");

	public FontShader()
	{
		super(name, "position", EMPTY_ATTRIBUTE, "uvs");
		storeAllUniformLocations(color, translation);
	}

}
