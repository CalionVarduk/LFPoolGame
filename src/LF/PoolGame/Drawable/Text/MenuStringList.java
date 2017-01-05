package LF.PoolGame.Drawable.Text;

import LF.PoolGame.Drawable.DrawableObject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class MenuStringList extends DrawableObject
{
	private float _spacing;
	private Font _font;
	private ArrayList<MenuString> _strings;

	public MenuStringList()
	{
		super();
		_strings = new ArrayList<MenuString>();
		setSpacing(0);
		setFont(null);
	}
	
	public MenuStringList(float spacing, Font font)
	{
		super();
		_strings = new ArrayList<MenuString>();
		setSpacing(spacing);
		setFont(font);
	}
	
	public void spawnCentre(float x, float y)
	{
		for(int i = 0; i < _strings.size(); ++i) {
			_strings.get(i).spawnCentre(x, y);
			y += (_strings.get(i).getHeight() + _spacing);
		}
		_strings.trimToSize();
		fixBoundingBox();
		startDrawing();
	}
	
	public void spawnLeft(float x, float y)
	{
		for(int i = 0; i < _strings.size(); ++i) {
			_strings.get(i).spawnLeft(x, y);
			y += (_strings.get(i).getHeight() + _spacing);
		}
		_strings.trimToSize();
		fixBoundingBox();
		startDrawing();
	}
	
	public void spawnRight(float x, float y)
	{
		for(int i = 0; i < _strings.size(); ++i) {
			_strings.get(i).spawnRight(x, y);
			y += (_strings.get(i).getHeight() + _spacing);
		}
		_strings.trimToSize();
		fixBoundingBox();
		startDrawing();
	}
	
	public void offsetLocationX(float x)
	{
		super.offsetLocationX(x);
		for(int i = 0; i < _strings.size(); ++i)
			_strings.get(i).offsetLocationX(x);
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		for(int i = 0; i < _strings.size(); ++i)
			_strings.get(i).offsetLocationY(y);
	}
	
	public float getSpacing()
	{
		return _spacing;
	}
	
	public void setSpacing(float spacing)
	{
		float delta = spacing - _spacing;
		_spacing = spacing;
		for(int i = 1; i < _strings.size(); ++i)
			_strings.get(i).offsetLocationY(delta * i);
	}
	
	public Font getFont()
	{
		return _font;
	}
	
	public void setFont(Font font)
	{
		_font = font;
		for(int i = 0; i < _strings.size(); ++i)
			_strings.get(i).setFont(font);
	}
	
	public int getSize()
	{
		return _strings.size();
	}
	
	public void selectAll()
	{
		for(int i = 0; i < _strings.size(); ++i)
			_strings.get(i).select();
	}
	
	public void select(int i)
	{
		if(i >= 0 && i < _strings.size())
			_strings.get(i).select();
	}
	
	public void deselectAll()
	{
		for(int i = 0; i < _strings.size(); ++i)
			_strings.get(i).deselect();
	}
	
	public void deselect(int i)
	{
		if(i >= 0 && i < _strings.size())
			_strings.get(i).deselect();
	}
	
	public boolean isSelected(int i)
	{
		return (i >= 0 && i < _strings.size()) ? _strings.get(i).isSelected() : false;
	}
	
	public int getFirstSelected()
	{
		for(int i = 0; i < _strings.size(); ++i)
			if(_strings.get(i).isSelected()) return i;
		return -1;
	}
	
	public int selectNext()
	{
		int selected = getFirstSelected();
		if(selected != -1) {
			_strings.get(selected++).deselect();
			if(selected == _strings.size()) selected = 0;
			_strings.get(selected).select();
		}
		return selected;
	}
	
	public int selectPrevious()
	{
		int selected = getFirstSelected();
		if(selected != -1) {
			_strings.get(selected--).deselect();
			if(selected == -1) selected = _strings.size() - 1;
			_strings.get(selected).select();
		}
		return selected;
	}
	
	public MenuString addMenuString(String string, Color color, Color selectedColor)
	{
		_strings.add(new MenuString(string, color, selectedColor, _font));
		return _strings.get(_strings.size() - 1);
	}
	
	public MenuString addMenuString(MenuString string)
	{
		_strings.add(string);
		return _strings.get(_strings.size() - 1);
	}
	
	public MenuString getMenuString(int i)
	{
		return (i >= 0 && i < _strings.size()) ? _strings.get(i) : null;
	}
	
	public void fixBoundingBox()
	{		
		if(_strings.size() > 0) {
			MenuString current = _strings.get(0);
			float minX = current.getLeft();
			float maxX = current.getRight();
			float minY = current.getTop();
			float maxY = current.getBottom();
			
			for(int i = 1; i < _strings.size(); ++i) {
				current = _strings.get(i);
				if(minX > current.getLeft()) minX = current.getLeft();
				if(maxX < current.getRight()) maxX = current.getRight();
				if(minY > current.getTop()) minY = current.getTop();
				if(maxY < current.getBottom()) maxY = current.getBottom();
			}
			
			_boundingBox.x = minX;
			_boundingBox.y = minY;
			_boundingBox.width = maxX - minX;
			_boundingBox.height = maxY - minY;
		}
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		for(int i = 0; i < _strings.size(); ++i)
			_strings.get(i).draw(g2d);
	}
}
