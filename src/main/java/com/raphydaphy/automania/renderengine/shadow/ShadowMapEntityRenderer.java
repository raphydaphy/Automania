package main.java.com.raphydaphy.automania.renderengine.shadow;

import java.util.List;
import java.util.Map;

import main.java.com.raphydaphy.automania.models.IModel;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.renderengine.renderer.WorldRenderManager;
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
	protected void render(Map<IModel, List<ModelTransform>> objects)
	{
		for (IModel model : objects.keySet())
		{
			bindModel(model);

			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

			if (model.getTexture().isTransparent())
			{
				WorldRenderManager.disableCulling();
			}
			for (ModelTransform modelTransform : objects.get(model))
			{
				prepareInstance(modelTransform);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			if (model.getTexture().isTransparent())
			{
				WorldRenderManager.enableCulling();
			}

			unbindModel(model);
		}
	}

	private void bindModel(IModel model)
	{
		GL30.glBindVertexArray(model.getVAOID());
		for (int attribArray : model.getAttribArrays())
		{
			GL20.glEnableVertexAttribArray(attribArray);
		}
	}

	private void unbindModel(IModel model)
	{
		GL30.glBindVertexArray(0);
		for (int attribArray : model.getAttribArrays())
		{
			GL20.glDisableVertexAttribArray(attribArray);
		}
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
		shader.mvpMatrix.load(mvpMatrix);
	}

}
