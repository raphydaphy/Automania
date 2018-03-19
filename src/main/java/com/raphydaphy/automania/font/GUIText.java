package main.java.com.raphydaphy.automania.font;

import main.java.com.raphydaphy.automania.renderengine.renderer.FontRenderManager;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Represents a piece of font in the game.
 *
 * @author Karl
 */
public class GUIText
{

	private String textString;
	private float fontSize;

	private int textMeshVao;
	private int vertexCount;
	private Vector3f colour = new Vector3f(0f, 0f, 0f);

	private Vector2f position;
	private float lineMaxSize;
	private int numberOfLines;

	private FontType font;

	private boolean centerText = false;

	/**
	 * Creates a new font, loads the font's quads into a VAO, and adds the font
	 * to the screen.
	 *
	 * @param text          - the font.
	 * @param fontSize      - the font size of the font, where a font size of 1 is the
	 *                      default size.
	 * @param font          - the font that this font should use.
	 * @param position      - the position on the screen where the top left corner of the
	 *                      font should be rendered. The top left corner of the screen is
	 *                      (0, 0) and the bottom right is (1, 1).
	 * @param maxLineLength - basically the width of the virtual page in terms of screen
	 *                      width (1 is full screen width, 0.5 is half the width of the
	 *                      screen, etc.) Text cannot go off the edge of the page, so if
	 *                      the font is longer than this length it will go onto the next
	 *                      line. When font is centered it is centered into the middle of
	 *                      the line, based on this line length value.
	 * @param centered      - whether the font should be centered or not.
	 */
	public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered)
	{
		this.textString = text;
		this.fontSize = fontSize;
		this.font = font;
		this.position = position;
		this.lineMaxSize = maxLineLength;
		this.centerText = centered;
		FontRenderManager.load(this);
	}

	/**
	 * Remove the font from the screen.
	 */
	public void remove()
	{
		FontRenderManager.remove(this, false);
	}

	/**
	 * @return The font used by this font.
	 */
	public FontType getFont()
	{
		return font;
	}

	/**
	 * Set the colour of the font.
	 *
	 * @param r - red value, between 0 and 1.
	 * @param g - green value, between 0 and 1.
	 * @param b - blue value, between 0 and 1.
	 */
	public void setColour(float r, float g, float b)
	{
		colour.set(r, g, b);
	}

	/**
	 * @return the colour of the font.
	 */
	public Vector3f getColour()
	{
		return colour;
	}

	/**
	 * @return The number of lines of font. This is determined when the font is
	 * loaded, based on the length of the font and the max line length
	 * that is set.
	 */
	public int getNumberOfLines()
	{
		return numberOfLines;
	}

	/**
	 * @return The position of the top-left corner of the font in screen-space.
	 * (0, 0) is the top left corner of the screen, (1, 1) is the bottom
	 * right.
	 */
	public Vector2f getPosition()
	{
		return position;
	}

	/**
	 * @return the ID of the font's VAO, which contains all the vertex data for
	 * the quads on which the font will be rendered.
	 */
	public int getMesh()
	{
		return textMeshVao;
	}

	/**
	 * Set the VAO and vertex count for this font.
	 *
	 * @param vao           - the VAO containing all the vertex data for the quads on
	 *                      which the font will be rendered.
	 * @param verticesCount - the total number of vertices in all of the quads.
	 */
	public void setMeshInfo(int vao, int verticesCount)
	{
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	/**
	 * @return The total number of vertices of all the font's quads.
	 */
	public int getVertexCount()
	{
		return this.vertexCount;
	}

	/**
	 * @return the font size of the font (a font size of 1 is normal).
	 */
	protected float getFontSize()
	{
		return fontSize;
	}

	/**
	 * Sets the number of lines that this font covers (method used only in
	 * loading).
	 *
	 * @param number
	 */
	protected void setNumberOfLines(int number)
	{
		this.numberOfLines = number;
	}

	/**
	 * @return {@code true} if the font should be centered.
	 */
	protected boolean isCentered()
	{
		return centerText;
	}

	/**
	 * @return The maximum length of a line of this font.
	 */
	protected float getMaxLineSize()
	{
		return lineMaxSize;
	}

	/**
	 * @return The string of font.
	 */
	protected String getTextString()
	{
		return textString;
	}

}
