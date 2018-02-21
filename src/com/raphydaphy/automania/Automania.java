package com.raphydaphy.automania;

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

import java.nio.FloatBuffer;
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

        timer.init();

        GLFW.glfwSetKeyCallback(window, keyCallback);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();

        width = MemoryUtil.memAllocInt(1);
        height = MemoryUtil.memAllocInt(1);

        // vertex array object
        int vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            FloatBuffer vertices = stack.mallocFloat(3 * 6);
            vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
            vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
            vertices.put(0f).put(0.6f).put(0f).put(0f).put(0f).put(1f);
            vertices.flip();

            // vertex buffer object
            int vbo = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        }

        gameLoop();

        GL30.glDeleteVertexArrays(vao);

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
        float accumulator = 0f;
        float interval = 1f / TARGET_TPS;
        float alpha;

        while (!GLFW.glfwWindowShouldClose(window))
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

    public void update(float delta)
    {

    }

    public void render(float alpha)

    {
        float ratio;

        GLFW.glfwGetFramebufferSize(window, width, height);
        ratio = width.get() / (float) height.get();

        width.rewind();
        height.rewind();

        GL11.glViewport(0, 0, width.get(), height.get());
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();

        width.flip();
        height.flip();

    }

    public static boolean isGL32()
    {
        return GL.getCapabilities().OpenGL32;
    }

}
