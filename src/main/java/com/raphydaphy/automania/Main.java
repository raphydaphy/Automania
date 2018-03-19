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
		Random rand = new Random(Terrain.SEED);
		FontRenderManager.init(loader);

		FontType arial = new FontType(loader.loadTextureExact("src/main/resources/fonts/arial.png"), new File("src/main/resources/fonts/arial.fnt"));
		GUIText text = new GUIText("hello world",1, arial, new Vector2f(10, 0), 1f, true);
		text.setColour(1, 0, 1);
		int colors = loader.loadTexture("colors");

		World world = new World(loader);


		ModelData treeData = OBJLoader.loadOBJ("tree");
		RawModel treeRaw = loader.loadToModel(treeData.getVertices(), treeData.getUVS(), treeData.getNormals(), treeData.getIndices());
		Material treeMaterial = new Material(colors);
		treeMaterial.setShineDamper(5);
		treeMaterial.setReflectivity(0.1f);
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

		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());

		while (!Display.isCloseRequested())
		{
			player.move(world);
			camera.move();

			picker.update();

			if (Mouse.isButtonDown(0))
			{
				int modifyX = (int)player.data.getTransform().getPosition().x;
				int modifyY = Integer.MAX_VALUE;
				int modifyZ = (int)player.data.getTransform().getPosition().z;

				Terrain terrain = world.getChunkFromWorldCoords(modifyX, player.data.getTransform().getPosition().y, modifyZ);

				if (terrain != null && terrain.received)
				{
					for (int y = Terrain.SIZE - 2; y > 0; y--)
					{
						float density = terrain.getDensity(modifyX, y, modifyZ);
						if (density > 0.5f)
						{
							modifyY = y;
							break;
						}
					}

					if (modifyY < Float.MAX_VALUE)
					{
						int range = 5;

						for (int mx = -range; mx < +range; mx++)
						{
							for (int mz = -range; mz < +range; mz++)
							{
								float density = terrain.getDensity(modifyX + mx, modifyY, modifyZ + mz);

								float factor = Math.abs(mx) + Math.abs(mz);

								float remove = Math.max(0, 0.3f - (0.02f * factor));

								if (!terrain.setDensity(modifyX + mx, modifyY, modifyZ + mz, density - remove))
								{
									System.out.println("sadness");
								}
							}
						}

						terrain.regenerateTerrain(loader);
					}
				}
			}

			int processed = 0;
			for (Terrain terrain : world.getChunks().values())
			{
				if (terrain.received && !terrain.populated)
				{
					for (int i = 0; i < Terrain.SIZE / 8; i++)
					{
						Vector3f treePos = new Vector3f(rand.nextInt(Terrain.SIZE) + terrain.getX(),-1f, rand.nextInt(Terrain.SIZE) + terrain.getZ());
						treePos.y = terrain.getHighestVoxel(treePos.x, treePos.z) - 1;

						if (treePos.y > 0)
						{
							Transform treeTransform = new Transform(treePos, 0, rand.nextInt(360), 0, rand.nextInt(5) + 5);
							trees.add(new ModelTransform(treeTransform, treeModel));
						}
					}
					terrain.populated = true;
				}
				else if (processed < World.MAX_TERRAINS_PER_FRAME && !terrain.received && terrain.meshesUnprocessed != null)
				{
					List<TerrainMesh> meshes = new ArrayList<>();
					for (TerrainMeshData meshData : terrain.meshesUnprocessed)
					{
						meshes.add(meshData.generateMesh(loader));
					}
					terrain.setMeshes(meshes);
					terrain.received = true;
					processed++;
				}

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
