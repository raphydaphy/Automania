package main.java.com.raphydaphy.automania.renderengine.renderer;

import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.models.RawModel;
import main.java.com.raphydaphy.automania.models.TexturedModel;
import main.java.com.raphydaphy.automania.renderengine.shader.StaticObjectShader;
import main.java.com.raphydaphy.automania.renderengine.load.Material;
import main.java.com.raphydaphy.automania.util.MathUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;

public class StaticObjectRenderer
{
    private StaticObjectShader shader;

    public StaticObjectRenderer(StaticObjectShader shader, Matrix4f projection)
    {
        this.shader = shader;

        shader.bind();
        shader.projection.load(projection);
        shader.unbind();
    }

    public void render(TexturedModel model, List<ModelTransform> batch)
    {
        prepareModel(model);

        for (ModelTransform object : batch)
        {
            prepareInstance(object);
            // Draw the vertices bound in GL_ARRAY_BUFFER using indices from GL_ELEMENT_BUFFER
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }

        unbindModel();
    }

    private void prepareModel(TexturedModel model)
    {
        // Get the raw model in order to get the vertex array ID and vertex count
        RawModel rawModel = model.getRawModel();

        // Bind the model's vertex array, along with all the pointer data stored in it
        GL30.glBindVertexArray(rawModel.getVAOID());

        // Enable the various vertex buffer arrays which we bound in Loader#storeDataInAttributeList
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // Load the reflection information from the material to the shader
        Material texture = model.getTexture();

        // Set all the normals to point upwards if the material is transparent
	    shader.artificialLighting.load(texture.usesArtificialLighting() ? 1 : 0);

        // Disable culling if we are rendering a transparent texture to ensure that all faces are rendered
        if (texture.isTransparent())
        {
            WorldRenderManager.disableCulling();
        }

        // Bind the texture to the sampler with id #0
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    }

    private void unbindModel()
    {
        // Re-enable culling in case it was disabled this batch
        WorldRenderManager.enableCulling();

        // Unbind everything to prevent it being accidently modified
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(ModelTransform object)
    {
        // Generate a transformation matrix based on the transform position, rotation and scale
        Matrix4f transformationMatrix = MathUtils.createTransformationMatrix(object.getTransform().getPosition(),
                object.getTransform().getRotX(), object.getTransform().getRotY(), object.getTransform().getRotZ(),
                object.getTransform().getScale());
        shader.transform.load(transformationMatrix);
    }
}
