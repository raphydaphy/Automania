package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformFloat;
import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformMatrix;
import main.java.com.raphydaphy.automania.renderengine.shader.uniform.UniformVector;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxShader extends ShaderProgram
{
	private static final String name = "src/main/resources/shaders/skybox";

	public UniformMatrix projectionView = new UniformMatrix("projection_view");
	public UniformVector<Vector3f> horizonColour = new UniformVector<>("horizon_color");
	public UniformVector<Vector3f> skyColour = new UniformVector<>("sky_color");
	public UniformFloat skyboxSize = new UniformFloat("skybox_size");

	public SkyboxShader() {
		super(name);
		super.storeAllUniformLocations(projectionView, horizonColour, skyboxSize, skyColour);
	}
}
