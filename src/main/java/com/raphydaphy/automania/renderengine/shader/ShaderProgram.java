package main.java.com.raphydaphy.automania.renderengine.shader;

import main.java.com.raphydaphy.automania.renderengine.shader.uniform.Uniform;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class ShaderProgram
{
    protected static final String EMPTY_ATTRIBUTE = "EMPTY";

    protected static final int MAX_LIGHTS = 4;

    private int programID;
    private int vertexID;
    private int fragmentID;

    // 4*4 matrix that can be reused for loading matrices
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    ShaderProgram(String name, String... attributes)
    {
        vertexID = loadShader(name + ".vert", GL20.GL_VERTEX_SHADER);
        fragmentID = loadShader(name + ".frag", GL20.GL_FRAGMENT_SHADER);

        programID = GL20.glCreateProgram();

        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);

        bindAttributes(attributes);

        GL20.glLinkProgram(programID);

        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
        {
            System.err.println("Failed to link program: " + name);
            System.err.println(GL20.glGetProgramInfoLog(programID, 500));
            System.exit(-1);
        }

    }

    void storeAllUniformLocations(Uniform... uniforms)
    {
        for (Uniform uniform : uniforms)
        {
            uniform.storeUniformLocation(programID);
        }
        GL20.glValidateProgram(programID);
    }

    public void bind()
    {
        GL20.glUseProgram(programID);
    }

    public void unbind()
    {
        GL20.glUseProgram(0);
    }

    public void cleanup()
    {
        unbind();

        GL20.glDetachShader(programID, vertexID);
        GL20.glDetachShader(programID, fragmentID);

        GL20.glDeleteShader(vertexID);
        GL20.glDeleteShader(fragmentID);

        GL20.glDeleteProgram(programID);
    }

    protected void bindAttributes(String... attributes)
    {
	    for (int attribute = 0; attribute < attributes.length; attribute++)
	    {
	        if (attributes[attribute] != EMPTY_ATTRIBUTE)
            {
                GL20.glBindAttribLocation(programID, attribute, attributes[attribute]);
            }
        }
    }

    public static int loadShader(String file, int type)
    {
        StringBuilder shaderSource = new StringBuilder();

        try(BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                shaderSource.append(line + "\n");
            }
        }
        catch (IOException e)
        {
            System.err.println("Could not read shader: " + file);
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderID = GL20.glCreateShader(type);

        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
        {
            System.err.println("Could not compile shader: " + file);
            System.err.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.exit(-1);
        }

        return shaderID;
    }
}
