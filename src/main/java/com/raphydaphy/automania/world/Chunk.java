package main.java.com.raphydaphy.automania.world;

import main.java.com.raphydaphy.automania.init.GameTiles;
import main.java.com.raphydaphy.automania.tile.Tile;

public class Chunk
{
    public static final int CHUNK_SIZE = 32;

    public final int chunkX;
    public final int chunkY;

    private Tile[] tiles;

    public Chunk(int x, int y)
    {
        this.chunkX = x;
        this.chunkY = y;

        tiles = new Tile[CHUNK_SIZE * CHUNK_SIZE];

        for (int i = 0; i < CHUNK_SIZE; i++)
        {
            for (int j = 0; j < CHUNK_SIZE; j++)
            {
                tiles[i + j * CHUNK_SIZE] = GameTiles.DIRT;
            }
        }
    }

    public Tile getTileFromWorldCoord(int x, int y)
    {
        x += chunkX * CHUNK_SIZE;
        y += chunkY * CHUNK_SIZE;

        x %= CHUNK_SIZE;
        y %= CHUNK_SIZE;

        return getTileFromInnerCoord(x, y);
    }

    public Tile getTileFromInnerCoord(int x, int y)
    {
        if (x >= 0 && y >= 0 && x < CHUNK_SIZE && y < CHUNK_SIZE)
        {
            return tiles[x + y * CHUNK_SIZE];
        }
        return null;
    }

    public boolean setTileFromWorldCoord(Tile tile, int x, int y)
    {
        x += chunkX * CHUNK_SIZE;
        y += chunkY * CHUNK_SIZE;

        x %= CHUNK_SIZE;
        y %= CHUNK_SIZE;

        return setTileFromInnerCoord(tile, x, y);
    }

    public boolean setTileFromInnerCoord(Tile tile, int x, int y)
    {
        if (x >= 0 && y >= 0 && x < CHUNK_SIZE && y < CHUNK_SIZE)
        {
            tiles[x + y * CHUNK_SIZE] = tile;
            return true;
        }
        return false;
    }
}
