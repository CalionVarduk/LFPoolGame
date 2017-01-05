package LF.PoolGame.Drawable.GameObjects;

import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.CuePointer;
import LF.PoolGame.Logic.GameToolbox;
import LF.PoolGame.Logic.Player;
import LF.PoolGame.Logic.PlayerType;
import LF.PoolGame.Math.Collider;
import LF.PoolGame.Math.Vector2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public final class PoolCue extends MovableObject
{
	private PoolBall _cueBall;
	private CuePointer _pointer;
	
	private float _length;
	private float _tipLength;
	private float _startPointDelta;
	private float _width;
	
	private Point2D.Float _startPoint;
	private Point2D.Float[] _coords;
	private Point2D.Float[] _tipCoords;
	private Path2D.Float _cueRectangle;
	private Path2D.Float _cueTipRectangle;
	private Color _tipColor;
	
	private PlayerType _ownerType;
	
	public PoolCue(PoolBall cueBall)
	{
		super();
		_construct(cueBall, 0, 0, 0, 0);
		setMainColor(Color.BLACK);
		setTipColor(Color.WHITE);
	}
	
	public PoolCue(PoolBall cueBall, float length, float tipLength, float width, float startPointDelta, Color color, Color tipColor)
	{
		super();
		_construct(cueBall, length, tipLength, width, startPointDelta);
		setMainColor(color);
		setTipColor(tipColor);
	}
	
	public void setupCuePointer(int crosshairLineLength, Color color)
	{
		_pointer.setCrosshairLineLength(crosshairLineLength);
		_pointer.setMainColor(color);
	}
	
	public void setOwner(Player player)
	{
		_ownerType = player.getType();
		if(_ownerType != PlayerType.Human) _pointer.stopDrawing();
	}
	
	public boolean isAiControlled()
	{
		return (_ownerType != PlayerType.Human);
	}
	
	public void offsetLocationX(float x)
	{
		super.offsetLocationX(x);
		_startPoint.x += x;
		
		for(int i = 0; i < 4; ++i) {
			_tipCoords[i].x += x;
			_coords[i].x += x;
		}
		getAngledRectangle(_cueTipRectangle, _tipCoords);
		getAngledRectangle(_cueRectangle, _coords);	
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		_startPoint.y += y;
		
		for(int i = 0; i < 4; ++i) {
			_tipCoords[i].y += y;
			_coords[i].y += y;
		}
		getAngledRectangle(_cueTipRectangle, _tipCoords);
		getAngledRectangle(_cueRectangle, _coords);	
	}
	
	public void setPointerLocation(float x, float y)
	{
		_pointer.setLocation(x, y);
	}
	
	public void setTipLength(float length)
	{
		_tipLength = (length > 0) ? length : 0;
	}
	
	public void setCueLength(float length)
	{
		_length = (length > 0) ? length : 0;
	}
	
	public float getCueLength()
	{
		return _length;
	}
	
	public float getCueTipLength()
	{
		return _tipLength;
	}
	
	public float getFullLength()
	{
		return _length + _tipLength;
	}
	
	public float getCueWidth()
	{
		return _width;
	}
	
	public void setCueWidth(float width)
	{
		_width = (width > 0) ? width : 0;
	}
	
	public float getStartPointDelta()
	{
		return _startPointDelta;
	}
	
	public void setStartPointDelta(float delta)
	{
		_startPointDelta = ((delta > 0) ? delta : 0) + _cueBall.getRadius();
	}
	
	public Vector2D getCueBallDelta()
	{
		return _pointer.getCueBallDelta();
	}
	
	public Color getTipColor()
	{
		return _tipColor;
	}
	
	public void setTipColor(Color color)
	{
		_tipColor = color;
	}
	
	public void fixBoundingBox()
	{
		float minX = _tipCoords[0].x;
		float maxX = _tipCoords[0].x;
		float minY = _tipCoords[0].y;
		float maxY = _tipCoords[0].y;
		
		for(int i = 1; i < 4; ++i) {
			if(minX > _tipCoords[i].x) minX = _tipCoords[i].x;
			if(maxX < _tipCoords[i].x) maxX = _tipCoords[i].x;
			if(minY > _tipCoords[i].y) minY = _tipCoords[i].y;
			if(maxY < _tipCoords[i].y) maxY = _tipCoords[i].y;
		}
		for(int i = 0; i < 4; ++i) {
			if(minX > _coords[i].x) minX = _coords[i].x;
			if(maxX < _coords[i].x) maxX = _coords[i].x;
			if(minY > _coords[i].y) minY = _coords[i].y;
			if(maxY < _coords[i].y) maxY = _coords[i].y;
		}
		
		_boundingBox.x = minX;
		_boundingBox.y = minY;
		_boundingBox.width = maxX - minX;
		_boundingBox.height = maxY - minY;
	}
	
	public void update()
	{
		if(isMoving()) {
			if(_pointer.isDrawn()) _pointer.stopDrawing();
			offsetLocation(getVelocityFractionX(), getVelocityFractionY());
			_collisionCheckWithCueBall();
		}
		else {
			if(!isAiControlled()) {
				if(!_pointer.isDrawn()) _pointer.startDrawing();
				_pointer.setLocation(GameToolbox.cursorX, GameToolbox.cursorY);
			}
			Vector2D delta = _pointer.getCueBallDelta();
			delta.normalize();
			
			offsetLocationX(_cueBall.getLocationX() - (delta.x * _startPointDelta) - _startPoint.x);
			offsetLocationY(_cueBall.getLocationY() - (delta.y * _startPointDelta) - _startPoint.y);
			_setCueRectangles(delta);
		}
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		if(_cueBall.isInGame()) {
			_pointer.draw(g2d);
			
			g2d.setColor(_tipColor);
			g2d.fill(_cueTipRectangle);
			
			g2d.setColor(_mainColor);
			g2d.fill(_cueRectangle);
		}
	}
	
	private void _collisionCheckWithCueBall()
	{
		if(Collider.circlePoint(_cueBall.getBoundingBox(), _startPoint)) {
			stopDrawing();
			if(_cueBall.isInGame()) _cueBall.setVelocity(_velocity);
			_velocity.set(0, 0);
		}
	}
	
	private void _setCueRectangles(Vector2D delta)
	{		
		Point2D.Float tipEndPoint = new Point2D.Float(_startPoint.x - (delta.x * (_tipLength - 1)), _startPoint.y - (delta.y * (_tipLength - 1)));
		getAngledRectangleCoords(_tipCoords, _startPoint, new Vector2D(delta), _tipLength, _width);
		getAngledRectangleCoords(_coords, tipEndPoint, delta, _length, _width);
		getAngledRectangle(_cueTipRectangle, _tipCoords);
		getAngledRectangle(_cueRectangle, _coords);
		fixBoundingBox();
	}
	
	private void _construct(PoolBall cueBall, float length, float tipLength, float width, float startPointDelta)
	{
		_ownerType = PlayerType.Human;
		_cueBall = cueBall;
		_pointer = new CuePointer(cueBall, 4, MoreColors.silver);
		_pointer.startDrawing();
		
		_startPoint = new Point2D.Float();
		_cueRectangle = new Path2D.Float();
		_cueTipRectangle = new Path2D.Float();
		
		_tipCoords = new Point2D.Float[4];
		_coords = new Point2D.Float[4];
		for(int i = 0; i < 4; ++i) {
			_tipCoords[i] = new Point2D.Float();
			_coords[i] = new Point2D.Float();
		}
		
		setTipLength(tipLength);
		setCueLength(length);
		setCueWidth(width);
		setStartPointDelta(startPointDelta);
	}
}
