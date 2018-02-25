package main.java.com.raphydaphy.automania.core;

import main.java.com.raphydaphy.automania.graphics.Renderer;
import main.java.com.raphydaphy.automania.util.InteractionManager;
import main.java.com.raphydaphy.automania.world.World;

public class Game
{
    private World world;
    private Renderer renderer;
    private InteractionManager manager;

    public Game init(Window window)
    {
        renderer = new Renderer().init(window);
        world = new World();
        manager = new InteractionManager();

        window.setCallbacks(manager);

        return this;
    }

    public Game update(Window window, float delta)
    {
        renderer.update(window);
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
