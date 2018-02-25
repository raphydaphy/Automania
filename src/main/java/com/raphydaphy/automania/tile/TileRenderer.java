package main.java.com.raphydaphy.automania.tile;

import main.java.com.raphydaphy.automania.graphics.Camera;
import main.java.com.raphydaphy.automania.graphics.Model;
import main.java.com.raphydaphy.automania.graphics.Shader;
import main.java.com.raphydaphy.automania.graphics.Texture;
import main.java.com.raphydaphy.automania.world.World;
import org.joml.Matrix4f;

public class TileRenderer<T extends Tile>
{
    private T tile;
    private Texture texture;
    private Texture texture_left;
    private Texture texture_right;
    private Texture texture_both;

    public TileRenderer(T tile)
    {
        this.tile = tile;
        if (tile.isVisible())
        {
            texture = new Texture("src//main/resources/textures/" + tile.getRegistryName() + ".png");
            texture_left = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_left.png");
            texture_right = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_right.png");
            texture_both = new Texture("src//main/resources/textures/" + tile.getRegistryName() + "_both.png");
        }
    }

    public void render(Shader shader, World world, Camera camera, Model model, Matrix4f view, int x, int y)
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
                    tex = texture_left;
                    flagLeft = true;
                }
            }

            if (right == null || !right.isVisible())
            {
                if (texture_right.isValidTexture())
                {
                    tex = texture_right;
                    flagRight = true;
                }
            }

            if (flagRight && flagLeft)
            {
                tex = texture_both;
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
