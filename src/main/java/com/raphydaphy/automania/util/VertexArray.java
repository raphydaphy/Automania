package main.java.com.raphydaphy.automania.util;

import org.lwjgl.opengl.GL30;

public class VertexArray
{
    private int id;

    public VertexArray init()
    {
        id = GL30.glGenVertexArrays();
        return this;
    }

    public VertexArray bind()
    {
        GL30.glBindVertexArray(id);
        return this;
    }

    public VertexArray unbind()
    {
        GL30.glBindVertexArray(0);
        return this;
    }

    public VertexArray delete()
    {
        GL30.glDeleteVertexArrays(id);
        return this;
    }
}
