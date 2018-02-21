package com.raphydaphy.automania;

import org.lwjgl.glfw.GLFW;

public class GameTimer
{
	private double lastLoopTime;
	private float timeCount;

	private int fps;
	private int fpsCount;

	private int tps;
	private int tpsCount;

	public void init()
	{
		lastLoopTime = GLFW.glfwGetTime();
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
}
