package main.java.com.raphydaphy.automania;

import main.java.com.raphydaphy.automania.core.Game;
import main.java.com.raphydaphy.automania.core.Timer;
import main.java.com.raphydaphy.automania.core.Window;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Automania
{
    private static Automania INSTANCE;

    private Window window;
    private Timer timer;

    private Game game;

    public static void main(String[] args)
    {
        INSTANCE = new Automania();
        INSTANCE.run();
    }

    public void run()
    {
        init();
        loop();
        cleanup();
    }

    public void init()
    {
        System.out.println("Starting Automania with LWJGL " + Version.getVersion());

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())
        {
            throw new IllegalStateException("Failed to initiate GLFW");
        }

        window = new Window().init("Automania");
        timer = new Timer().init();

        GL.createCapabilities();

        game = new Game().init(window);
    }

    public void loop()
    {
        float delta;
        float accumulator = 0f;
        float interval = 1f / timer.targetFPS;
        float alpha;

        GL11.glClearColor(0, 0, 1, 1);

        while (!window.shouldClose())
        {
            delta = timer.getDeltaTime();
            accumulator += delta;

            while (accumulator >= interval)
            {
                update(1f / timer.targetTPS);
                timer.updateTPS();
                accumulator -= interval;
            }

            alpha = accumulator / interval;
            render(alpha);
            timer.updateFPS();

            timer.update();

            timer.sync();
        }
    }

    public void update(float delta)
    {
        game.update(window, delta);
    }

    public void render(float alpha)
    {
        window.update();
        game.render(alpha);
        window.swapBuffers();
    }

    public void cleanup()
    {
        game.cleanup();
        window.destroy();

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public Game getGame()
    {
        return game;
    }

    public static Automania getInstance()
    {
        return INSTANCE;
    }
}
