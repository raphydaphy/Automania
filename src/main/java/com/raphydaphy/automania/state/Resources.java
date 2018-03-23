package main.java.com.raphydaphy.automania.state;

import main.java.com.raphydaphy.automania.font.FontType;
import main.java.com.raphydaphy.automania.renderengine.load.Loader;

import java.io.File;

public class Resources
{
	public static Loader loader;
	public static FontType arial;
	public static int colors;

	public static void init()
	{
		loader = new Loader();
		arial = new FontType(Resources.loader.loadTextureExact("src/main/resources/fonts/arial.png", 0), new File("src/main/resources/fonts/arial.fnt"));
		colors = loader.loadTexture("colors");
	}
}
