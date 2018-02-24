package main.java.com.raphydaphy.automania.graphics;

import main.java.com.raphydaphy.automania.core.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class Renderer
{
    private VertexArray vao;
    private Shader shader;
    private Camera camera;
    private Matrix4f world;
    private Model square;
    private Texture missing;

    private int scale;
    private int viewX;
    private int viewY;

    public Renderer init(Window window)
    {
        scale = 64;

        vao = new VertexArray().init().bind();

        shader = new Shader("default");

        camera = new Camera(window.getWidth(), window.getHeight());

        calculateView(window);

        world = new Matrix4f().setTranslation(new Vector3f(0));

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
        //shader.setUniform("projection", camera.getProjection().mul(world, new Matrix4f()).mul(new Matrix4f().translate(0, 0, 0)));
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

    public void update(Window window)
    {
        if (window.hasResized())
        {
            camera.setProjection(window.getWidth(), window.getHeight());
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            calculateView(window);
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_A))
        {
            camera.move(5, 0, 0);
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_D))
        {
            camera.move(-5, 0, 0);
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_W))
        {
            camera.move(0, 5, 0);
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_S))
        {
            camera.move(0, -5, 0);
        }
    }

    public void calculateView(Window window)
    {
        viewX = (window.getWidth() / scale) + 4;
        viewY = (window.getHeight() / scale) + 4;
    }
}
