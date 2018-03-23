package main.java.com.raphydaphy.automania.state;

import main.java.com.raphydaphy.automania.renderengine.DisplayManager;
import main.java.com.raphydaphy.automania.terrain.World;

public abstract class State
{
	public State update(World world)
	{
		if (DisplayManager.hasResized)
		{
			handleResize();
		}

		return this;
	}

	public abstract State bind();

	public abstract void unbind();

	protected void handleResize()
	{
		Resources.arial.resize();
	}
}
