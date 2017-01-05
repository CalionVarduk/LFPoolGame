package LF.PoolGame.Math;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;

public abstract class Collider
{
	public static boolean rectangles(RectangularShape rect1, RectangularShape rect2)
	{
		if((rect1.getMinX() >= rect2.getMinX() && rect1.getMinX() <= rect2.getMaxX()) || 
			(rect1.getMaxX() >= rect2.getMinX() && rect1.getMaxX() <= rect2.getMaxX()))
			if((rect1.getMinY() >= rect2.getMinY() && rect1.getMinY() <= rect2.getMaxY()) || 
				(rect1.getMaxY() >= rect2.getMinY() && rect1.getMaxY() <= rect2.getMaxY()))
				return true;
		return false;
	}
	
	public static boolean rectanglePoint(RectangularShape rect, Point2D.Float point)
	{
		if(rect.getMinX() <= point.x && rect.getMaxX() >= point.x)
			if(rect.getMinY() <= point.y && rect.getMaxY() >= point.y)
				return true;
		return false;
	}
	
	public static boolean circles(RectangularShape circle1, RectangularShape circle2)
	{
		Vector2D delta =  Vector2D.getDelta((float)circle1.getCenterX(), (float)circle1.getCenterY(), (float)circle2.getCenterX(), (float)circle2.getCenterY());
		float radius2 = (float)circle1.getWidth() * 0.5f + (float)circle2.getWidth() * 0.5f;
		radius2 *= radius2;
		
		return (delta.getLengthSquared() <= radius2);
	}
	
	public static boolean circlePoint(RectangularShape circle, Point2D.Float point)
	{
		Vector2D delta = Vector2D.getDelta((float)circle.getCenterX(), (float)circle.getCenterY(), point.x, point.y);
		float radius2 = (float)circle.getWidth() * 0.5f;
		radius2 *= radius2;
		
		return (delta.getLengthSquared() <= radius2);
	}
}
