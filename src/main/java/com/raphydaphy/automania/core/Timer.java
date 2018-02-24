package main.java.com.raphydaphy.automania.core;

import org.lwjgl.glfw.GLFW;

public class Timer
{
    public int targetFPS;
    public int targetTPS;

    private double lastLoopTime;
    private float timeCount;

    private int fps;
    private int fpsCount;

    private int tps;
    private int tpsCount;

    public Timer init()
    {
        targetFPS = 60;
        targetTPS = 40;
        lastLoopTime = GLFW.glfwGetTime();

        return this;
    }

    public float getDeltaTime()
    {
        double time = getTime();
        float delta = (float) (time - lastLoopTime);
        lastLoopTime = time;
        timeCount += delta;
        return delta;
    }

    public double getTime()
    {
        return GLFW.glfwGetTime();
    }

    public void updateFPS()
    {
        fpsCount++;
    }

    public void updateTPS()
    {
        tpsCount++;
    }

    public void update()
    {
        if (timeCount > 1f)
        {
            fps = fpsCount;
            fpsCount = 0;

            tps = tpsCount;
            tpsCount = 0;

            timeCount -= 1f;
        }
    }

    public int getFPS()
    {
        return fps > 0 ? fps : fpsCount;
    }

    public int getTPS()
    {
        return tps > 0 ? tps : tpsCount;
    }

    public double getLastLoopTime()
    {
        return lastLoopTime;
    }

    public void sync()
    {
        double tempLastLoopTime = getLastLoopTime();
        double now = getTime();
        float targetTime = 1f / targetFPS;


        while (now - tempLastLoopTime < targetTime)
        {
            Thread.yield();

            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            now = getTime();
        }
    }
}
