package com.raphydaphy.automania.render;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class Texture
{
	private final int id;

	private final int width;
	private final int height;

	public Texture(int width, int height, ByteBuffer data)
	{
		id = GL11.glGenTextures();
		this.width = width;
		this.height = height;

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		MemoryUtil.memFree(data);
	}

	public static Texture loadTexture(String path)
	{
		ByteBuffer image;
		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			/* Prepare image buffers */
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			/* Load image */
			STBImage.stbi_set_flip_vertically_on_load(true);
			image = STBImage.stbi_load(path, w, h, comp, 4);
			if (image == null)
			{
				throw new RuntimeException("Failed to load a texture file!" + System.lineSeparator() + STBImage.stbi_failure_reason());
			}

			/* Get width and height of image */
			width = w.get();
			height = h.get();
		}

		return new Texture(width, height, image);
	}

	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	public void delete()
	{
		GL11.glDeleteTextures(id);
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

}