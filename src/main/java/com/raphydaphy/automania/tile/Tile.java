package main.java.com.raphydaphy.automania.tile;

import java.util.HashMap;
import java.util.Map;

public class Tile
{
    public static final Map<Integer, Tile> REGISTRY = new HashMap<>();

    private String registryName;
    private String unlocalizedName;

    private boolean visible;
    private int id;

    private TileRenderer<Tile> renderer;

    public Tile(String name)
    {
        id = -1;
        visible = true;

        setRegistryName(name);
        setUnlocalizedName(name);
    }

    public Tile register()
    {
        id = REGISTRY.size() + 1;
        REGISTRY.put(id, this);

        renderer = new TileRenderer<>(this);
        return this;
    }

    public Tile setRegistryName(String name)
    {
        this.registryName = name;
        return this;
    }

    public Tile setUnlocalizedName(String name)
    {
        this.unlocalizedName = name;
        return this;
    }

    public Tile setVisible(boolean visible)
    {
        this.visible = visible;
        return this;
    }

    public Tile setRenderer(TileRenderer<Tile> other)
    {
        this.renderer = other;
        return this;
    }

    public String getRegistryName()
    {
        return registryName;
    }

    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public TileRenderer<Tile> getRenderer()
    {
        return renderer;
    }
}
