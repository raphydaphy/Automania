package main.java.com.raphydaphy.automania.tile;

import main.java.com.raphydaphy.automania.init.GameTiles;
import main.java.com.raphydaphy.automania.util.AABB;
import main.java.com.raphydaphy.automania.util.Direction;
import main.java.com.raphydaphy.automania.world.World;

import java.util.ArrayList;
import java.util.List;

public class TileGrass extends Tile
{

    public TileGrass()
    {
        super("grass");
    }

    @Override
    public void onAdded(World world, int x, int y)
    {
        super.onAdded(world, x, y);
        Tile above = world.getTile(x, y + 1);

        if (above != null && above.isVisible())
        {
            world.setTile(GameTiles.DIRT, x, y);
        }
    }

    @Override
    public void onChangedAround(World world, int x, int y, int changedX, int changedY)
    {
        super.onChangedAround(world, x,  y, changedX, changedY);

        int yDist = changedY - y;
        Tile changed = world.getTile(changedX, changedY);

        if (changed != null && yDist == 1)
        {
            if (changed.isVisible())
            {
                world.setTile(GameTiles.DIRT, x, y);
            }
        }
    }
}
