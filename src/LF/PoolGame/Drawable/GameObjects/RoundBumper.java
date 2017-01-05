package LF.PoolGame.Drawable.GameObjects;

import LF.PoolGame.Drawable.DrawableObject;

import java.awt.Graphics2D;
import java.awt.Color;

public final class RoundBumper extends DrawableObject
{
	private float _radius;
	
	public RoundBumper()
	{
		super();
		setDiameter(0);
	}
	
	public RoundBumper(float diameter)
	{
		super();
		setDiameter(diameter);
	}
	
	public RoundBumper(float diameter, Color color)
	{
		super();
		setDiameter(diameter);
		setMainColor(color);
	}
	
	public void spawn(float x, float y)
	{
		setLocation(x, y);
		startDrawing();
	}
	
	public float getRadius()
	{
		return _radius;
	}
	
	public void setDiameter(float diameter)
	{
		_radius = (diameter > 0) ? diameter * 0.5f : 0;
		_boundingBox.width = _boundingBox.height = _radius * 2;
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		fillEllipse(_boundingBox, _mainColor, g2d);
	}
}
