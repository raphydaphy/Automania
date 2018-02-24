package main.java.com.raphydaphy.automania.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture
{
    public static final String MISSING_TEXTURE = "src//main/resources/textures/missing.png";

    private int id;
    private int width;
    private int height;

    public Texture(String file)
    {
        ByteBuffer image;
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            image = STBImage.stbi_load(file, w, h, comp, 4);
            if (image == null)
            {
                System.err.println("Failed to load texture " + file);
                image = STBImage.stbi_load(MISSING_TEXTURE, w, h, comp, 4);
            }

            width = w.get();
            height = h.get();
        }

        id = GL11.glGenTextures();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
    }

    public void bind(int sampler)
    {
        if (sampler >= 0 && sampler <= 31)
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + sampler);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        }
    }

    public void delete()
    {
        GL11.glDeleteTextures(id);
    }
}
