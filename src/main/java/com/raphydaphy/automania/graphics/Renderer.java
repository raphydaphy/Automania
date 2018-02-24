package main.java.com.raphydaphy.automania.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class Renderer
{
    private VertexArray vao;
    private Shader shader;
    private Model square;
    private Texture missing;

    public Renderer init()
    {
        vao = new VertexArray().init().bind();

        shader = new Shader("default");

        square = new Model(new float[]{-0.5f, 0.5f, 0, 0.5f, 0.5f, 0, 0.5f, -0.5f, 0, -0.5f, -0.5f, 0},
                new float[]{0, 0, 1, 0, 1, 1, 0, 1}, 0, 1, 2, 2, 3, 0);

        missing = new Texture("hah i doint exit");
        vao.unbind();

        return this;
    }

    public void render(float alpha)
    {
        vao.bind();

        shader.bind();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        missing.bind(0);

        shader.setUniform("sampler", 0);
        shader.setUniform("projection", new Matrix4f().translate(new Vector3f(0)));
        square.render();

        vao.unbind();
    }

    public void cleanup()
    {
        missing.delete();
        square.delete();
        vao.delete();
        shader.delete();
    }
}
