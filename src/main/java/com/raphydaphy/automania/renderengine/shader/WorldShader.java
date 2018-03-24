package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.render.Light;
import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformMatrix;
import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformVector;
import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformVectors;
import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldShader extends ShaderProgram
{
	public UniformMatrix transform = new UniformMatrix("transform");
	public UniformMatrix projection = new UniformMatrix("projection");
	public UniformMatrix view = new UniformMatrix("view");

	private UniformVectors<Vector3f> lightPosition = new UniformVectors<>("light_position", MAX_LIGHTS);
	private UniformVectors<Vector3f> lightColor = new UniformVectors<>("light_color", MAX_LIGHTS);
	private UniformVectors<Vector3f> lightAttenuation = new UniformVectors<>("light_attenuation", MAX_LIGHTS);

	public UniformVector<Vector3f> skyColor = new UniformVector<>("sky_color");

	public WorldShader(String name, String... additionalAttributes)
	{
		super(name, makeAttributeList(additionalAttributes));
		storeAllUniformLocations(transform, projection, view, lightPosition, lightColor, lightAttenuation, skyColor);
	}

	private static String[] makeAttributeList(String[] additionalAttributes)
	{
		String[] attributes = new String[2 + additionalAttributes.length];
		attributes[0] = "position";
		attributes[1] = "normal";

		for (int additionlAttribute = 0; additionlAttribute < additionalAttributes.length; additionlAttribute++)
		{
			attributes[additionlAttribute + 2] = additionalAttributes[additionlAttribute];
		}

		return attributes;
	}

	public void loadLights(List<Light> lights)
	{
		for (int light = 0; light < MAX_LIGHTS; light++)
		{
			if (light < lights.size())
			{
				lightPosition.load(lights.get(light).getPosition(), light);
				lightColor.load(lights.get(light).getColor(), light);
				lightAttenuation.load(lights.get(light).getAttenuation(), light);
			} else
			{
				lightAttenuation.load(new Vector3f(1, 0, 0), light);
			}
		}
	}
}
