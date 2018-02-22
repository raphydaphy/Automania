package com.raphydaphy.automania;

import com.raphydaphy.automania.math.Vector2f;
import com.raphydaphy.automania.render.Renderer;
import com.raphydaphy.automania.render.Texture;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
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
	private Renderer renderer;

	private int width;
	private int height;

	private Texture example;

	public static boolean isGL32()
	{
		return GL.getCapabilities().OpenGL32;
	}

	public void start()
	{
		System.out.println("Ver: " + Version.getVersion());

		timer = new GameTimer();
		renderer = new Renderer();

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

		if (window == MemoryUtil.NULL)
		{
			GLFW.glfwTerminate();
			throw new RuntimeException("Could not create the window, sorry :(");
		}

		GLFW.glfwSetKeyCallback(window, keyCallback);

		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();

		timer.init();
		renderer.init();

		int width, height;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			long window = GLFW.glfwGetCurrentContext();
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
			width = widthBuffer.get();
			height = heightBuffer.get();
		}

		example = Texture.loadTexture("resources/example.png");

		this.width = width;
		this.height = height;

		gameLoop();

		example.delete();

		renderer.dispose();

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

		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1f);

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
		renderer.clear();

		example.bind();
		renderer.begin();

		renderer.drawTextureRegion(example, 0, 0, 0, 0, width, height, Color.RED);

		renderer.end();
	}

}
