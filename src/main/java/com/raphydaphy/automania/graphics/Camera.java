package main.java.com.raphydaphy.automania.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera
{
    private Matrix4f projection;
    private Vector3f position;

    public Camera(int width, int height)
    {
        position = new Vector3f(0, 0, 0);
        setProjection(width, height);
    }

    public Camera setProjection(int width, int height)
    {
        projection = new Matrix4f().ortho2D(-width / 2, width / 2, -height / 2, height / 2);
        return this;
    }

    public void move(int x, int y, int z)
    {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public Matrix4f getProjection()
    {
        return projection.translate(position, new Matrix4f());
    }

    public Vector3f getPosition()
    {
        return position;
    }
}
