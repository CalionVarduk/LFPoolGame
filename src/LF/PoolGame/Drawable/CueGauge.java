package LF.PoolGame.Drawable;

import LF.PoolGame.Logic.GameToolbox;
import LF.PoolGame.Math.Collider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CueGauge extends DrawableObject
{
	private Color _frameColor;
	private Path2D.Float _frame;
	private Rectangle2D.Float _filling;
	private float _strength;
	
	public CueGauge()
	{
		super();
		_strength = 1;
		_frame = new Path2D.Float();
		_filling = getBoundingBox();
		setFrameColor(Color.BLACK);
		setMainColor(Color.WHITE);
	}
	
	public CueGauge(float width, float height, Color color, Color frameColor)
	{
		super(width, height);
		_strength = 1;
		_frame = new Path2D.Float();
		_filling = getBoundingBox();
		_frameColor = Color.BLACK;
		setMainColor(color);
		setFrameColor(frameColor);
	}
	
	public void spawn(float x, float y)
	{
		setLocation(x, y);
		getRectangleFrame(_frame, _boundingBox, 3);
		_strength = 1;
		_filling = getBoundingBox();
		startDrawing();
	}
	
	public void offsetLocationX(float x)
	{
		super.offsetLocationX(x);
		_filling.x += x;
		getRectangleFrame(_frame, _boundingBox, 3);
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		_filling.y += y;
		getRectangleFrame(_frame, _boundingBox, 3);
	}
	
	public Color getFrameColor()
	{
		return _frameColor;
	}
	
	public void setFrameColor(Color color)
	{
		_frameColor = color;
	}
	
	public float getCueStrength()
	{
		return _strength;
	}
	
	public void setSize(float width, float height)
	{
		_boundingBox.width = (width > 0) ? width : 0;
		_boundingBox.height = (height > 0) ? height : 0;
		getRectangleFrame(_frame, _boundingBox, 3);
	}
	
	public boolean trySetCueStrength()
	{
		if(Collider.rectanglePoint(_boundingBox, new Point2D.Float(GameToolbox.cursorX, GameToolbox.cursorY))) {
			_strength = (GameToolbox.cursorX - _boundingBox.x) / _boundingBox.width;
			_filling.width = _boundingBox.width * _strength;
			return true;
		}
		return false;
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		g2d.setColor(_mainColor);
		g2d.fill(_filling);
		g2d.setColor(_frameColor);
		g2d.fill(_frame);
		
		for(float s = 0.25f; s < 0.9f; s += 0.25f)
			_drawScaleLine(s, g2d);
	}
	
	private void _drawScaleLine(float scale, Graphics2D g2d)
	{
		int x = (int)(_boundingBox.x + (_boundingBox.width * scale) + 0.5f);
		int y1 = (int)_boundingBox.y;
		int y2 = (int)(_boundingBox.y + _boundingBox.height + 0.5f);
		g2d.drawLine(x, y1, x, y2);
	}
}
