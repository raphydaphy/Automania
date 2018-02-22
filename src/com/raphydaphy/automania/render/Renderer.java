package com.raphydaphy.automania.render;

import com.raphydaphy.automania.Automania;
import com.raphydaphy.automania.math.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Renderer
{
	public VertexBufferObject vbo;
	public VertexArrayObject vao;
	private ShaderProgram program;

	private FloatBuffer vertices;
	private int numVertices;
	private boolean drawing;

	public void init()
	{
		initShaders();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void clear()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private void initShaders()
	{
		if (Automania.isGL32())
		{
			vao = new VertexArrayObject();
			vao.bind();
		} else
		{
			vao = null;
		}

		vbo = new VertexBufferObject();
		vbo.bind(GL15.GL_ARRAY_BUFFER);

		vertices = MemoryUtil.memAllocFloat(4096);

		long size = vertices.capacity() * Float.BYTES;
		vbo.uploadData(GL15.GL_ARRAY_BUFFER, size, GL15.GL_DYNAMIC_DRAW);

		numVertices = 0;
		drawing = false;

		Shader vertexShader, fragmentShader;
		if (Automania.isGL32())
		{
			vertexShader = Shader.loadFromFile(GL20.GL_VERTEX_SHADER, "resources/default.vert");
			fragmentShader = Shader.loadFromFile(GL20.GL_FRAGMENT_SHADER, "resources/default.frag");
		} else
		{
			vertexShader = Shader.loadFromFile(GL20.GL_VERTEX_SHADER, "resources/legacy.vert");
			fragmentShader = Shader.loadFromFile(GL20.GL_FRAGMENT_SHADER, "resources/legacy.frag");
		}

		program = new ShaderProgram();
		program.attachShader(vertexShader);
		program.attachShader(fragmentShader);

		if (Automania.isGL32())
		{
			program.bindFragmentDataLocation(0, "fragColor");
		}

		program.link();
		program.use();

		vertexShader.delete();
		fragmentShader.delete();

		long window = GLFW.glfwGetCurrentContext();
		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);

			GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);

			width = widthBuffer.get();
			height = heightBuffer.get();
		}

		setupVertexAttributes();

		int uniTex = program.getUniformLocation("texImage");
		program.setUniform(uniTex, 0);

		// set model matrix to identity matrix
		Matrix4f model = new Matrix4f();
		int uniModel = program.getUniformLocation("model");
		program.setUniform(uniModel, model);

		// set view matrix to identity matrix
		Matrix4f view = new Matrix4f();
		int uniView = program.getUniformLocation("view");
		program.setUniform(uniView, view);

		// set projection matrix to orthographic
		Matrix4f projection = Matrix4f.orthographic(0f, width, 0f, height, -1f, 1f);
		int uniPRojection = program.getUniformLocation("projection");
		program.setUniform(uniPRojection, projection);
	}

	private void setupVertexAttributes()
	{
		// Vertex pointer used in shaders
		int posAttrib = program.getAttributeLocation("position");

		program.enableVertexAttribute(posAttrib);
		program.pointVertexAttribute(posAttrib, 2, 8 * Float.BYTES, 0);

		// Color pointer used in shaders
		int colorAttrib = program.getAttributeLocation("color");
		program.enableVertexAttribute(colorAttrib);
		program.pointVertexAttribute(colorAttrib, 4, 8 * Float.BYTES, 2 * Float.BYTES);

		// Texture pointer
		int textureAttrib = program.getAttributeLocation("texcoord");
		program.enableVertexAttribute(textureAttrib);
		program.pointVertexAttribute(textureAttrib, 2, 8 * Float.BYTES, 6 * Float.BYTES);
	}

	public void begin()
	{
		if (drawing)
		{
			throw new IllegalStateException("Tried to start a new drawing while the renderer was not finished :(");
		}
		drawing = true;
		numVertices = 0;
	}

	public void end()
	{
		if (!drawing)
		{
			throw new IllegalStateException("Tried to stop drawing when it was already stopped :(");
		}
		drawing = false;
		flush();
	}

	public void flush()
	{
		if (numVertices > 0)
		{
			vertices.flip();

			if (vao != null)
			{
				vao.bind();
			} else
			{
				vbo.bind(GL15.GL_ARRAY_BUFFER);
				setupVertexAttributes();
			}

			program.use();

			vbo.bind(GL15.GL_ARRAY_BUFFER);
			vbo.uploadSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);

			// TODO: support multiple drawning modes
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numVertices);

			vertices.clear();
			numVertices = 0;
		}
	}

	public void dispose()
	{
		MemoryUtil.memFree(vertices);

		if (vao != null)
		{
			vao.delete();
		}

		vbo.delete();
		program.delete();

		// Dispose font
		// Dispose debug font
	}

	public void drawTexture(Texture texture, float x, float y) {
		drawTexture(texture, x, y, Color.WHITE);
	}

	public void drawTexture(Texture texture, float x, float y, Color c) {
		/* Vertex positions */
		float x1 = x;
		float y1 = y;
		float x2 = x1 + texture.getWidth();
		float y2 = y1 + texture.getHeight();

		/* Texture coordinates */
		float s1 = 0f;
		float t1 = 0f;
		float s2 = 1f;
		float t2 = 1f;

		drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
	}

	public void drawTextureRegion(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight) {
		drawTextureRegion(texture, x, y, regX, regY, regWidth, regHeight, Color.WHITE);
	}

	public void drawTextureRegion(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight, Color c) {
		/* Vertex positions */
		float x1 = x;
		float y1 = y;
		float x2 = x + regWidth;
		float y2 = y + regHeight;

		/* Texture coordinates */
		float s1 = regX / texture.getWidth();
		float t1 = regY / texture.getHeight();
		float s2 = (regX + regWidth) / texture.getWidth();
		float t2 = (regY + regHeight) / texture.getHeight();

		drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
	}

	public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2) {
		drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, Color.WHITE);
	}

	public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
		if (vertices.remaining() < 7 * 6) {
			/* We need more space in the buffer, so flush it */
			flush();
		}

		float r = c.getRed();
		float g = c.getGreen();
		float b = c.getBlue();
		float a = c.getAlpha();

		vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
		vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
		vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);

		vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
		vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
		vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);

		numVertices += 6;
	}

	public Renderer add(float pos)
	{
		if (vertices.remaining() < 1)
		{
			flush();
		}

		vertices.put(pos);
		numVertices++;
		return this;
	}
}
