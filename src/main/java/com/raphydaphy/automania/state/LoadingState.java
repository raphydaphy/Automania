package main.java.com.raphydaphy.automania.state;

import main.java.com.raphydaphy.automania.font.GUIText;
import main.java.com.raphydaphy.automania.renderengine.renderer.FontRenderManager;
import main.java.com.raphydaphy.automania.terrain.Terrain;
import main.java.com.raphydaphy.automania.terrain.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class LoadingState extends State
{
	private boolean loaded = false;
	private GUIText loadingText;

	@Override
	public State update(World world)
	{
		super.update(world);

		loaded = true;

		world.updateVisibleChunks(new Vector3f(0,0,0));

		int processed = 0;

		for (Terrain terrain : world.getChunks().values())
		{
			if (terrain != null && !terrain.received)
			{
				if (terrain.processMesh(Resources.loader))
				{
					processed++;
				}
				loaded = false;
			}

			if (processed > 1)
			{
				break;
			}
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

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
