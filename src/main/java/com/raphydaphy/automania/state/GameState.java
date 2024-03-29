package main.java.com.raphydaphy.automania.state;

import main.java.com.raphydaphy.automania.entity.Player;
import main.java.com.raphydaphy.automania.font.GUIText;
import main.java.com.raphydaphy.automania.models.RawModel;
import main.java.com.raphydaphy.automania.models.TexturedModel;
import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.render.Light;
import main.java.com.raphydaphy.automania.render.ModelTransform;
import main.java.com.raphydaphy.automania.renderengine.DisplayManager;
import main.java.com.raphydaphy.automania.renderengine.load.ModelData;
import main.java.com.raphydaphy.automania.renderengine.load.OBJLoader;
import main.java.com.raphydaphy.automania.renderengine.renderer.FontRenderManager;
import main.java.com.raphydaphy.automania.renderengine.renderer.WorldRenderManager;
import main.java.com.raphydaphy.automania.renderengine.load.Material;
import main.java.com.raphydaphy.automania.terrain.InteractionManager;
import main.java.com.raphydaphy.automania.terrain.Terrain;
import main.java.com.raphydaphy.automania.terrain.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GameState extends State
{
	private TexturedModel treeModel;
	private List<ModelTransform> trees;

	private List<Light> lights;
	private Light sun;

	private Camera camera;
	private WorldRenderManager renderer;
	private InteractionManager interactionManager;

	private Player player;
	private GUIText title;

	@Override
	public State update(World world)
	{
		super.update(world);
		float delta = DisplayManager.getFrameTimeSeconds();

		player.move(world, delta);
		camera.move();

		interactionManager.update(world, player, Resources.loader, delta);
		world.updateWorld(trees, treeModel, delta);

		for (Terrain terrain : world.getChunks().values())
		{
			if (terrain.received)
			{
				renderer.processTerrain(terrain);
			}
		}
		renderer.processSimilarObjects(trees);

		renderer.processObject(player.data);

		renderer.renderShadowMap(sun);
		renderer.render(lights, camera);

		FontRenderManager.render();

		return this;
	}

	@Override
	public State bind()
	{
		ModelData treeData = OBJLoader.loadOBJ("tree");
		RawModel treeRaw = Resources.loader.loadToModel(treeData.getVertices(), treeData.getUVS(), treeData.getNormals(), treeData.getIndices());
		Material treeMaterial = new Material(Resources.colors);
		treeModel = new TexturedModel(treeRaw, treeMaterial);
		trees = new ArrayList<>();

		ModelData playerData = OBJLoader.loadOBJ("person");
		RawModel playerRaw = Resources.loader.loadToModel(playerData.getVertices(), playerData.getUVS(), playerData.getNormals(), playerData.getIndices());
		TexturedModel playerModel = new TexturedModel(playerRaw, new Material(Resources.colors));
		player = new Player(playerModel, new Vector3f(0, 10, 0), 0, 180, 0, 0.75f);

		lights = new ArrayList<>();

		float sunBrightness = 0.8f;
		sun = new Light(new Vector3f(50000, 100000, 100000), new Vector3f(sunBrightness, sunBrightness, sunBrightness));
		lights.add(sun);
		lights.add(new Light(new Vector3f(0, 35, 0), new Vector3f(1, 1, 1), new Vector3f(1f, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(-300, 35, -300), new Vector3f(1, 1, 1), new Vector3f(1f, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(-300, 35, -450), new Vector3f(1, 1, 1), new Vector3f(1f, 0.01f, 0.002f)));

		camera = new Camera(player);

		renderer = new WorldRenderManager(camera);
		interactionManager = new InteractionManager(camera, renderer.getProjectionMatrix());

		title = new GUIText("Automania Alpha",2.5f, Resources.arial, new Vector2f(0, 0), 1f,  true);
		title.setColour(1, 1, 0);

		return this;
	}

	@Override
	public void unbind()
	{
		title.remove();
		renderer.cleanup();
	}

	@Override
	protected void handleResize()
	{
		super.handleResize();
		renderer.recalculateProjection();
	}
}
