package main.java.com.raphydaphy.automania.tile;

import main.java.com.raphydaphy.automania.init.GameTiles;
import main.java.com.raphydaphy.automania.world.World;

public class TileDirt extends Tile
{

    public TileDirt()
    {
        super("dirt");
    }

    @Override
    public void onAdded(World world, int x, int y)
    {
        Tile above = world.getTile(x, y + 1);

        if (above == null || !above.isVisible())
        {
            world.setTile(GameTiles.GRASS, x, y);
        }
    }

    @Override
    public void onChangedAround(World world, int x, int y, int changedX, int changedY)
    {
        int yDist = changedY - y;
        Tile changed = world.getTile(changedX, changedY);

        if (changed != null && yDist == 1)
        {
            if (!changed.isVisible())
            {
                world.setTile(GameTiles.GRASS, x, y);
            }
        }
    }
}
