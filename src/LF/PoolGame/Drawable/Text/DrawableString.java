package LF.PoolGame.Drawable.Text;

import LF.PoolGame.Drawable.DrawableObject;
import LF.PoolGame.Logic.GameToolbox;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class DrawableString extends DrawableObject
{
	protected Font _font;
	protected String _string;
	
	public DrawableString()
	{
		super();
		_construct("", null);
	}
	
	public DrawableString(String string)
	{
		super();
		_construct(string, null);
	}
	
	public DrawableString(String string, Color color, Font font)
	{
		super();
		_construct(string, font);
		setMainColor(color);
	}
	
	public final void spawnLeft(float x, float y)
	{
		_boundingBox.x = x;
		_boundingBox.y = y;
		
		FontMetrics fm = GameToolbox.graphics.getFontMetrics(_font);
		Rectangle2D stringRect = fm.getStringBounds(_string, GameToolbox.graphics);
		
		_boundingBox.width = (float)stringRect.getWidth();
		_boundingBox.height = (float)stringRect.getHeight();
		
		startDrawing();
	}
	
	public final void spawnRight(float x, float y)
	{
		spawnLeft(x, y);
		_boundingBox.x -= _boundingBox.width;
	}
	
	public final void spawnCentre(float x, float y)
	{
		spawnLeft(x, y);
		_boundingBox.x -= (_boundingBox.width * 0.5f);
	}
	
	public final String getString()
	{
		return _string;
	}
	
	public final void setString(String string)
	{
		_string = string;
	}
	
	public final Font getFont()
	{
		return _font;
	}
	
	public final void setFont(Font font)
	{
		_font = font;
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		g2d.setFont(_font);
		g2d.setColor(_mainColor);
		g2d.drawString(_string, _boundingBox.x, _boundingBox.y + _boundingBox.height * 0.8f);
	}
	
	private void _construct(String string, Font font)
	{
		setString(string);
		setFont(font);
	}
}
