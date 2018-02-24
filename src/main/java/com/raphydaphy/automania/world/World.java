package main.java.com.raphydaphy.automania.world;

import main.java.com.raphydaphy.automania.tile.Tile;

public class World
{
    public static final int CHUNKS = 1;

    private Chunk[] chunks;

    public World()
    {
        chunks = new Chunk[CHUNKS * CHUNKS];
        for (int i = 0; i < CHUNKS; i++)
        {
            for (int j = 0; j < CHUNKS; j++)
            {
                chunks[i + j * CHUNKS] = new Chunk(i, j);
            }
        }
    }

    public Chunk getChunkFromWorldCoords(int x, int y)
    {
        x /= Chunk.CHUNK_SIZE;
        y /= Chunk.CHUNK_SIZE;

        return getChunkFromChunkCoords(x, y);
    }

    public Chunk getChunkFromChunkCoords(int x, int y)
    {
        if (x < CHUNKS && y < CHUNKS)
        {
            return chunks[x + y * CHUNKS];
        }
        return null;
    }

    public Tile getTile(int x, int y)
    {
        Chunk chunk = getChunkFromWorldCoords(x, y);

        if (chunk != null)
        {
            return chunk.getTileFromWorldCoord(x, y);
        }

        return null;
    }

    public boolean setTile(Tile tile, int x, int y)
    {
        Chunk chunk = getChunkFromWorldCoords(x, y);

        System.out.println("Trying to set tile at world coord " + x + ", " + y);
        if (chunk != null)
        {
            System.out.println("Got chunk to set tile at chunk grid pos " + chunk.chunkX + ", " + chunk.chunkY);
            return chunk.setTileFromWorldCoord(tile, x, y);
        }

        return false;
    }
}
