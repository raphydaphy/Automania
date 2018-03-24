package main.java.com.raphydaphy.automania.renderengine.renderer;

import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.render.SkyboxGenerator;
import main.java.com.raphydaphy.automania.renderengine.DisplayManager;
import main.java.com.raphydaphy.automania.renderengine.shader.ShaderProgram;
import main.java.com.raphydaphy.automania.renderengine.shader.SkyboxShader;
import main.java.com.raphydaphy.automania.util.MathUtils;
import main.java.com.raphydaphy.automania.util.VertexArray;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxRenderer
{
	private static final float SIZE = 550;
	private static final int SEG_COUNT = 25;

	public static Vector3f HORIZON = new Vector3f(1, 0.87f, 0.6f);
	public static Vector3f SKY = new Vector3f(0.6f, 0.9f, 1f);

	private VertexArray vao;
	private SkyboxShader shader;

	private Matrix4f projectionMatrix;
	private Matrix4f projectionViewMatrix = new Matrix4f();

	public SkyboxRenderer(SkyboxShader shader, Matrix4f projectionMatrix)
	{
		SKY = new Vector3f(0.2f, 0.3f, 0.1f);
		this.shader = shader;
		this.projectionMatrix = projectionMatrix;
		vao = new SkyboxGenerator(SEG_COUNT, (float) (Math.PI / 2f)).generateMeshVao();

		shader.bind();
		shader.skyboxSize.load(SIZE);
		shader.unbind();
	}

	public void render(Camera camera)
	{
		prepare(camera);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, (SEG_COUNT + 1) * 2);
		finishRendering();
	}

	private void prepare(Camera camera)
	{
		vao.bind();
		GL11.glDepthMask(false);
		shader.bind();
		shader.horizonColour.load(HORIZON);
		shader.skyColour.load(SKY);
		Matrix4f.mul(projectionMatrix, MathUtils.createViewMatrix(camera), projectionViewMatrix);
		shader.projectionView.load(projectionViewMatrix);

	}

	private void finishRendering()
	{
		vao.unbind();
		GL11.glDepthMask(true);
		shader.unbind();
	}

	public void setProjectionMatrix(Matrix4f newProjection)
	{
		this.projectionMatrix = newProjection;
	}
}
