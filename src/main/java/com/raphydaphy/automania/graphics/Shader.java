package main.java.com.raphydaphy.automania.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.io.*;
import java.nio.FloatBuffer;

public class Shader
{
    private int program;

    private int vertex;
    private int fragment;

    public Shader(String name)
    {
        program = GL20.glCreateProgram();

        vertex = makeShader(GL20.GL_VERTEX_SHADER, name);
        fragment = makeShader(GL20.GL_FRAGMENT_SHADER, name);

        GL20.glAttachShader(program, vertex);
        GL20.glAttachShader(program, fragment);

        GL20.glBindAttribLocation(program, 0, "vertices");
        GL20.glBindAttribLocation(program, 1, "textures");

        GL20.glLinkProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != 1)
        {
            System.err.println("Failed to link shader program " + name + ":\n" + GL20.glGetProgramInfoLog(program));
            System.exit(1);
        }

        GL20.glValidateProgram(program);

        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) != 1)
        {
            System.err.print("Failed to validate shader program " + name + "\n" + GL20.glGetProgramInfoLog(program));
            System.exit(1);
        }
    }

    public int makeShader(int type, String name)
    {
        int id = GL20.glCreateShader(type);

        StringBuilder src = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("src//main/resources/shaders/" + name + (type == GL20.GL_VERTEX_SHADER ? ".vert" : ".frag")))))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                src.append(line + "\n");
            }
        } catch (IOException e)
        {
            System.err.println("The shader " + name + " could not be found:");
            e.printStackTrace();
        }

        GL20.glShaderSource(id, src.toString());
        GL20.glCompileShader(id);

        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) != 1)
        {
            System.err.println("Failed to compile shader " + name + ":\n" + GL20.glGetShaderInfoLog(id));
            System.exit(1);
        }

        return id;
    }

    public void setUniform(String name, int value)
    {
        int location = GL20.glGetUniformLocation(program, name);
        if (location != -1)
        {
            GL20.glUniform1i(location, value);
        }
    }

    public void setUniform(String name, Matrix4f matrix)
    {
        int location = GL20.glGetUniformLocation(program, name);
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        matrix.get(buf);
        if (location != -1)
        {
            GL20.glUniformMatrix4fv(location, false, buf);
        }
    }

    public void bind()
    {
        GL20.glUseProgram(program);
    }

    public void delete()
    {
        GL20.glDetachShader(program, vertex);
        GL20.glDetachShader(program, fragment);

        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);

        GL20.glDeleteProgram(program);
    }
}
