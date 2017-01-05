package LF.PoolGame.Drawable;

import LF.PoolGame.Logic.GameToolbox;
import LF.PoolGame.Math.Collider;
import LF.PoolGame.Math.Vector2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public abstract class DrawableObject
{
	public static Color boundingBoxColor = Color.WHITE;
	
	private boolean _isDrawn;
	protected Color _mainColor;
	protected Rectangle2D.Float _boundingBox;
	
	public static void drawRectangle(RectangularShape shape, Color color, Graphics2D g2d)
	{
		Rectangle2D.Float rect = new Rectangle2D.Float((float)shape.getX(), (float)shape.getY(), (float)shape.getWidth(), (float)shape.getHeight());
		g2d.setColor(color);
		g2d.draw(rect);
	}
	
	public static void fillRectangle(RectangularShape shape, Color color, Graphics2D g2d)
	{
		Rectangle2D.Float rect = new Rectangle2D.Float((float)shape.getX(), (float)shape.getY(), (float)shape.getWidth(), (float)shape.getHeight());
		g2d.setColor(color);
		g2d.fill(rect);
	}
	
	public static void drawEllipse(RectangularShape shape, Color color, Graphics2D g2d)
	{
		Ellipse2D.Float ellipse = new Ellipse2D.Float((float)shape.getX(), (float)shape.getY(), (float)shape.getWidth(), (float)shape.getHeight());
		g2d.setColor(color);
		g2d.draw(ellipse);
	}
	
	public static void fillEllipse(RectangularShape shape, Color color, Graphics2D g2d)
	{
		Ellipse2D.Float ellipse = new Ellipse2D.Float((float)shape.getX(), (float)shape.getY(), (float)shape.getWidth(), (float)shape.getHeight());
		g2d.setColor(color);
		g2d.fill(ellipse);
	}
	
	public static void getRectangleFrame(Path2D.Float out_frame, RectangularShape rect, float thickness)
	{
		out_frame.reset();
		if(thickness > 0) {
			float halfThickness = thickness * 0.5f;
			
			out_frame.moveTo(rect.getMinX() + halfThickness, rect.getMinY() + halfThickness);
			out_frame.lineTo(rect.getMaxX() - halfThickness, rect.getMinY() + halfThickness);
			out_frame.lineTo(rect.getMaxX() - halfThickness, rect.getMaxY() - halfThickness);
			out_frame.lineTo(rect.getMinX() + halfThickness, rect.getMaxY() - halfThickness);
			out_frame.lineTo(rect.getMinX() + halfThickness, rect.getMinY() + halfThickness);
			out_frame.lineTo(rect.getMinX() - halfThickness, rect.getMinY() + halfThickness);
			out_frame.lineTo(rect.getMinX() - halfThickness, rect.getMaxY() + halfThickness);
			out_frame.lineTo(rect.getMaxX() + halfThickness, rect.getMaxY() + halfThickness);
			out_frame.lineTo(rect.getMaxX() + halfThickness, rect.getMinY() - halfThickness);
			out_frame.lineTo(rect.getMinX() - halfThickness, rect.getMinY() - halfThickness);
			out_frame.lineTo(rect.getMinX() - halfThickness, rect.getMinY() + halfThickness);
			out_frame.closePath();
		}
	}
	
	public static void getAngledRectangle(Path2D.Float out_rect, Point2D.Float[] coords)
	{
		out_rect.reset();
		out_rect.moveTo(coords[0].x, coords[0].y);
		for(int i = 1; i < 4; ++i) out_rect.lineTo(coords[i].x, coords[i].y);
		out_rect.closePath();
	}
	
	public static void getAngledRectangleCoords(Point2D.Float[] out_coords, Point2D.Float startPoint, Vector2D angleDelta, float width, float height)
	{
		if(width > 0 && height > 0) {
			Vector2D norm = new Vector2D(-angleDelta.y, angleDelta.x);
			
			angleDelta = new Vector2D(norm);
			angleDelta.scale(height * 0.5f);
			
			Vector2D current = new Vector2D(startPoint.x + angleDelta.x, startPoint.y + angleDelta.y);
			
			out_coords[0].setLocation(current.x, current.y);
			_setNextAngledRectanglePoint(out_coords, 1, angleDelta, norm, current, width);
			_setNextAngledRectanglePoint(out_coords, 2, angleDelta, norm, current, height);
			_setNextAngledRectanglePoint(out_coords, 3, angleDelta, norm, current, width);
		}
	}
	
	private static void _setNextAngledRectanglePoint(Point2D.Float[] out_coords, int i, Vector2D angle, Vector2D norm, Vector2D current, float scale)
	{
		angle.x = -norm.y;
		angle.y = norm.x;
		norm.x = angle.x;
		norm.y = angle.y;
		angle.scale(scale);
		current.add(angle);
		out_coords[i].setLocation(current.x, current.y);
	}
	
	public DrawableObject()
	{
		_mainColor = Color.WHITE;
		_boundingBox = new Rectangle2D.Float();
		stopDrawing();
	}
	
	public DrawableObject(float width, float height)
	{
		_mainColor = Color.WHITE;
		_boundingBox = new Rectangle2D.Float(0, 0, (width > 0) ? width : 0, (height > 0) ? height : 0);
		stopDrawing();
	}
	
	public DrawableObject(float width, float height, Color color)
	{
		_mainColor = color;
		_boundingBox = new Rectangle2D.Float(0, 0, (width > 0) ? width : 0, (height > 0) ? height : 0);
		stopDrawing();
	}
	
	public final Vector2D getLocationDelta(DrawableObject other)
	{
		return Vector2D.getDelta(getLocation(), other.getLocation());
	}
	
	public final Vector2D getLocationDelta(float x, float y)
	{
		return Vector2D.getDelta(getLocationX(), getLocationY(), x, y);
	}
	
	public final Vector2D getLocationDelta(Point2D.Float p)
	{
		return Vector2D.getDelta(getLocation(), p);
	}
	
	public final Vector2D getLocationDelta(Vector2D v)
	{
		return Vector2D.getDelta(getLocationX(), getLocationY(), v);
	}
	
	public final Color getMainColor()
	{
		return _mainColor;
	}
	
	public final void setMainColor(Color color)
	{
		_mainColor = color;
	}
	
	public final Rectangle2D.Float getBoundingBox()
	{
		return new Rectangle2D.Float(_boundingBox.x, _boundingBox.y, _boundingBox.width, _boundingBox.height);
	}
	
	public final float getLeft()
	{
		return (float)_boundingBox.getMinX();
	}
	
	public final float getRight()
	{
		return (float)_boundingBox.getMaxX();
	}
	
	public final float getTop()
	{
		return (float)_boundingBox.getMinY();
	}
	
	public final float getBottom()
	{
		return (float)_boundingBox.getMaxY();
	}
	
	public final float getLocationX()
	{
		return (float)_boundingBox.getCenterX();
	}
	
	public final float getLocationY()
	{
		return (float)_boundingBox.getCenterY();
	}
	
	public final float getWidth()
	{
		return _boundingBox.width;
	}
	
	public final float getHeight()
	{
		return _boundingBox.height;
	}
	
	public final Point2D.Float getLocation()
	{
		return new Point2D.Float(getLocationX(), getLocationY());
	}
	
	public final void setLocationX(float x)
	{
		offsetLocationX(x - getLocationX());
	}
	
	public final void setLocationY(float y)
	{
		offsetLocationY(y - getLocationY());
	}
	
	public final void setLocation(float x, float y)
	{
		setLocationX(x);
		setLocationY(y);
	}
	
	public final void setLocation(Point2D.Float location)
	{
		setLocationX(location.x);
		setLocationY(location.y);
	}
	
	public void offsetLocationX(float x)
	{
		_boundingBox.x += x;
	}
	
	public void offsetLocationY(float y)
	{
		_boundingBox.y += y;
	}
	
	public final void offsetLocation(float x, float y)
	{
		offsetLocationX(x);
		offsetLocationY(y);
	}
	
	public final void offsetLocation(Point2D.Float location)
	{
		offsetLocationX(location.x);
		offsetLocationY(location.y);
	}
	
	public boolean collisionCheck(DrawableObject other)
	{
		return Collider.rectangles(_boundingBox, other._boundingBox);
	}
	
	public final boolean isDrawn()
	{
		return _isDrawn;
	}
	
	public final void startDrawing()
	{
		_isDrawn = true;
	}
	
	public final void stopDrawing()
	{
		_isDrawn = false;
	}
	
	public final void draw(Graphics2D g2d)
	{
		if(_isDrawn)
			if(getRight() >= 0 && getBottom() >= 0 && getLeft() <= GameToolbox.windowWidth && getTop() <= GameToolbox.windowHeight)
				drawObject(g2d);
	}
	
	public final void drawBoundingBox(Graphics2D g2d)
	{
		if(_isDrawn) {
			g2d.setColor(boundingBoxColor);
			g2d.draw(_boundingBox);
		}
	}
	
	protected abstract void drawObject(Graphics2D g2d);
}
