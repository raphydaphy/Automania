package main.java.com.raphydaphy.automania.util;

import main.java.com.raphydaphy.automania.Automania;
import main.java.com.raphydaphy.automania.core.Window;
import main.java.com.raphydaphy.automania.entity.Player;
import main.java.com.raphydaphy.automania.graphics.Camera;
import main.java.com.raphydaphy.automania.graphics.Renderer;
import main.java.com.raphydaphy.automania.tile.Tile;
import main.java.com.raphydaphy.automania.world.World;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;

public class InteractionManager
{
    public void processMouseClick(Window window, long windowID, int button, int action, int mods)
    {
        if (button < Tile.REGISTRY.size())
        {
            DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);

            GLFW.glfwGetCursorPos(windowID, posX, posY);

            Camera camera = Automania.getInstance().getGame().getRenderer().getCamera();
            int scale = Automania.getInstance().getGame().getRenderer().getScale();

            int mouseX = (int) posX.get();
            int mouseY = (int) posY.get();

            int worldX = (int) Math.floor((mouseX - (window.getWidth() / 2) - camera.getPosition().x) / scale);
            int worldY = (int) -(Math.ceil((mouseY - (window.getHeight() / 2) + camera.getPosition().y) / scale));

            if (Automania.getInstance().getGame().getWorld().getTile(worldX, worldY) != null)
            {
                Automania.getInstance().getGame().getWorld().setTile(Tile.REGISTRY.get(button), worldX, worldY);
            }
        }
    }

    public InteractionManager update(Window window, float delta, World world, Player player, Renderer renderer)
    {
        Camera camera = renderer.getCamera();

        if (window.hasResized())
        {
            camera.setProjection(window.getWidth(), window.getHeight());
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            renderer.calculateView(window);
        }

        float moveSpeed = 5 * delta;

        if (window.isKeyDown(GLFW.GLFW_KEY_A))
        {
            if (!player.collides(world, -moveSpeed, 0))
            {
                player.getTransform().pos.add(-moveSpeed, 0, 0);
            }
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_D))
        {
            if (!player.collides(world, moveSpeed, 0))
            {
                player.getTransform().pos.add(moveSpeed, 0, 0);
            }
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_W))
        {
            if (!player.collides(world, 0, moveSpeed))
            {
                player.getTransform().pos.add(0, moveSpeed, 0);
            }
        }

        if (window.isKeyDown(GLFW.GLFW_KEY_S))
        {
            if (!player.collides(world, 0, -moveSpeed))
            {
                player.getTransform().pos.add(0, -moveSpeed, 0);
            }
        }

        camera.getPosition().set(player.getTransform().pos.mul(-renderer.getScale(), new Vector3f()));

        return this;
    }
}
