package com.raphydaphy.automania.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.*;

public class Shader
{
    private final int id;

    public Shader(int type)
    {
        id = GL20.glCreateShader(type);
    }

    public void setSource(CharSequence source)
    {
        GL20.glShaderSource(id, source);
    }

    public void compile()
    {
        GL20.glCompileShader(id);

        verifyStatus();
    }

    private void verifyStatus()
    {
        int status = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);

        if (status != GL11.GL_TRUE)
        {
            throw new RuntimeException(GL20.glGetShaderInfoLog(id));
        }
    }

    public void delete()
    {
        GL20.glDeleteShader(id);
    }

    public int getID()
    {
        return id;
    }

    public static Shader createShader(int type, CharSequence source)
    {
        Shader shader = new Shader(type);
        shader.setSource(source);
        shader.compile();

        return shader;
    }

    public static Shader loadFromFile(int type, String path)
    {
        StringBuilder builder = new StringBuilder();

        try (InputStream in = new FileInputStream(path))
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line).append("\n");
            }
        } catch (IOException e)
        {
            throw new RuntimeException("Could not load shader file :(" + System.lineSeparator() + e.getMessage());
        }

        CharSequence source = builder.toString();

        return createShader(type, source);
    }
}
