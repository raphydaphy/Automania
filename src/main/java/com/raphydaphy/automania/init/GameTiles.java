package main.java.com.raphydaphy.automania.init;

import main.java.com.raphydaphy.automania.tile.Tile;

public class GameTiles
{
    public static Tile AIR;
    public static Tile GRASS;
    public static Tile DIRT;
    public static Tile STONE;

    public static void init()
    {
        AIR = new Tile("air").setVisible(false).register();
        GRASS = new Tile("grass").register();
        DIRT = new Tile("dirt").register();
        STONE = new Tile("stone").register();
    }
}
