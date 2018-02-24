package main.java.com.raphydaphy.automania.graphics;

import main.java.com.raphydaphy.automania.core.Window;
import main.java.com.raphydaphy.automania.init.GameTiles;
import main.java.com.raphydaphy.automania.tile.Tile;
import main.java.com.raphydaphy.automania.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;

public class Renderer
{
    private VertexArray vao;
    private Shader shader;
    private Camera camera;
    private World world;
    private Matrix4f view;
    private Model square;

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

        GameTiles.init();

        world = new World();

        view = new Matrix4f().setTranslation(new Vector3f(0)).scale(scale);

        square = new Model(new float[]{-0.5f, 0.5f, 0, 0.5f, 0.5f, 0, 0.5f, -0.5f, 0, -0.5f, -0.5f, 0},
                new float[]{0, 0, 1, 0, 1, 1, 0, 1}, 0, 1, 2, 2, 3, 0);



        vao.unbind();

        return this;
    }

    public void render(float alpha)
    {
        vao.bind();

        shader.bind();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        int screenX = (int) camera.getPosition().x / (scale);
        int screenY = (int) camera.getPosition().y / (scale);

        for (int i = 0; i < viewX; i++)
        {
            for (int j = 0; j < viewY; j++)
            {
                int x = i - screenX - (viewX / 2) + 1;
                int y = -j - screenY + (viewY / 2);

                Tile tile = world.getTile(x, y);

                if (tile != null && tile.isVisible())
                {
                    tile.getRenderer().render(shader, world, camera, square, view, x, y);
                }
            }
        }

        vao.unbind();
    }

    public void cleanup()
    {
        for (Tile tile : Tile.REGISTRY)
        {
            tile.getRenderer().delete();
        }
        square.delete();
        vao.delete();
        shader.delete();
    }

    public void processMouseClick(Window window, long windowID, int button, int action, int mods)
    {
        if (button < Tile.REGISTRY.size())
        {
            DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
            GLFW.glfwGetCursorPos(windowID, posX, posY);

            int mouseX = (int) posX.get();
            int mouseY = (int) posY.get();

            int worldX = Math.round((mouseX - (window.getWidth() / 2) - camera.getPosition().x) / scale);
            int worldY = -(Math.round((mouseY - (window.getHeight() / 2) + camera.getPosition().y) / scale));

            if (world.getTile(worldX, worldY) != null)
            {
                world.setTile(Tile.REGISTRY.get(button), worldX, worldY);
            }
        }
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
            camera.move(0, -5, 0);
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_S))
        {
            camera.move(0, 5, 0);
        }
    }

    public void calculateView(Window window)
    {
        viewX = (window.getWidth() / scale) + 4;
        viewY = (window.getHeight() / scale) + 4;
    }
}
