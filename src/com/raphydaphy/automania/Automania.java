package com.raphydaphy.automania;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Automania
{
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

    public static final int TARGET_FPS = 60;
    public static final int TARGET_TPS = 40;

    private GameTimer timer;

    private IntBuffer width;
    private IntBuffer height;

    public void start()
    {
        System.out.println("Ver: " + Version.getVersion());

        timer = new GameTimer();

        GLFW.glfwSetErrorCallback(errorCallback);

        if (!GLFW.glfwInit())
        {
            throw new IllegalStateException("Cannot initialize GLFW :(");
        }

        window = GLFW.glfwCreateWindow(640, 480, "automania", MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL)
        {
            GLFW.glfwTerminate();
            throw new RuntimeException("Could not create the window, sorry :(");
        }

        timer.init();

        GLFW.glfwSetKeyCallback(window, keyCallback);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();


        width = MemoryUtil.memAllocInt(1);
        height = MemoryUtil.memAllocInt(1);

        gameLoop();

        MemoryUtil.memFree(width);
        MemoryUtil.memFree(height);

        GLFW.glfwDestroyWindow(window);
        keyCallback.free();

        GLFW.glfwTerminate();
        errorCallback.free();
    }

    public void gameLoop()
    {
        float delta;

        while (!GLFW.glfwWindowShouldClose(window))
        {
            delta = timer.getDeltaTime();

            update(delta);
            timer.updateTPS();

            render();
            timer.updateFPS();

            timer.update();

            System.out.println("FPS:" + timer.getFPS() + ", TPS: " + timer.getTPS());

            sync(TARGET_FPS);
        }
    }

    public void sync(int fps)
    {
        double lastLoopTime = timer.getLastLoopTime();
        double now = timer.getTime();
        float targetTime = 1f / fps;

        while (now - lastLoopTime < targetTime)
        {
            Thread.yield();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                Logger.getLogger(Automania.class.getName()).log(Level.SEVERE, null, e);
            }
            now = timer.getTime();
        }
    }

    public void update(float deltaTime)
    {

    }

    public void render()

    {
        float ratio;

        GLFW.glfwGetFramebufferSize(window, width, height);
        ratio = width.get() / (float) height.get();

        width.rewind();
        height.rewind();

        GL11.glViewport(0, 0, width.get(), height.get());
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-ratio, ratio, -1f, 1f, 1f, -1f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glLoadIdentity();
        GL11.glRotatef((float) GLFW.glfwGetTime() * 50f, 0f, 0f, 1f);

        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glColor3f(0f, 1f, 0f);
        GL11.glVertex3f(-6f, -4f, 0f);
        GL11.glColor3f(0f, 1f, 0f);
        GL11.glVertex3f(0.6f, -0.4f, 0f);
        GL11.glColor3f(0f, 0f, 1f);
        GL11.glVertex3f(0f, 0.6f, 0f);
        GL11.glEnd();

        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        width.flip();
        height.flip();
    }

}
