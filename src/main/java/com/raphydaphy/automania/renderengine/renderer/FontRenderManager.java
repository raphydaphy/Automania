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

		int vao = loader.createVAO();

		int positionsVBO = loader.storeDataInAttributeList(0, 2, data.getVertexPositions());
		int uvsVBO = loader.storeDataInAttributeList(2, 2, data.getTextureCoords());

		loader.unbindVAO();

		text.setMeshInfo(vao, positionsVBO, uvsVBO, data.getVertexCount());
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null)
		{
			textBatch = new ArrayList<>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public static void reload(GUIText text)
	{
		FontType font = text.getFont();
		if (texts.containsKey(font))
		{
			if (texts.get(font).contains(text))
			{
				TextMeshData data = font.loadText(text);

				GL30.glBindVertexArray(text.getMeshVAO());

				loader.storeDataInAttributeList(text.getMeshPositionsVBO(), 0, 2, data.getVertexPositions());
				loader.storeDataInAttributeList(text.getMeshUvsVBO(), 2, 2, data.getTextureCoords());

				loader.unbindVAO();

				text.setVertexCount(data.getVertexCount());
			}
		}
	}

	public static void remove(GUIText text)
	{
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		if (textBatch.isEmpty())
		{
			texts.remove(texts.get(text.getFont()));
		}
	}

	public static void cleanup()
	{
		renderer.cleanup();
	}
}
