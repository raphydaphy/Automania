package main.java.com.raphydaphy.automania.tile;

import java.util.ArrayList;
import java.util.List;

public class Tile
{
    public static final List<Tile> REGISTRY = new ArrayList<>();

    private String registryName;
    private String unlocalizedName;

    private boolean visible;

    private TileRenderer<Tile> renderer;

    public Tile(String name)
    {
        visible = true;

        setRegistryName(name);
        setUnlocalizedName(name);
    }

    public Tile register()
    {
        REGISTRY.add(this);

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
