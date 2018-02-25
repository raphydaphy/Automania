package main.java.com.raphydaphy.automania.util;

public class AABB
{
    private int x1;
    private int y1;

    private int x2;
    private int y2;

    public AABB(int x1, int y1, int x2, int y2)
    {
        this.x1 = x1;
        this.y1 = y1;

        this.x2 = x2;
        this.y2 = y2;
    }

    public AABB add(int x, int y)
    {
        return add(x, y, this);
    }

    public AABB add(int x, int y, AABB dest)
    {
        return dest.set(dest.getX1() + x, dest.getY1() + y, dest.getX2() + x, dest.getY2() + y);
    }

    public AABB set(int x1, int y1, int x2, int y2)
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

    public boolean containsPoint(int x, int y)
    {
        return this.getX1() <= x && this.getX2() >= x && this.getY1() <= y && this.getY2() >= y;
    }

    public AABB clone()
    {
        return new AABB(x1, y1, x2, y2);
    }

    public int getX1()
    {
        return x1;
    }

    public int getY1()
    {
        return y1;
    }

    public int getX2()
    {
        return x2;
    }

    public int getY2()
    {
        return y2;
    }

    public int getWidth()
    {
        return x2 - x1;
    }

    public int getHeight()
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

}
