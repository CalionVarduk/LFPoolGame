package LF.PoolGame.Drawable.GameObjects;

import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.DrawableObject;
import LF.PoolGame.Math.Vector2D;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.Color;

public final class SegmentBumper extends DrawableObject
{
	public static Color reboundVectorColor = MoreColors.blueViolet;
	
	private Path2D.Float _bumperRectangle;
	private Point2D.Float _startPoint;
	private Point2D.Float _endPoint;
	private Vector2D _normParallel;
	private Vector2D _normInverseParallel;
	private Vector2D _normPerpendicular;
	private int _thickness;
	
	public SegmentBumper()
	{
		super();
		_construct(0);
	}
	
	public SegmentBumper(int thickness)
	{
		super();
		_construct(thickness);
	}
	
	public SegmentBumper(int thickness, Color color)
	{
		super();
		_construct(thickness);
		setMainColor(color);
	}
	
	public void spawn(float x1, float y1, float x2, float y2)
	{
		_normParallel = Vector2D.getDelta(x2, y2, x1, y1).getNormalized();
		_normInverseParallel = _normParallel.getNegated();
		_normPerpendicular.set(-_normParallel.y, _normParallel.x);
		
		_startPoint.x = x1;
		_startPoint.y = y1;
		_endPoint.x = x2;
		_endPoint.y = y2;
		_setBumperRectangle();
		
		float minX = Math.min(x1, x2);
		float maxX = Math.max(x1, x2);
		float minY = Math.min(y1, y2);
		float maxY = Math.max(y1, y2);
		_boundingBox.setRect(minX - 3, minY - 3, maxX - minX + 6, maxY - minY + 6);
		
		startDrawing();
	}
	
	public void offsetLocationX(float x)
	{
		super.offsetLocationX(x);
		_startPoint.x += x;
		_endPoint.x += x;
		_setBumperRectangle();
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		_startPoint.y += y;
		_endPoint.y += y;
		_setBumperRectangle();
	}
	
	public void setThickness(int thickness)
	{
		_thickness = (thickness > 0) ? thickness : 0;
		_setBumperRectangle();
	}
	
	public int getThickness()
	{
		return _thickness;
	}
	
	public Vector2D getSegment()
	{
		return Vector2D.getDelta(getEnd(), getStart());
	}
	
	public float getSegmentLength()
	{
		return getSegment().getLength();
	}
	
	public Vector2D getNormParallel()
	{
		return new Vector2D(_normParallel);
	}
	
	public Vector2D getNormInverseParallel()
	{
		return new Vector2D(_normInverseParallel);
	}
	
	public Vector2D getNormPerpendicular()
	{
		return new Vector2D(_normPerpendicular);
	}
	
	public float getStartX()
	{
        return _startPoint.x;
	}
	
	public float getStartY()
	{
		return _startPoint.y;
	}
	
	public Point2D.Float getStart()
	{
		return new Point2D.Float(getStartX(), getStartY());
	}
	
	public float getEndX()
	{
		return _endPoint.x;
	}
	
	public float getEndY()
	{
		return _endPoint.y;
	}
	
	public Point2D.Float getEnd()
	{
		return new Point2D.Float(getEndX(), getEndY());
	}
	
	public void drawObject(Graphics2D g2d)
	{
		g2d.setColor(_mainColor);
		g2d.fill(_bumperRectangle);
	}
	
	public void drawVector(Graphics2D g2d)
	{
		_normPerpendicular.draw((int)(getLocationX() + 0.5f), (int)(getLocationY() + 0.5f), 20, reboundVectorColor, g2d);
	}
	
	private void _setBumperRectangle()
	{
		if(_normParallel.x != 0 || _normParallel.y != 0) {
			_bumperRectangle.reset();
			_bumperRectangle.moveTo(_startPoint.x, _startPoint.y);
			_bumperRectangle.lineTo(_endPoint.x, _endPoint.y);
			
			Vector2D negPerpendicular = _normPerpendicular.getNegated();
			negPerpendicular.scale(_thickness);
			
			_bumperRectangle.lineTo(_endPoint.x + negPerpendicular.x, _endPoint.y + negPerpendicular.y);
			_bumperRectangle.lineTo(_startPoint.x + negPerpendicular.x, _startPoint.y + negPerpendicular.y);
			_bumperRectangle.closePath();
		}
	}
	
	private void _construct(int thickness)
	{
		_normParallel = new Vector2D();
		_normInverseParallel = new Vector2D();
		_normPerpendicular = new Vector2D();
		
		_bumperRectangle = new Path2D.Float();
		_startPoint = new Point2D.Float();
		_endPoint = new Point2D.Float();
		
		setThickness(thickness);
	}
}
