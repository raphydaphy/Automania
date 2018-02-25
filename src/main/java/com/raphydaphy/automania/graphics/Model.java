package main.java.com.raphydaphy.automania.graphics;

import main.java.com.raphydaphy.automania.util.VertexBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class Model
{
    private int drawCount;

    private VertexBuffer vertices;
    private VertexBuffer textures;
    private VertexBuffer indices;

    public Model(float[] vertices, float[] texCoords, int... indices)
    {
        drawCount = indices.length;

        this.vertices = new VertexBuffer().init().bind(GL15.GL_ARRAY_BUFFER).upload(vertices, GL15.GL_STATIC_DRAW).unbind();
        this.textures = new VertexBuffer().init().bind(GL15.GL_ARRAY_BUFFER).upload(texCoords, GL15.GL_STATIC_DRAW).unbind();
        this.indices = new VertexBuffer().init().bind(GL15.GL_ELEMENT_ARRAY_BUFFER).upload(indices, GL15.GL_STATIC_DRAW).unbind();
    }

    public void render()
    {
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        vertices.bind(GL15.GL_ARRAY_BUFFER);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        vertices.unbind();

        textures.bind(GL15.GL_ARRAY_BUFFER);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

        indices.bind(GL15.GL_ELEMENT_ARRAY_BUFFER);
        GL11.glDrawElements(GL11.GL_TRIANGLES, drawCount, GL11.GL_UNSIGNED_INT, 0);

        indices.unbind();
        textures.unbind();

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
    }

    public void delete()
    {
        vertices.delete();
        textures.delete();
        indices.delete();
    }
}
