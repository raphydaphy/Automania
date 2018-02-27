package main.java.com.raphydaphy.automania.init;

import main.java.com.raphydaphy.automania.tile.Tile;
import main.java.com.raphydaphy.automania.tile.TileDirt;
import main.java.com.raphydaphy.automania.tile.TileGrass;

public class GameTiles
{
    public static Tile AIR;
    public static Tile GRASS;
    public static Tile DIRT;
    public static Tile STONE;

    public static void init()
    {
        AIR = new Tile("air").setVisible(false).setHasGravity(false).register();
        GRASS = new TileGrass().register();
        DIRT = new TileDirt().register();
        STONE = new Tile("stone").register();
    }
}
