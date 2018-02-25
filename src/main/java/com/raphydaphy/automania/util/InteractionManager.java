package main.java.com.raphydaphy.automania.util;

import main.java.com.raphydaphy.automania.Automania;
import main.java.com.raphydaphy.automania.core.Window;
import main.java.com.raphydaphy.automania.graphics.Camera;
import main.java.com.raphydaphy.automania.tile.Tile;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

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

            int worldX = (int)Math.floor((mouseX - (window.getWidth() / 2) - camera.getPosition().x) / scale);
            int worldY = (int)-(Math.ceil((mouseY - (window.getHeight() / 2) + camera.getPosition().y) / scale));

            if (Automania.getInstance().getGame().getWorld().getTile(worldX, worldY) != null)
            {
                Automania.getInstance().getGame().getWorld().setTile(Tile.REGISTRY.get(button), worldX, worldY);
            }
        }
    }
}
