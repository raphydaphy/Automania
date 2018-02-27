package main.java.com.raphydaphy.automania.tile;

import main.java.com.raphydaphy.automania.graphics.Camera;
import main.java.com.raphydaphy.automania.graphics.Model;
import main.java.com.raphydaphy.automania.graphics.Shader;
import main.java.com.raphydaphy.automania.graphics.Texture;
import main.java.com.raphydaphy.automania.util.TexInfo;
import main.java.com.raphydaphy.automania.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;import java.util.Map;

public class TileRenderer<T extends Tile>
{
	private T tile;
	private Texture texture;
	private Texture texture_left;
	private Texture texture_right;
	private Texture texture_both;
	private Texture texture_top_left;
	private Texture texture_top_right;
	private Texture texture_top_both;
	private Texture texture_bottom;
	private Texture texture_bottom_left;

	public TileRenderer(T tile)
	{
		this.tile = tile;
		if (tile.isVisible())
		{
			texture = new Texture("src//main/resources/textures/" + tile.getRegistryName() + ".png");
			texture_left = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_left.png");
			texture_right = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_right.png");
			texture_both = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_both.png");
			texture_top_left = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_top_left.png");
			texture_top_right = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_top_right.png");
			texture_top_both = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_top_both.png");
			texture_bottom = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_bottom.png");
			texture_bottom_left = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_bottom_left.png");
		}
	}

	public void render(Shader shader, World world, Camera camera, Model model, Matrix4f view, Map<Vector2f, TexInfo> extras, int x, int y)
	{
		if (tile.isVisible())
		{
			Texture tex = texture;

			Tile left = world.getTile(x - 1, y);
			Tile right = world.getTile(x + 1, y);

			boolean flagLeft = false;
			boolean flagRight = false;
			if (left == null || !left.isVisible())
			{
				if (texture_left.isValidTexture())
				{
					extras.put(new Vector2f(x, y - 1), new TexInfo(texture_top_left, TexInfo.TexShape.SQUARE32));
					extras.put(new Vector2f(x, y + (1 / 32f) * 5), new TexInfo(texture_bottom_left, (right != null && right.isVisible()) ? TexInfo.TexShape.SQUARE37NOCLIP : TexInfo.TexShape.SQUARE37));
					tex = texture_left;
					flagLeft = true;
				}
			}

			if (right == null || !right.isVisible())
			{
				if (texture_right.isValidTexture())
				{
					extras.put(new Vector2f(x, y - 1), new TexInfo(texture_top_right, TexInfo.TexShape.SQUARE32));
					tex = texture_right;
					flagRight = true;
				}
			}

			if (flagRight && flagLeft)
			{
				extras.put(new Vector2f(x, y - 1), new TexInfo(texture_top_both, TexInfo.TexShape.SQUARE32));
				tex = texture_both;
			} else if (!flagLeft && !flagRight && texture_bottom.isValidTexture())
			{
				TexInfo.TexShape shape = TexInfo.TexShape.RECT37;
				Tile rightUp = world.getTile(x + 1, y + 1);
				if (rightUp != null && rightUp.isVisible())
				{
					shape = TexInfo.TexShape.RECT37NOCLIP;
				}
				extras.put(new Vector2f(x, y + 1), new TexInfo(texture_bottom, shape));
			}

			tex.bind(0);

			Matrix4f tile_pos = new Matrix4f().translate(x, y, 0);
			Matrix4f target = new Matrix4f();

			camera.getProjection().mul(view, target);
			target.mul(tile_pos);

			shader.setUniform("sampler", 0);
			shader.setUniform("projection", target);

			model.render();
		}
	}

	public void delete()
	{
		if (texture != null)
		{
			texture.delete();
		}
	}
}
