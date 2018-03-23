package main.java.com.raphydaphy.automania.state;

import main.java.com.raphydaphy.automania.terrain.World;

public abstract class State
{
	public abstract State update(World world);

	public abstract State bind();

	public abstract void unbind();
}
