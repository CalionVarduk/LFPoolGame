package LF.PoolGame.Drawable.Text;

import LF.PoolGame.Drawable.DrawableObject;
import LF.PoolGame.Math.Vector2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public final class MessageBox extends DrawableObject
{
	private Vector2D _padding;
	private Color _foreColor;
	private MenuStringList _strings;
	
	public MessageBox()
	{
		super();
		_construct(0, 0, 0, Color.WHITE, Color.BLACK, null);
	}
	
	public MessageBox(float paddingX, float paddingY, float spacing, Color color, Color foreColor, Font font)
	{
		super();
		_construct(paddingX, paddingY, spacing, color, foreColor, font);
	}
	
	public void spawn(float x, float y)
	{
		_strings.spawnCentre(x, y);
		_fixBoundingBox();
		startDrawing();
	}
	
	public MenuString addString(MenuString string)
	{
		return _strings.addMenuString(string);
	}
	
	public MenuString addString(String string)
	{
		return _strings.addMenuString(string, _foreColor, _foreColor);
	}
	
	public int getStringCount()
	{
		return _strings.getSize();
	}
	
	public void offsetLocationX(float x)
	{
		super.offsetLocationX(x);
		_strings.offsetLocationX(x);
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		_strings.offsetLocationY(y);
	}
	
	public Color getForeColor()
	{
		return _foreColor;
	}
	
	public void setForeColor(Color color)
	{
		_foreColor = color;
		for(int i = 0; i < _strings.getSize(); ++i) {
			_strings.getMenuString(i).setMainColor(color);
			_strings.getMenuString(i).setSelectedColor(color);
		}
	}
	
	public Font getFont()
	{
		return _strings.getFont();
	}
	
	public void setFont(Font font)
	{
		_strings.setFont(font);
	}
	
	public float getSpacing()
	{
		return _strings.getSpacing();
	}
	
	public void setSpacing(float spacing)
	{
		_strings.setSpacing(spacing);
	}
	
	public MenuString getBoxString(int i)
	{
		return _strings.getMenuString(i);
	}
	
	public float getPaddingX()
	{
		return _padding.x;
	}
	
	public float getPaddingY()
	{
		return _padding.y;
	}
	
	public Vector2D getPadding()
	{
		return new Vector2D(_padding);
	}
	
	public void setPaddingX(float x)
	{
		_padding.x = (x > 0) ? x : 0;
	}
	
	public void setPaddingY(float y)
	{
		_padding.y = (y > 0) ? y : 0;
	}
	
	public void setPadding(float x, float y)
	{
		setPaddingX(x);
		setPaddingY(y);
	}

	public void setPadding(Vector2D padding)
	{
		setPaddingX(padding.x);
		setPaddingY(padding.y);
	}
	
	public void fixBoxSize()
	{
		_strings.fixBoundingBox();
		_fixBoundingBox();
	}
	
	public void drawObject(Graphics2D g2d)
	{
		g2d.setColor(_mainColor);
		g2d.fill(_boundingBox);
		_strings.draw(g2d);
	}
	
	private void _fixBoundingBox()
	{
		_boundingBox.x = _strings.getLeft() - (_padding.x * 0.5f);
		_boundingBox.y = _strings.getTop() - (_padding.y * 0.5f);
		_boundingBox.width = _strings.getWidth() + _padding.x;
		_boundingBox.height = _strings.getHeight() + _padding.y;
	}
	
	private void _construct(float paddingX, float paddingY, float spacing, Color color, Color foreColor, Font font)
	{
		_padding = new Vector2D();
		_strings = new MenuStringList(spacing, font);
		setPadding(paddingX, paddingY);
		setMainColor(color);
		setForeColor(foreColor);
	}
}
