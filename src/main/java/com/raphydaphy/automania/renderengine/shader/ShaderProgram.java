package main.java.com.raphydaphy.automania.renderengine.shader;

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
    protected static final int MAX_LIGHTS = 4;

    private int programID;
    private int vertexID;
    private int fragmentID;

    // 4*4 matrix that can be reused for loading matrices
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public ShaderProgram(String name)
    {
        vertexID = loadShader(name + ".vert", GL20.GL_VERTEX_SHADER);
        fragmentID = loadShader(name + ".frag", GL20.GL_FRAGMENT_SHADER);

        programID = GL20.glCreateProgram();

        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);

        bindAttributes();

        GL20.glLinkProgram(programID);

        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
        {
            System.err.println("Failed to link program: " + name);
            System.err.println(GL20.glGetProgramInfoLog(programID, 500));
            System.exit(-1);
        }

        GL20.glValidateProgram(programID);

        getAllUniformLocations();

    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName)
    {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    protected void uniformInt(int location, int value)
    {
        GL20.glUniform1i(location, value);
    }

    protected void uniformFloat(int location, float value)
    {
        GL20.glUniform1f(location, value);
    }

    protected void uniformVector2(int location, Vector2f value)
    {
        GL20.glUniform2f(location, value.x, value.y);
    }

    protected void uniformVector3(int location, Vector3f value)
    {
        GL20.glUniform3f(location, value.x, value.y, value.z);
    }

    protected void uniformBoolean(int location, boolean value)
    {
        int intValue = value == false ? 0 : 1;
        GL20.glUniform1i(location, intValue);
    }

    protected void uniformMatrix4(int location, Matrix4f value)
    {
        value.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location, false, matrixBuffer);
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

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName)
    {
        // This will add the variable to the shader, allowing you to access it using the `in` keyword in the vertex shader with the name specified here
        GL20.glBindAttribLocation(programID, attribute, variableName);
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
