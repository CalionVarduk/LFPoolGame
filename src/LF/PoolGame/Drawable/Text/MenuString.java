package LF.PoolGame.Drawable.Text;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class MenuString extends DrawableString
{
	private boolean _isSelected;
	private Color _selectedColor;

	public MenuString()
	{
		super();
		setSelectedColor(Color.WHITE);
		deselect();
	}
	
	public MenuString(String string)
	{
		super(string);
		setSelectedColor(Color.WHITE);
		deselect();
	}
	
	public MenuString(String string, Color color, Color selectedColor, Font font)
	{
		super(string, color, font);
		setSelectedColor(selectedColor);
		deselect();
	}
	
	public final Color getSelectedColor()
	{
		return _selectedColor;
	}
	
	public final void setSelectedColor(Color color)
	{
		_selectedColor = color;
	}
	
	public final boolean isSelected()
	{
		return _isSelected;
	}
	
	public final void select()
	{
		_isSelected = true;
	}
	
	public final void deselect()
	{
		_isSelected = false;
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		if(_isSelected) {
			g2d.setFont(_font);
			g2d.setColor(_selectedColor);
			g2d.draw(_boundingBox);
			g2d.drawString(_string, _boundingBox.x, _boundingBox.y + _boundingBox.height * 0.8f);
		}
		else super.drawObject(g2d);
	}
}
