package com.raphydaphy.automania.render;

import com.raphydaphy.automania.math.Matrix4f;
import com.raphydaphy.automania.math.Vector2f;
import com.raphydaphy.automania.math.Vector3f;
import com.raphydaphy.automania.math.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public class ShaderProgram
{
	private final int id;

	public ShaderProgram()
	{
		id = GL20.glCreateProgram();
	}

	public void attachShader(Shader shader)
	{
		GL20.glAttachShader(id, shader.getID());
	}

	public void bindFragmentDataLocation(int number, CharSequence name)
	{
		GL30.glBindFragDataLocation(id, number, name);
	}

	public void link()
	{
		GL20.glLinkProgram(id);

		verifyStatus();
	}

	public void verifyStatus()
	{
		int status = GL20.glGetProgrami(id, GL20.GL_LINK_STATUS);
		if (status != GL11.GL_TRUE)
		{
			throw new RuntimeException(GL20.glGetProgramInfoLog(id));
		}
	}

	public int getAttributeLocation(CharSequence name)
	{
		return GL20.glGetAttribLocation(id, name);
	}

	public void enableVertexAttribute(int location)
	{
		GL20.glEnableVertexAttribArray(location);
	}

	public void disableVertexAttribute(int location)
	{
		GL20.glDisableVertexAttribArray(location);
	}

	public void pointVertexAttribute(int location, int size, int stride, int offset)
	{
		GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
	}

	public int getUniformLocation(CharSequence name)
	{
		return GL20.glGetUniformLocation(id, name);
	}

	public void setUniform(int location, int value)
	{
		GL20.glUniform1i(location, value);
	}

	public void setUniform(int location, Vector2f value)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buffer = stack.mallocFloat(2);
			value.toBuffer(buffer);
			GL20.glUniform2fv(location, buffer);
		}
	}

	public void setUniform(int location, Vector3f value)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buffer = stack.mallocFloat(3);
			value.toBuffer(buffer);
			GL20.glUniform3fv(location, buffer);
		}
	}

	public void setUniform(int location, Vector4f value)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buffer = stack.mallocFloat(4);
			value.toBuffer(buffer);
			GL20.glUniform4fv(location, buffer);
		}
	}

	public void setUniform(int location, Matrix4f value)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer buffer = stack.mallocFloat(4 * 4);
			value.toBuffer(buffer);
			GL20.glUniformMatrix4fv(location, false, buffer);
		}
	}

	public void use()
	{
		GL20.glUseProgram(id);
	}

	public void delete()
	{
		GL20.glDeleteProgram(id);
	}

}
