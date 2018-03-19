package main.java.com.raphydaphy.automania.renderengine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager
{
	private static int FPS_CAP = 60;

    private static int width = 1080;
    private static int height = 720;

    private static long lastFrameTime;
    private static float delta;

    public static boolean hasResized;

    public static void createDisplay(String title)
    {
        ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);

        try
        {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create(new PixelFormat(), attribs);
            Display.setTitle(title);
            Display.setResizable(true);
        } catch (LWJGLException e)
        {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, width, height);
    }

    public static void updateDisplay()
    {
    	hasResized = false;

    	if (Display.getWidth() != width || Display.getHeight() != height)
	    {
		    System.out.println("resized");
	    	width = Display.getWidth();
	    	height = Display.getHeight();
	    	GL11.glViewport(0,0,width, height);
	    	hasResized = true;
	    }

        Display.sync(FPS_CAP);
        Display.update();
        long currentTime = getCurrentTime();
        delta = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;
    }

    public static void closeDisplay()
    {
        Display.destroy();
    }

    public static float getFrameTimeSeconds()
    {
        return delta;
    }

    private static long getCurrentTime()
    {
        return Sys.getTime() * 1000 / Sys.getTimerResolution();
    }
}
