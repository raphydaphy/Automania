package main.java.com.raphydaphy.automania.state;

import main.java.com.raphydaphy.automania.font.GUIText;
import main.java.com.raphydaphy.automania.renderengine.renderer.FontRenderManager;
import main.java.com.raphydaphy.automania.terrain.Terrain;
import main.java.com.raphydaphy.automania.terrain.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class LoadingState extends State
{
	private boolean loaded = false;
	private GUIText loadingText;

	@Override
	public State update(World world)
	{
		loaded = true;

		world.updateVisibleChunks(new Vector3f(0,0,0));

		for (Terrain terrain : world.getChunks().values())
		{
			if (terrain != null && !terrain.received)
			{
				terrain.processMesh(Resources.loader);
				loaded = false;
			}
		}

		FontRenderManager.render();

		if (world.getChunks().size() > 0 && loaded)
		{
			System.out.println("Finished loading");
			return new GameState();
		}

		return this;
	}

	@Override
	public State bind()
	{
		loadingText = new GUIText("Loading...", 3, Resources.arial, new Vector2f(0, 0.5f), 1f, true);
		loadingText.setColour(1,0,1);
		return this;
	}

	@Override
	public void unbind()
	{
		loadingText.remove();
	}
}
