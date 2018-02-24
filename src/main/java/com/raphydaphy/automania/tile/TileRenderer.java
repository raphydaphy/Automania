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

    public TileRenderer(T tile)
    {
        this.tile = tile;
        if (tile.isVisible())
        {
            texture = new Texture("src//main/resources/textures/" + tile.getRegistryName() + ".png");
        }
    }

    public void render(Shader shader, World world, Camera camera, Model model, Matrix4f view, int x, int y)
    {
        if (tile.isVisible())
        {
            texture.bind(0);

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
