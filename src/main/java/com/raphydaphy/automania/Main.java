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
import main.java.com.raphydaphy.automania.terrain.*;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.util.*;

public class Main
{
	public static void main(String[] args)
	{
		DisplayManager.createDisplay("Automania");

		Loader loader = new Loader();
		FontRenderManager.init(loader);

		FontType arial = new FontType(loader.loadTextureExact("src/main/resources/fonts/arial.png", 0), new File("src/main/resources/fonts/arial.fnt"));
		GUIText info = new GUIText("Automania Alpha",2.5f, arial, new Vector2f(0, 0), 1f,  true);
		info.setColour(1, 1, 0);
		int colors = loader.loadTexture("colors");

		World world = new World(Sys.getTime(), loader);

		ModelData treeData = OBJLoader.loadOBJ("tree");
		RawModel treeRaw = loader.loadToModel(treeData.getVertices(), treeData.getUVS(), treeData.getNormals(), treeData.getIndices());
		Material treeMaterial = new Material(colors);
		TexturedModel treeModel = new TexturedModel(treeRaw, treeMaterial);
		List<ModelTransform> trees = new ArrayList<>();

		ModelData playerData = OBJLoader.loadOBJ("person");
		RawModel playerRaw = loader.loadToModel(playerData.getVertices(), playerData.getUVS(), playerData.getNormals(), playerData.getIndices());
		TexturedModel playerModel = new TexturedModel(playerRaw, new Material(colors));
		Player player = new Player(playerModel, new Vector3f(0, 0, 0), 0, 180, 0, 0.75f);

		List<Light> lights = new ArrayList<>();

		float sunBrightness = 0.8f;
		Light sun = new Light(new Vector3f(50000, 100000, 100000), new Vector3f(sunBrightness, sunBrightness, sunBrightness));
		lights.add(sun);
		lights.add(new Light(new Vector3f(0, 35, 0), new Vector3f(1, 1, 1), new Vector3f(1f, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(-300, 35, -300), new Vector3f(1, 1, 1), new Vector3f(1f, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(-300, 35, -450), new Vector3f(1, 1, 1), new Vector3f(1f, 0.01f, 0.002f)));

		Camera camera = new Camera(player);
		RenderManager renderer = new RenderManager(camera);

		InteractionManager interactionManager = new InteractionManager(camera, renderer.getProjectionMatrix());

		while (!Display.isCloseRequested())
		{
			float delta = DisplayManager.getFrameTimeSeconds();

			player.move(world, delta);
			camera.move();

			interactionManager.update(world, player, loader, delta);
			world.updateWorld(trees, treeModel);

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

			DisplayManager.updateDisplay();

			if (DisplayManager.hasResized)
			{
				renderer.recalculateProjection();
			}
		}

		renderer.cleanup();
		FontRenderManager.cleanup();
		loader.cleanup();
		DisplayManager.closeDisplay();
	}
}
