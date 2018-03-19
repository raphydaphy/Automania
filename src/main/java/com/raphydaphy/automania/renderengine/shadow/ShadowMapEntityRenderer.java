package main.java.com.raphydaphy.automania.renderengine.shadow;

import java.util.List;
import java.util.Map;

import main.java.com.raphydaphy.automania.models.RawModel;
import main.java.com.raphydaphy.automania.models.TexturedModel;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.renderengine.renderer.RenderManager;
import main.java.com.raphydaphy.automania.renderengine.shader.ShadowShader;
import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class ShadowMapEntityRenderer
{

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	/**
	 * @param shader               - the simple shader program being used for the shadow render
	 *                             pass.
	 * @param projectionViewMatrix - the orthographic projection matrix multiplied by the light's
	 *                             "view" matrix.
	 */
	protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix)
	{
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	/**
	 * Renders entieis to the shadow map. Each model is first bound and then all
	 * of the entities using that model are rendered to the shadow map.
	 *
	 * @param objects - the entities to be rendered to the shadow map.
	 */
	protected void render(Map<TexturedModel, List<ModelTransform>> objects)
	{
		for (TexturedModel model : objects.keySet())
		{
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
			if (model.getTexture().isTransparent())
			{
				RenderManager.disableCulling();
			}
			for (ModelTransform modelTransform : objects.get(model))
			{
				prepareInstance(modelTransform);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			if (model.getTexture().isTransparent())
			{
				RenderManager.enableCulling();
			}
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	/**
	 * Binds a raw model before rendering. Only the attribute 0 is enabled here
	 * because that is where the positions are stored in the VAO, and only the
	 * positions are required in the vertex shader.
	 *
	 * @param rawModel - the model to be bound.
	 */
	private void bindModel(RawModel rawModel)
	{
		GL30.glBindVertexArray(rawModel.getVAOID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(2);
	}

	/**
	 * Prepares an entity to be rendered. The model matrix is created in the
	 * usual way and then multiplied with the projection and view matrix (often
	 * in the past we've done this in the vertex shader) to create the
	 * mvp-matrix. This is then loaded to the vertex shader as a uniform.
	 *
	 * @param modelTransform - the entity to be prepared for rendering.
	 */
	private void prepareInstance(ModelTransform modelTransform)
	{
		Matrix4f modelMatrix = MathUtils.createTransformationMatrix(modelTransform.getTransform().getPosition(), modelTransform.getTransform().getRotX(), modelTransform.getTransform().getRotY(), modelTransform.getTransform().getRotZ(), modelTransform.getTransform().getScale());
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shader.loadMvpMatrix(mvpMatrix);
	}

}
