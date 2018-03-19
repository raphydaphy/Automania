package main.java.com.raphydaphy.automania.terrain;

import main.java.com.raphydaphy.automania.entity.Player;
import main.java.com.raphydaphy.automania.render.Camera;
import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import main.java.com.raphydaphy.automania.util.MathUtils;
import main.java.com.raphydaphy.automania.util.Pos3;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;

import java.util.ArrayList;
import java.util.List;

public class InteractionManager
{
	private Vector3f currentRay;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;

	public InteractionManager(Camera camera, Matrix4f projectionMatrix)
	{
		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = MathUtils.createViewMatrix(camera);
	}

	public Vector3f getCurrentRay()
	{
		return currentRay;
	}

	public void update(World world, Player player, Loader loader, float delta)
	{
		viewMatrix = MathUtils.createViewMatrix(camera);
		currentRay = doRaycast();

		dig(world, player, loader, delta);
	}

	private float digProgress = 0;
	private float digSpeed = 0.8f;

	private void dig(World world, Player player, Loader loader, float delta)
	{
		if (Mouse.isButtonDown(0))
		{
			digProgress += delta;

			if (digProgress >= 0.25f)
			{
				digProgress = 0;
				int modifyX = (int) player.data.getTransform().getPosition().x;
				int modifyZ = (int) player.data.getTransform().getPosition().z;

				Terrain terrain = world.getChunkFromWorldCoords(modifyX, player.data.getTransform().getPosition().y, modifyZ);

				if (terrain != null && terrain.received)
				{
					modifyX = modifyX % (Terrain.SIZE - 1);
					modifyZ = modifyZ % (Terrain.SIZE - 1);

					if (modifyX < 0)
					{
						modifyX = Terrain.SIZE + modifyX;
					}
					if (modifyZ < 0)
					{
						modifyZ = Terrain.SIZE + modifyZ;
					}

					System.out.println("Terrain: " + terrain.getX() + ", " + terrain.getZ() + ". Player: " + player.data.getTransform().getPosition().x + ", " + player.data.getTransform().getPosition().z + ". Grid: " + modifyX + ", " + modifyZ);
					int modifyY = terrain.getHighestVoxelCoord(modifyX, modifyZ);

					if (modifyY < Integer.MAX_VALUE)
					{
						int range = 2;

						List<Terrain> adjChunks = new ArrayList<>();
						for (int mx = -range; mx < +range; mx++)
						{
							for (int mz = -range; mz < +range; mz++)
							{
								float density = terrain.getDensity(modifyX + mx, modifyY, modifyZ + mz);

								float factor = Math.abs(mx) + Math.abs(mz);

								float remove = Math.max(0, digSpeed - (0.02f * factor));

								if (!terrain.setDensity(modifyX + mx, modifyY, modifyZ + mz, density - remove))
								{
									Pos3 adj = new Pos3((int)terrain.getX() + modifyX + mx, modifyY, (int)terrain.getZ() + modifyZ + mz);
									Terrain terrAdj = world.getChunkFromWorldCoords(terrain.getX() + modifyX + mx, 0, terrain.getZ() + modifyZ + mz);
									if (terrAdj != null && terrAdj.received)
									{
										adj.x = adj.x % (Terrain.SIZE);
										adj.z = adj.z % (Terrain.SIZE);

										if (adj.x < 0)
										{
											adj.x = Terrain.SIZE + adj.x;
										}
										if (adj.z < 0)
										{
											adj.z = Terrain.SIZE + adj.z;
										}

										//adj.y = terrAdj.getHighestVoxelCoord(adj.x, adj.z);


										System.out.println("Got adjacent terrain at " + adj.x + ", " + adj.y + ", " + adj.z + " offset: " + mx + ", " + mz + " center: " + modifyX + ", " + modifyZ + " adj terrain: " + terrAdj.getX() + ", " + terrAdj.getZ());
										if (adj.y < Integer.MAX_VALUE)
										{
											if (terrAdj.setDensity(adj.x, adj.y, adj.z, density - remove) && !adjChunks.contains(terrAdj))
											{
												adjChunks.add(terrAdj);
											}
										}
									}
								}
							}
						}

						terrain.regenerateTerrain(loader);
						for (Terrain adjTerr : adjChunks)
						{
							adjTerr.regenerateTerrain(loader);
						}
					}
				}
			}
		}

	}

	// This does everything required to turn world coordinates into viewport coordinates in reverse
	// The resulting vector is a 3D raycast from the mouse position on the screen
	private Vector3f doRaycast()
	{
		float viewportMouseX = Mouse.getX();
		float viewportMouseY = Mouse.getY();

		Vector2f normalizedMouseCoords = normalizeMouseCoords(viewportMouseX, viewportMouseY);

		Vector4f clipSpace = new Vector4f(normalizedMouseCoords.x, normalizedMouseCoords.y, -1f, 1f);

		Vector4f eyeSpace = clipToEyeSpace(clipSpace);

		Vector3f worldSpace = eyeToWorldSpace(eyeSpace);

		return worldSpace;
	}

	// Instead of the mouse coordinates ranging from 0 to the screen width or height, this converts them from -1 to 1
	private Vector2f normalizeMouseCoords(float mouseX, float mouseY)
	{
		float x = (2f * mouseX) / Display.getWidth() - 1;
		float y = (2f * mouseY) / Display.getHeight() - 1;
		return new Vector2f(x,y);
	}

	// This will convert the position in clip-space to a vector from the viewpoint of the camera
	private Vector4f clipToEyeSpace(Vector4f clipSpace)
	{
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeSpace = Matrix4f.transform(invertedProjection, clipSpace, null);
		return new Vector4f(eyeSpace.x, eyeSpace.y, -1f, 0f);
	}

	// This converts the camera-space vector into something that shares the same coordinate system as all other objects in the world
	private Vector3f eyeToWorldSpace(Vector4f eyeSpace)
	{
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f raycast = Matrix4f.transform(invertedView, eyeSpace, null);
		Vector3f ray3D = new Vector3f(raycast.x, raycast.y, raycast.z);
		ray3D.normalise();
		return ray3D;
	}
}
