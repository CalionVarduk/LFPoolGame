package LF.PoolGame.Drawable;

import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.GameObjects.PoolBall;
import LF.PoolGame.Logic.GameToolbox;
import LF.PoolGame.Math.Collider;
import LF.PoolGame.Math.Vector2D;

import java.awt.Color;
import java.awt.Graphics2D;

public final class CuePointer extends DrawableObject
{
	private PoolBall _cueBall;
	private int _crosshairLineLength;
	
	public CuePointer(PoolBall cueBall)
	{
		super(GameToolbox.poolBallSize, GameToolbox.poolBallSize);
		_cueBall = cueBall;
		_crosshairLineLength = 0;
	}
	
	public CuePointer(PoolBall cueBall, int crosshairLineLength, Color color)
	{
		super(GameToolbox.poolBallSize, GameToolbox.poolBallSize, color);
		_cueBall = cueBall;
		setCrosshairLineLength(crosshairLineLength);
	}
	
	public int getCrosshairLineLength()
	{
		return _crosshairLineLength;
	}
	
	public void setCrosshairLineLength(int crosshairLineLength)
	{
		_crosshairLineLength = (crosshairLineLength <= 0) ? 0 :
								(crosshairLineLength < (int)_cueBall.getRadius()) ? crosshairLineLength : (int)_cueBall.getRadius();
	}
	
	public Vector2D getCueBallDelta()
	{
		return getLocationDelta(_cueBall);
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		_drawCrosshair(g2d);
		if(!Collider.circles(_cueBall.getBoundingBox(), _boundingBox))
			_drawPointerLine(g2d);
	}
	
	private void _drawCrosshair(Graphics2D g2d)
	{
		fillEllipse(_boundingBox, MoreColors.darkTransparent, g2d);
		drawEllipse(_boundingBox, _mainColor, g2d);
		
		int x = (int)getLocationX();
		int y = (int)getLocationY();
		g2d.drawLine((int)getLeft(), y, (int)getLeft() + _crosshairLineLength, y);
		g2d.drawLine((int)getRight(), y, (int)getRight() - _crosshairLineLength, y);
		g2d.drawLine(x, (int)getTop(), x, (int)getTop() + _crosshairLineLength);
		g2d.drawLine(x, (int)getBottom(), x, (int)getBottom() - _crosshairLineLength);
	}
	
	private void _drawPointerLine(Graphics2D g2d)
	{
		Vector2D delta = getCueBallDelta();
		delta.normalize();
		delta.scale(_cueBall.getRadius());
		
		int x1 = (int)(_cueBall.getLocationX() + delta.x + 0.5f);
		int y1 = (int)(_cueBall.getLocationY() + delta.y + 0.5f);
		int x2 = (int)(getLocationX() - delta.x + 0.5f);
		int y2 = (int)(getLocationY() - delta.y + 0.5f);
		
		g2d.drawLine(x1, y1, x2, y2);
	}
}
