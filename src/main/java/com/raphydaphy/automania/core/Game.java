package main.java.com.raphydaphy.automania.core;

import main.java.com.raphydaphy.automania.entity.Player;
import main.java.com.raphydaphy.automania.graphics.Renderer;
import main.java.com.raphydaphy.automania.util.InteractionManager;
import main.java.com.raphydaphy.automania.world.Chunk;
import main.java.com.raphydaphy.automania.world.World;

public class Game
{
    private World world;
    private Renderer renderer;
    private Player player;
    private InteractionManager manager;

    public Game init(Window window)
    {
        player = new Player();
        renderer = new Renderer().init(window, player);
        world = new World();

        manager = new InteractionManager();

        window.setCallbacks(manager);

        player.getTransform().pos.add(0, Chunk.CHUNK_SIZE * World.CHUNKS + 0.5f, 0);

        return this;
    }

    public Game update(Window window, float delta)
    {
        manager.update(window, delta, world, player, renderer);
        return this;
    }

    public Game render(float alpha)
    {
        renderer.render(alpha);
        return this;
    }

    public Game cleanup()
    {
        renderer.cleanup();
        return this;
    }

    public Player getPlayer()
    {
        return player;
    }

    public World getWorld()
    {
        return world;
    }

    public Renderer getRenderer()
    {
        return renderer;
    }

    public InteractionManager getManager()
    {
        return manager;
    }
}
