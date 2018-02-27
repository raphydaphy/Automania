package main.java.com.raphydaphy.automania.util;

import main.java.com.raphydaphy.automania.graphics.Texture;

public class TexInfo
{
	public final Texture texture;
	public final TexShape shape;

	public TexInfo(Texture tex, TexShape shape)
	{
		this.texture = tex;
		this.shape = shape;
	}


	public static enum TexShape
	{
		SQUARE32, RECT37, RECT37NOCLIP, SQUARE37, SQUARE37NOCLIP;
	}
}
