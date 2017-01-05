package LF.PoolGame.Drawable.GameObjects;

import LF.PoolGame.Drawable.DrawableObject;
import LF.PoolGame.Logic.GameSettings;
import LF.PoolGame.Math.Vector2D;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class MovableObject extends DrawableObject
{
	public static Color velocityVectorColor = Color.RED;
	
	protected Vector2D _velocity;
	
	public MovableObject()
	{
		super();
		_velocity = new Vector2D();
	}
	
	public MovableObject(float width, float height)
	{
		super(width, height);
		_velocity = new Vector2D();
	}
	
	public MovableObject(float width, float height, Color color)
	{
		super(width, height, color);
		_velocity = new Vector2D();
	}
	
	public final float getVelocityFractionX()
	{
		return _velocity.x / GameSettings.timeFractions;
	}
	
	public final float getVelocityFractionY()
	{
		return _velocity.y / GameSettings.timeFractions;
	}
	
	public final Vector2D getVelocityFraction()
	{
		return new Vector2D(getVelocityFractionX(), getVelocityFractionY());
	}
	
	public final float getVelocityX()
	{
		return _velocity.x;
	}
	
	public final float getVelocityY()
	{
		return _velocity.y;
	}
	
	public final Vector2D getVelocity()
	{
		return new Vector2D(_velocity);
	}
	
	public void setVelocityX(float x)
	{
		_velocity.x = x;
	}
	
	public void setVelocityY(float y)
	{
		_velocity.y = y;
	}
	
	public final void setVelocity(float x, float y)
	{
		setVelocityX(x);
		setVelocityY(y);
	}
	
	public final void setVelocity(Vector2D velocity)
	{
		setVelocityX(velocity.x);
		setVelocityY(velocity.y);
	}
	
	public final void offsetVelocityX(float x)
	{
		setVelocityX(_velocity.x + x);
	}
	
	public final void offsetVelocityY(float y)
	{
		setVelocityY(_velocity.y + y);
	}
	
	public final void offsetVelocity(float x, float y)
	{
		offsetVelocityX(x);
		offsetVelocityY(y);
	}
	
	public final void offsetVelocity(Vector2D velocity)
	{
		offsetVelocityX(velocity.x);
		offsetVelocityY(velocity.y);
	}
	
	public final Vector2D getNormVelocity()
	{
		return _velocity.getNormalized();
	}
	
	public final float getVelocityLengthSquared()
	{
		return _velocity.getLengthSquared();
	}
	
	public final float getVelocityLength()
	{
		return _velocity.getLength();
	}
	
	public final boolean isMoving()
	{
		return (_velocity.x != 0 || _velocity.y != 0);
	}
	
	public final void drawVector(Graphics2D g2d)
	{
		if(isDrawn())
			_velocity.draw((int)(getLocationX() + 0.5f), (int)(getLocationY() + 0.5f), 8, velocityVectorColor, g2d);
	}
	
	public abstract void update();
}
