package main.java.com.raphydaphy.automania.entity;

import main.java.com.raphydaphy.automania.graphics.*;
import main.java.com.raphydaphy.automania.tile.Tile;
import main.java.com.raphydaphy.automania.util.AABB;
import main.java.com.raphydaphy.automania.util.Transform;
import main.java.com.raphydaphy.automania.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Player
{
    private Model model;
    private Texture texture;

    private Transform transform;
    private AABB bounds;

    public Player init(Renderer renderer)
    {
        model = new Model(new float[]{-0.5f, 0.5f, 0,
                0.5f, 0.5f, 0,
                0.5f, -0.5f, 0,
                -0.5f, -0.5f, 0},
                new float[]{0, 0, 1, 0, 1, 1, 0, 1}, 0, 1, 2, 2, 3, 0);
        texture = new Texture("missing ? yes");

        transform = new Transform();
        transform.scale = new Vector3f(renderer.getScale(), renderer.getScale(), 1);

        bounds = new AABB(-0.5f, -0.5f, 0.5f, 0.5f);
        return this;
    }

    public Player render(Shader shader, Camera camera)
    {
        shader.bind();

        shader.setUniform("sampler", 0);
        shader.setUniform("projection", transform.getProjection(camera.getProjection()));

        texture.bind(0);
        model.render();

        return this;
    }

    public Transform getTransform()
    {
        return transform;
    }

    public boolean collides(World world, float moveX, float moveY)
    {
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                int posX = (int) Math.floor(transform.pos.x + moveX + x - 2);
                int posY = (int) Math.floor(transform.pos.y + moveY + y - 2);

                Tile tile = world.getTile((int) Math.floor(posX), (int) Math.floor(posY));

                if (tile != null)
                {
                    AABB bounds = tile.getBounds(world,posX, posY);

                    if (bounds != null)
                    {
                        AABB box = bounds.add(posX, posY, new AABB());
                        AABB playerBox = this.bounds.add(transform.pos.x + moveX, transform.pos.y + moveY, new AABB());

                        if (box.intersects(playerBox))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
