package main.java.com.raphydaphy.automania.renderengine.renderer;

import main.java.com.raphydaphy.automania.models.AnimatedModel;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.renderengine.shader.AnimatedObjectShader;
import main.java.com.raphydaphy.automania.renderengine.load.Material;
import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;

public class AnimatedObjectRenderer
{
	private AnimatedObjectShader shader;

	public AnimatedObjectRenderer(AnimatedObjectShader shader, Matrix4f projection)
	{
		this.shader = shader;

		shader.bind();
		shader.projection.load(projection);
		shader.unbind();
	}

	public void render(AnimatedModel model, List<ModelTransform> batch)
	{
		prepareModel(model);

		for (ModelTransform object : batch)
		{
			prepareInstance(object);
			GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

			WorldRenderManager.enableCulling();
			model.vao.unbind(0, 1, 2, 3, 4);
		}

	}

	private void prepareModel(AnimatedModel model)
	{
		model.vao.bind(0, 1, 2, 3, 4);

		Material texture = model.getTexture();

		if (texture.isTransparent())
		{
			WorldRenderManager.disableCulling();
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
	}

	private void prepareInstance(ModelTransform object)
	{
		// Generate a transformation matrix based on the transform position, rotation and scale
		Matrix4f transformationMatrix = MathUtils.createTransformationMatrix(object.getTransform().getPosition(),
				object.getTransform().getRotX(), object.getTransform().getRotY(), object.getTransform().getRotZ(),
				object.getTransform().getScale());
		shader.transform.load(transformationMatrix);
		shader.jointTransforms.load(((AnimatedModel)object.getModel()).getJointTransforms());
	}
}
