package main.java.com.raphydaphy.automania.renderengine.load;

import main.java.com.raphydaphy.automania.models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader
{
	private List<Integer> vaos = new ArrayList<>();
	private List<Integer> vbos = new ArrayList<>();
	private List<Integer> textures = new ArrayList<>();

	public RawModel loadToModel(float[] positions, float[] uvs, float[] normals, int[] indices)
	{
		return new RawModel(loadToVAO(positions, 3, uvs,2, normals, indices), indices.length);
	}

	public int loadToVAO(float[] positions, int positionDimensions, float[] uvs, int uvDimensions, float[] normals, int[] indices)
	{
		// Vertex array used to store the buffers
		int vaoID = createVAO();

		if (indices != null)
		{
			// Put the indices into the element buffer before storing the vertexes which the indices are for
			bindIndexBuffer(indices);
		}

		// Store the vertex positions in a vertex buffer
		storeDataInAttributeList(0, positionDimensions, positions);

		if (normals != null)
		{
			// Normals are used to calculate light by telling GL what direction the triangles are facing
			storeDataInAttributeList(1, 3, normals);
		}

		if (uvs != null)
		{
			// Store texture coordinates in another vertex buffer
			storeDataInAttributeList(2, uvDimensions, uvs);
		}

		// Unbind the VAO to prevent accidental modification
		unbindVAO();
		return vaoID;
	}

	public int createVAO()
	{
		// Generate a new vertex array and save it to the array so that it can be deleted on game close
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);

		// Bind the vertex array, as gl will now automatically use it whenever vertex attrib pointers are set
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	public int loadTexture(String filename)
	{
		return loadTextureExact("src/main/resources/textures/" + filename + ".png", -0.4f);
	}

	public int loadTextureExact(String file, float bias)
	{
		Texture texture = new Texture(file);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, bias);

		// Add the texture ID to the array in order to safely delete it on shutdown and return the ID
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}


	public int storeDataInAttributeList(int attributeNumber, int dimensions, float[] data)
	{
		return storeDataInAttributeList(GL15.glGenBuffers(), attributeNumber, dimensions, data);
	}

	public int storeDataInAttributeList(int vboID, int attributeNumber, int dimensions, float[] data)
	{
		if (!vbos.contains(vboID))
		{
			vbos.add(vboID);
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

		// Put the data into a FloatBuffer and upload it to the array buffer, using STATIC_DRAW since the data won't change
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, genBuffer(data), GL15.GL_STATIC_DRAW);

		// Tell opengl that we had added data to the vertex array at the specified index (attributeNumber)
		GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);

		// Unbind the buffer since we don't need it now that we have saved the data to the vertex array (gl uses whatever vertex array that was already bound)
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		return vboID;
	}

	public void unbindVAO()
	{
		GL30.glBindVertexArray(0);
	}

	public int bindIndexBuffer(int[] indices)
	{
		return bindIndexBuffer(GL15.glGenBuffers(), indices);
	}

	public int bindIndexBuffer(int vboID, int[] indices)
	{
		if (!vbos.contains(vboID))
		{
			vbos.add(vboID);
		}

		// We will be using the element array buffer as it is used to store indices that are mapped based on the array buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);

		// Put the indices into the element array buffer, in the form of a newly generated IntBuffer
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, genBuffer(indices), GL15.GL_STATIC_DRAW);

		return vboID;
	}

	private FloatBuffer genBuffer(float[] data)
	{
		// Generate a buffer to put the array points in
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

		// Add the array to the buffer, and flip it so that it can be read back in the order it was added
		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	private IntBuffer genBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);

		buffer.put(data);
		buffer.flip();

		return buffer;
	}

	public void cleanup()
	{
		for (int texture : textures)
		{
			// Remove the texture to save memory
			GL11.glDeleteTextures(texture);
		}

		for (int vao : vaos)
		{
			// Save memory by deleting the vertex arrays on shutdown
			GL30.glDeleteVertexArrays(vao);
		}

		for (int vbo : vbos)
		{
			// Same deal for the buffers
			GL15.glDeleteBuffers(vbo);
		}
	}
}
