package main.java.com.raphydaphy.automania.render;

import org.lwjgl.util.vector.Vector3f;

public class Transform
{
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    public Transform( Vector3f position, float rotX, float rotY, float rotZ, float scale)
    {
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public void move(float deltaX, float deltaY, float deltaZ)
    {
        this.position.x += deltaX;
        this.position.y += deltaY;
        this.position.z += deltaZ;
    }

    public void rotate(float deltaX, float deltaY, float deltaZ)
    {
        this.rotX += deltaX;
        this.rotY += deltaY;
        this.rotZ += deltaZ;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public float getRotX()
    {
        return rotX;
    }

    public void setRotX(float rotX)
    {
        this.rotX = rotX;
    }

    public float getRotY()
    {
        return rotY;
    }

    public void setRotY(float rotY)
    {
        this.rotY = rotY;
    }

    public float getRotZ()
    {
        return rotZ;
    }

    public void setRotZ(float rotZ)
    {
        this.rotZ = rotZ;
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }
}
