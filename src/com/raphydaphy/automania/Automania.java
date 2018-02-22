package com.raphydaphy.automania;

import com.raphydaphy.automania.math.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Automania
{
	private static final int TARGET_FPS = 60;
	private static final int TARGET_TPS = 40;
	private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback()
	{
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods)
		{
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS)
			{
				GLFW.glfwSetWindowShouldClose(window, true);
			}
		}
	};
	private long window;
	private GameTimer timer;

	private IntBuffer width;
	private IntBuffer height;

	private int vao;
	private int vbo;

	private int program;

	public static boolean isGL32()
	{
		return GL.getCapabilities().OpenGL32;
	}

	public void start()
	{
		System.out.println("Ver: " + Version.getVersion());

		timer = new GameTimer();

		GLFW.glfwSetErrorCallback(errorCallback);

		if (!GLFW.glfwInit())
		{
			throw new IllegalStateException("Cannot initialize GLFW :(");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);

		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

		window = GLFW.glfwCreateWindow(640, 480, "Automania", MemoryUtil.NULL, MemoryUtil.NULL);

		// OpenGL 3.2 not supported
		if (window == MemoryUtil.NULL)
		{
			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 2);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 1);
			window = GLFW.glfwCreateWindow(640, 480, "Autonamia", MemoryUtil.NULL, MemoryUtil.NULL);
		}

		GLFW.glfwSetKeyCallback(window, keyCallback);

		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();

		timer.init();

		width = MemoryUtil.memAllocInt(1);
		height = MemoryUtil.memAllocInt(1);

		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

		try (MemoryStack stack = MemoryStack.stackPush())
		{
			FloatBuffer vertices = stack.mallocFloat(3 * 6);
			vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
			vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
			vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
			vertices.flip();

			vbo = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		}

		int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vertexShader, "#version 150 core\n" + "\n" + "in vec3 position;\n" + "in vec3 color;\n" + "\n" + "out vec3 vertexColor;\n" + "\n" + "uniform mat4 model;\n" + "uniform mat4 view;\n" + "uniform mat4 projection;\n" + "\n" + "void main() {\n" + "    vertexColor = color;\n" + "    mat4 mvp = projection * view * model;\n" + "    gl_Position = mvp * vec4(position, 1.0);\n" + "}");
		GL20.glCompileShader(vertexShader);

		if (GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE)
		{
			throw new RuntimeException(GL20.glGetShaderInfoLog(vertexShader));
		}

		int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fragmentShader, "#version 150 core\n" + "\n" + "in vec3 vertexColor;\n" + "\n" + "out vec4 fragColor;\n" + "\n" + "void main() {\n" + "    fragColor = vec4(vertexColor, 1.0);\n" + "}");
		GL20.glCompileShader(fragmentShader);

		if (GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE)
		{
			throw new RuntimeException(GL20.glGetShaderInfoLog(fragmentShader));
		}

		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		GL20.glBindAttribLocation(program, 0, "fragColor");
		GL20.glLinkProgram(program);

		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) != GL11.GL_TRUE)
		{
			throw new RuntimeException(GL20.glGetProgramInfoLog(program));
		}

		int floatSize = 4;

		int posAttrib = GL20.glGetAttribLocation(program, "position");
		GL20.glEnableVertexAttribArray(posAttrib);
		GL20.glVertexAttribPointer(posAttrib, 3, GL11.GL_FLOAT, false, 6 * floatSize, 0);

		int colAttrib = GL20.glGetAttribLocation(program, "color");
		GL20.glEnableVertexAttribArray(colAttrib);
		GL20.glVertexAttribPointer(colAttrib, 3, GL11.GL_FLOAT, false, 6 * floatSize, 3 * floatSize);

		int uniModel = GL20.glGetUniformLocation(program, "model");
		Matrix4f model = new Matrix4f();
		GL20.glUniformMatrix4fv(uniModel, false, model.getBuffer());

		int uniView =  GL20.glGetUniformLocation(program, "view");
		Matrix4f view = new Matrix4f();
		GL20.glUniformMatrix4fv(uniView, false, view.getBuffer());

		int uniProjection =  GL20.glGetUniformLocation(program, "projection");
		float ratio = 640f / 480f;
		Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
		GL20.glUniformMatrix4fv(uniProjection, false, projection.getBuffer());

		gameLoop();

		GLFW.glfwDestroyWindow(window);
		keyCallback.free();

		GLFW.glfwTerminate();

		errorCallback.free();
	}

	private void gameLoop()
	{
		float delta;
		float accumulator = 0f;
		float interval = 1f / TARGET_TPS;
		float alpha;

		boolean running = true;

		while (running)
		{
			delta = timer.getDeltaTime();
			accumulator += delta;

			// process input

			while (accumulator >= interval)
			{
				update(1f / TARGET_TPS);
				timer.updateTPS();
				accumulator -= interval;
			}


			alpha = accumulator / interval;
			render(alpha);
			timer.updateFPS();

			timer.update();

			System.out.println("FPS:" + timer.getFPS() + ", TPS: " + timer.getTPS());

			sync(TARGET_FPS);

			if (GLFW.glfwWindowShouldClose(window))
			{
				running = false;
			}

		}
	}

	private void sync(int fps)
	{
		double lastLoopTime = timer.getLastLoopTime();
		double now = timer.getTime();
		float targetTime = 1f / fps;

		while (now - lastLoopTime < targetTime)
		{
			Thread.yield();

			try
			{
				Thread.sleep(1);
			} catch (InterruptedException e)
			{
				Logger.getLogger(Automania.class.getName()).log(Level.SEVERE, null, e);
			}
			now = timer.getTime();
		}
	}

	private void update(float delta)
	{

	}

	private void render(float alpha)
	{
		/*
		float ratio;
		GLFW.glfwGetFramebufferSize(window, width, height);
		ratio = width.get() / (float) height.get();

		width.rewind();
		height.rewind();

		GL11.glViewport(0, 0, width.get(), height.get());
		*/

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
	//	GL20.glUseProgram(program);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();

		/*
		width.flip();
		height.flip();
		*/
	}

}
