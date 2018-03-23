package main.java.com.raphydaphy.automania;

import main.java.com.raphydaphy.automania.entity.Player;
import main.java.com.raphydaphy.automania.font.FontType;
import main.java.com.raphydaphy.automania.font.GUIText;
import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.render.Light;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.render.Transform;
import main.java.com.raphydaphy.automania.models.TexturedModel;
import main.java.com.raphydaphy.automania.renderengine.*;
import main.java.com.raphydaphy.automania.models.RawModel;
import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import main.java.com.raphydaphy.automania.renderengine.load.ModelData;
import main.java.com.raphydaphy.automania.renderengine.load.OBJLoader;
import main.java.com.raphydaphy.automania.renderengine.renderer.FontRenderManager;
import main.java.com.raphydaphy.automania.renderengine.renderer.RenderManager;
import main.java.com.raphydaphy.automania.renderengine.shader.Material;
import main.java.com.raphydaphy.automania.state.LoadingState;
import main.java.com.raphydaphy.automania.state.Resources;
import main.java.com.raphydaphy.automania.state.State;
import main.java.com.raphydaphy.automania.terrain.*;
import main.java.com.raphydaphy.automania.terrain.biome.BiomeRegistry;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

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
