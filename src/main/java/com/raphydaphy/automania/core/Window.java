package main.java.com.raphydaphy.automania.core;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class Window
{
    private long window;

    private int width, height;

    private boolean fullscreen;
    private boolean hasResized;

    public Window()
    {
        setSize(1080, 720);
        setFullscreen(false);
        hasResized = false;
    }

    public Window init(String name)
    {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, 1);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, 1);

        window = GLFW.glfwCreateWindow(getWidth(), getHeight(), name, fullscreen ? GLFW.glfwGetPrimaryMonitor() : 0, 0);

        if (window == 0)
        {
            throw new RuntimeException("Failed to initiate the window");
        }

        if (!fullscreen)
        {
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(
                    window,
                    (vidMode.width() - getWidth()) / 2,
                    (vidMode.height() - getHeight()) / 2
            );

            GLFW.glfwShowWindow(window);
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);

        setCallbacks();

        return this;
    }

    private Window setCallbacks()
    {
        GLFW.glfwSetWindowSizeCallback(window, (argWindow, argWidth, argHeight) ->
        {
            if (window == argWindow)
            {
                width = argWidth;
                height = argHeight;
                hasResized = true;
            }
        });

        GLFW.glfwSetKeyCallback(window, (argWindow, argKey, argCode, argAction, argMods) ->
        {
            if (window == argWindow && argAction == GLFW.GLFW_RELEASE)
            {
                if (argKey == GLFW.GLFW_KEY_ESCAPE)
                {
                    GLFW.glfwSetWindowShouldClose(window, true);
                }
            }
        });

        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) ->
        {
            if (action == GLFW.GLFW_RELEASE)
            {

            }
        });

        return this;
    }

    public Window destroy()
    {
        System.out.println("ded son");
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        return this;
    }

    public void update()
    {
        hasResized = false;
        GLFW.glfwPollEvents();
    }

    public void swapBuffers()
    {
        GLFW.glfwSwapBuffers(window);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public boolean hasResized()
    {
        return hasResized;
    }

    public boolean shouldClose()
    {
        return GLFW.glfwWindowShouldClose(window);
    }

    public boolean isKeyDown(int key)
    {
        return GLFW.glfwGetKey(window, key) == 1;
    }

    public void setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public void setFullscreen(boolean fullscreen)
    {
        this.fullscreen = fullscreen;
    }
}
