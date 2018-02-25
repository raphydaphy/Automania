package main.java.com.raphydaphy.automania.util;

import org.joml.Vector2f;

public class AABB
{
    private float x1;
    private float y1;

    private float x2;
    private float y2;

    public AABB()
    {
        this(0, 0, 0, 0);
    }

    public AABB(float x1, float y1, float x2, float y2)
    {
        this.x1 = x1;
        this.y1 = y1;

        this.x2 = x2;
        this.y2 = y2;
    }

    public AABB add(float x, float y)
    {
        return add(x, y, this);
    }

    public AABB add(float x, float y, AABB dest)
    {
        return dest.set(this.getX1() + x, this.getY1() + y, this.getX2() + x, this.getY2() + y);
    }

    public AABB set(float x1, float y1, float x2, float y2)
    {
        this.x1 = x1;
        this.y1 = y1;

        this.x2 = x2;
        this.y2 = y2;

        return this;
    }

    public boolean intersects(AABB other)
    {
        return this.getX1() < other.getX2() && this.getX2() > other.getX1() && this.getY1() < other.getY2() && this.getY2() > other.getY1();
    }

    public boolean containsPoint(float x, float y)
    {
        return this.getX1() <= x && this.getX2() >= x && this.getY1() <= y && this.getY2() >= y;
    }

    public Vector2f getCenter()
    {
        return new Vector2f(this.x1 + (this.getWidth() /  2), this.y1 + (this.getHeight() / 2));
    }

    public AABB clone()
    {
        return new AABB(x1, y1, x2, y2);
    }

    public float getX1()
    {
        return x1;
    }

    public float getY1()
    {
        return y1;
    }

    public float getX2()
    {
        return x2;
    }

    public float getY2()
    {
        return y2;
    }

    public float getWidth()
    {
        return x2 - x1;
    }

    public float getHeight()
    {
        return y2 - y1;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other == null || other.getClass() != this.getClass())
        {
            return false;
        }

        AABB otherAABB = (AABB) other;

        return otherAABB.getX1() == getX1() && otherAABB.getY1() == getY1() && otherAABB.getWidth() == getWidth() && otherAABB.getHeight() == getHeight();
    }

    @Override
    public int hashCode()
    {
        long bits = 7L;
        bits = 31L * bits + Double.doubleToLongBits(getX1());
        bits = 31L * bits + Double.doubleToLongBits(getY1());
        bits = 31L * bits + Double.doubleToLongBits(getWidth());
        bits = 31L * bits + Double.doubleToLongBits(getHeight());
        return (int) (bits ^ (bits >> 32));
    }

    @Override
    public String toString()
    {
        return "[" + getX1() + ", " + getY1() + "]  [" + getX2() + ", " + getY2() + "]";
    }

}
