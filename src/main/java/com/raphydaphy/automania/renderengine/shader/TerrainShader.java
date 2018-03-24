package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformInt;
import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformMatrix;
import org.lwjgl.util.vector.Matrix4f;

public class TerrainShader extends WorldShader
{
	private static final String name = "src/main/resources/shaders/terrain";

	private UniformInt shadowMapSampler = new UniformInt("shadow_map");
	public UniformMatrix toShadowMapSpace = new UniformMatrix("to_shadow_map_space");

	public TerrainShader()
	{
		super(name, "color");
		storeAllUniformLocations(shadowMapSampler, toShadowMapSpace);

		bind();
		shadowMapSampler.load(5);
		unbind();
	}
}
