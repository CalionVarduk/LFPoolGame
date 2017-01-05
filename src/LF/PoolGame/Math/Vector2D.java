package LF.PoolGame.Math;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Vector2D
{
	public float x, y;
	
	public Vector2D()
	{
		set(0, 0);
	}
	
	public Vector2D(Point2D p)
	{
		set((float)p.getX(), (float)p.getY());
	}
	
	public Vector2D(Vector2D other)
	{
		set(other);
	}
	
	public Vector2D(float x, float y)
	{
		set(x, y);
	}
	public Vector2D getDelta(Vector2D other)
	{
		return getDelta(other.x, other.y);
	}
	
	public Vector2D getDelta(float x, float y)
	{
		return new Vector2D(this.x - x, this.y - y);
	}
	
	public static Vector2D getDelta(Vector2D v1, Vector2D v2)
	{
		return getDelta(v1.x, v1.y, v2.x, v2.y);
	}
	
	public static Vector2D getDelta(Point2D.Float p1, Point2D.Float p2)
	{
		return getDelta(p1.x, p1.y, p2.x, p2.y);
	}
	
	public static Vector2D getDelta(Vector2D v1, float x2, float y2)
	{
		return getDelta(v1.x, v1.y, x2, y2);
	}
	
	public static Vector2D getDelta(float x1, float y1, Vector2D v2)
	{
		return getDelta(x1, y1, v2.x, v2.y);
	}
	
	public static Vector2D getDelta(float x1, float y1, float x2, float y2)
	{
		return new Vector2D(x1 - x2, y1 - y2);
	}
	
	public void set(Vector2D other)
	{
		set(other.x, other.y);
	}
	
	public void set(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void add(Vector2D other)
	{
		add(other.x, other.y);
	}
	
	public void add(float x, float y)
	{
		this.x += x;
		this.y += y;
	}
	
	public void subtract(Vector2D other)
	{
		subtract(other.x, other.y);
	}
	
	public void subtract(float x, float y)
	{
		this.x -= x;
		this.y -= y;
	}
	
	public void multiply(Vector2D other)
	{
		multiply(other.x, other.y);
	}
	
	public void multiply(float x, float y)
	{
		this.x *= x;
		this.y *= y;
	}
	
	public void scale(float scale)
	{
		multiply(scale, scale);
	}
	
	public void divide(Vector2D other)
	{
		divide(other.x, other.y);
	}
	
	public void divide(float x, float y)
	{
		this.x = (x != 0) ? this.x / x : 0;
		this.y = (y != 0) ? this.y / y : 0;
	}
	
	public float getLengthSquared()
	{
		return (x * x) + (y * y);
	}
	
	public float getLength()
	{
		return (float)Math.sqrt(getLengthSquared());
	}
	
	public void normalize()
	{
		float length = getLength();
		if(length > 0) set(x / length, y / length);
		else set(0, 0);
	}
	
	public Vector2D getNormalized()
	{
		Vector2D copy = new Vector2D(this);
		copy.normalize();
		return copy;
	}
	
	public void negate()
	{
		set(-x, -y);
	}
	
	public Vector2D getNegated()
	{
		return new Vector2D(-x, -y);
	}
	
	public float getDotProduct(Vector2D other)
	{
		return getDotProduct(other.x, other.y);
	}
	
	public float getDotProduct(float x, float y)
	{
		return (this.x * x) + (this.y * y);
	}
	
	public String toString()
	{
		return "Vector2D[" + Float.toString(x) + ", " + Float.toString(y) + "]";
	}
	
	public void draw(int x, int y, float scale, Color color, Graphics2D g2d)
	{
		if(scale < 0) scale = 1;
		Vector2D scaled = new Vector2D(this);
		scaled.scale(scale);
		
		g2d.setColor(color);
		g2d.drawLine(x, y, x + (int)(scaled.x + 0.5f), y + (int)(scaled.y + 0.5f));
	}
}
