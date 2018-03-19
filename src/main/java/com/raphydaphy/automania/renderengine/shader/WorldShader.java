package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.render.Light;
import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class WorldShader extends ShaderProgram
{
	private int transformLocation;
	private int projectionLocation;
	private int viewLocation;
	private int[] lightPositionLocation;
	private int[] lightColorLocation;
	private int[] lightAttenuationLocation;
	private int shineDamperLocation;
	private int reflectivityLocation;
	private int skyColorLocation;

	public WorldShader(String name)
	{
		super(name);
	}

	@Override
	protected void bindAttributes()
	{
		// The ID's here are the ID's of the vertex buffers within the vertex array
		// The number is whatever we tell it to be in Loader#loadToVAO
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "normal");
	}

	@Override
	protected void getAllUniformLocations()
	{
		transformLocation = super.getUniformLocation("transform");
		projectionLocation = super.getUniformLocation("projection");
		viewLocation = super.getUniformLocation("view");
		shineDamperLocation = super.getUniformLocation("shine_damper");
		reflectivityLocation = super.getUniformLocation("reflectivity");
		skyColorLocation = super.getUniformLocation("sky_color");

		lightPositionLocation = new int[MAX_LIGHTS];
		lightColorLocation = new int[MAX_LIGHTS];
		lightAttenuationLocation = new int[MAX_LIGHTS];

		for (int light = 0; light < MAX_LIGHTS; light++)
		{
			lightPositionLocation[light] = super.getUniformLocation("light_position[" + light + "]");
			lightColorLocation[light] = super.getUniformLocation("light_color[" + light + "]");
			lightAttenuationLocation[light] = super.getUniformLocation("light_attenuation[" + light + "]");
		}
	}

	public void loadSkyColor(Vector3f skyColor)
	{
		super.uniformVector3(skyColorLocation, skyColor);
	}

	public void loadReflectionInfo(float damper, float reflectivity)
	{
		super.uniformFloat(shineDamperLocation, damper);
		super.uniformFloat(reflectivityLocation, reflectivity);
	}

	public void loadTransformationMatrix(Matrix4f transform)
	{
		super.uniformMatrix4(transformLocation, transform);
	}

	public void loadProjectionMatrix(Matrix4f projection)
	{
		super.uniformMatrix4(projectionLocation, projection);
	}

	public void loadViewMatrix(Camera camera)
	{
		super.uniformMatrix4(viewLocation, MathUtils.createViewMatrix(camera));
	}

	public void loadLights(List<Light> lights)
	{
		for (int light = 0; light < MAX_LIGHTS; light++)
		{
			if (light < lights.size())
			{
				super.uniformVector3(lightPositionLocation[light], lights.get(light).getPosition());
				super.uniformVector3(lightColorLocation[light], lights.get(light).getColor());
				super.uniformVector3(lightAttenuationLocation[light], lights.get(light).getAttenuation());
			} else
			{
				super.uniformVector3(lightAttenuationLocation[light], new Vector3f(1, 0, 0));
			}
		}

	}
}
