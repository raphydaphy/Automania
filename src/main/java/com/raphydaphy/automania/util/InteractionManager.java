package main.java.com.raphydaphy.automania.util;

import main.java.com.raphydaphy.automania.Automania;
import main.java.com.raphydaphy.automania.core.Window;
import main.java.com.raphydaphy.automania.entity.Player;
import main.java.com.raphydaphy.automania.graphics.Camera;
import main.java.com.raphydaphy.automania.graphics.Renderer;
import main.java.com.raphydaphy.automania.init.GameTiles;
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

            World world = Automania.getInstance().getGame().getWorld();

            Tile existingTile = world.getTile(worldX, worldY);
            if (existingTile != null && ((button == 0 && existingTile != GameTiles.AIR) || (button != 0 && existingTile == GameTiles.AIR)))
            {
                world.setTile(GameTiles.AIR, worldX, worldY);
                existingTile.onRemoved(world, worldX, worldY);
                Tile.REGISTRY.get(button).doPlace(world, worldX, worldY);
            }
        }
    }

    public void update(Window window, float delta, World world, Player player, Renderer renderer)
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

        if (window.isKeyDown(GLFW.GLFW_KEY_SPACE) && player.onGround(world))
        {
            if (player.lastOnGround == player.fallTime && player.onGround(world))
            {
                player.lastOnGround = GLFW.glfwGetTime();
                player.motionY += 120 * delta;

                System.out.println("my " + player.motionY);
            }
        }

        if (player.lastOnGround != player.fallTime || !player.onGround(world))
        {
            boolean doneFalling = false;
            player.fallTime = GLFW.glfwGetTime() - player.lastOnGround;

            float addY = (player.motionY * (float) player.fallTime) * delta;
            if (!player.collides(world, 0, addY))
            {
                player.getTransform().pos.add(0, addY, 0);
                player.motionY -= 3f * delta;
            } else
            {
                player.getTransform().pos.add(0, (float) Math.floor(player.getTransform().pos.y) - player.getTransform().pos.y + 0.5f, 0);
                doneFalling = true;
            }
            if (doneFalling || player.onGround(world))
            {
                player.fallTime = player.lastOnGround;
            }
        } else
        {
            player.motionY = 0;
            player.lastOnGround = GLFW.glfwGetTime();
            player.fallTime = player.lastOnGround;
        }

        camera.getPosition().set(player.getTransform().pos.mul(-renderer.getScale(), new Vector3f()));
    }
}
