package main.java.com.raphydaphy.automania;

import main.java.com.raphydaphy.automania.renderengine.*;
import main.java.com.raphydaphy.automania.renderengine.renderer.FontRenderManager;
import main.java.com.raphydaphy.automania.state.LoadingState;
import main.java.com.raphydaphy.automania.state.Resources;
import main.java.com.raphydaphy.automania.state.State;
import main.java.com.raphydaphy.automania.terrain.*;
import main.java.com.raphydaphy.automania.terrain.biome.BiomeRegistry;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

public class Main
{
	public static void main(String[] args)
	{
		DisplayManager.createDisplay("Automania");

		Resources.init();
		FontRenderManager.init(Resources.loader);

		World world = new World(Sys.getTime(), Resources.loader);

		BiomeRegistry.init();

		State state = new LoadingState().bind();
		State nextState = state;

		while (!Display.isCloseRequested())
		{
			if (state != nextState)
			{
				state.unbind();
				nextState.bind();

				state = nextState;
			}

			nextState = state.update(world);

			DisplayManager.updateDisplay();
		}

		state.unbind();
		FontRenderManager.cleanup();
		Resources.loader.cleanup();
		DisplayManager.closeDisplay();
	}
}
