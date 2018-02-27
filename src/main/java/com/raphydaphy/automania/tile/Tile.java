package main.java.com.raphydaphy.automania.tile;

import main.java.com.raphydaphy.automania.init.GameTiles;
import main.java.com.raphydaphy.automania.util.AABB;
import main.java.com.raphydaphy.automania.util.Direction;
import main.java.com.raphydaphy.automania.world.World;

import java.util.ArrayList;
import java.util.List;

public class Tile
{
	public static final List<Tile> REGISTRY = new ArrayList<>();
	public static final AABB TILE_AABB = new AABB(0, 0, 1, 1);

	private String registryName;
	private String unlocalizedName;

	private boolean visible;
	private boolean hasGravity;

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

	public Tile setHasGravity(boolean hasGravity)
	{
		this.hasGravity = hasGravity;
		return this;
	}

	public String getRegistryName()
	{
		return registryName;
	}

	public Tile setRegistryName(String name)
	{
		this.registryName = name;
		return this;
	}

	public String getUnlocalizedName()
	{
		return unlocalizedName;
	}

	public Tile setUnlocalizedName(String name)
	{
		this.unlocalizedName = name;
		return this;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public Tile setVisible(boolean visible)
	{
		this.visible = visible;
		return this;
	}

	public boolean hasGravity()
	{
		return hasGravity;
	}

	public TileRenderer<Tile> getRenderer()
	{
		return renderer;
	}

	public Tile setRenderer(TileRenderer<Tile> other)
	{
		this.renderer = other;
		return this;
	}

	public AABB getBounds(World world, int x, int y)
	{
		return isVisible() ? TILE_AABB : null;
	}

	public void onRemoved(World world, int x, int y)
	{
		for (Direction dir : Direction.ADJACENT)
		{
			Tile tile = world.getTile(x + dir.x, y + dir.y);

			if (tile != null)
			{
				tile.onChangedAround(world, x + dir.x, y + dir.y, x, y);
			}
		}
	}

	public void onAdded(World world, int x, int y)
	{
		System.out.println("added at " + x + ", " + y);
	}

	public void onChangedAround(World world, int x, int y, int changedX, int changedY)
	{
		System.out.println("chang aroud");
		Tile below = world.getTile(x, y - 1);

 		System.out.println(below.getRegistryName() + " <> " + this.getRegistryName());
		if (below == GameTiles.AIR)
		{
			world.setTile(this, x, y - 1);
			world.setTile(GameTiles.AIR, x, y);

			System.out.println("bam");

			for (Direction dir : Direction.ADJACENT)
			{
				if (dir != Direction.UP)
				{
					Tile tile = world.getTile(x + dir.x, y - 1 + dir.y);

					if (tile != null)
					{
						tile.onChangedAround(world, x + dir.x, y - 1 + dir.y, x, y - 1);
					}
				}
			}
		}
	}

	public void doPlace(World world, int x, int y)
	{
		world.setTile(this, x, y);
		onAdded(world, x, y);

		for (Direction dir : Direction.ADJACENT)
		{
			Tile tile = world.getTile(x + dir.x, y + dir.y);

			if (tile != null)
			{
				System.out.println(dir.toString());
				tile.onChangedAround(world, x + dir.x, y + dir.y, x, y);
			}
		}
	}
}
