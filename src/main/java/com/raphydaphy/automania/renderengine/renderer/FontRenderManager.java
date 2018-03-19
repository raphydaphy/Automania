package main.java.com.raphydaphy.automania.renderengine.renderer;

import main.java.com.raphydaphy.automania.font.FontType;
import main.java.com.raphydaphy.automania.font.GUIText;
import main.java.com.raphydaphy.automania.font.TextMeshData;
import main.java.com.raphydaphy.automania.renderengine.load.Loader;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontRenderManager
{
	private static Loader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<>();
	private static FontRenderer renderer;

	public static void init(Loader loaderIn)
	{
		renderer = new FontRenderer();
		loader = loaderIn;
	}

	public static void render()
	{
		renderer.render(texts);
	}

	public static void load(GUIText text)
	{
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		int vao = loader.loadToVAO(data.getVertexPositions(), 2, data.getTextureCoords(), 2,null, null);
		text.setMeshInfo(vao, data.getVertexCount());
		List<GUIText> batch = texts.get(font);
		if (batch == null)
		{
			batch = new ArrayList<>();
			texts.put(font, batch);
		}
		batch.add(text);
	}

	public static void remove(GUIText text, boolean destroy)
	{
		if (texts.containsKey(text.getFont()))
		{
			List<GUIText> batch = texts.get(text.getFont());
			batch.remove(text);
			if (batch.isEmpty())
			{
				texts.remove(batch);
			}
		}

		if (destroy)
		{
			GL30.glDeleteVertexArrays(text.getMesh());
		}
	}

	public static void cleanup()
	{
		renderer.cleanup();
	}
}
