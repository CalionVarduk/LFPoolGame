package LF.PoolGame.Drawable.GameObjects;

import LF.PoolGame.Math.*;
import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.Text.DrawableString;
import LF.PoolGame.Logic.BallType;
import LF.PoolGame.Logic.GameRules;
import LF.PoolGame.Logic.GameSettings;
import LF.PoolGame.Logic.GameToolbox;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public final class PoolBall extends MovableObject
{
	public static boolean debugBallCollision = true;
	
	public static Color fontColor = Color.BLACK;
	public static Font numberFont = new Font("TimesRoman", Font.PLAIN, 7);
	
	private Vector2D _normVelocity;
	private float _radius;
	private DrawableString _number;
	private BallType _type;
	private boolean _isInPocket;
	
	public PoolBall()
	{
		super();
		_construct(0, 0, 0);
	}
	
	public PoolBall(float diameter, int number, int ballCount)
	{
		super();
		_construct(diameter, number, ballCount);
	}
	
	public PoolBall(float diameter, int number, int ballCount, Color color)
	{
		super();
		_construct(diameter, number, ballCount);
		setMainColor(color);
	}
	
	public void spawn(float x, float y)
	{
		_isInPocket = false;
		_number.spawnCentre(getLocationX(), getLocationY());
		setLocation(x, y);
		startDrawing();
	}
	
	public boolean isInGame()
	{
		return isDrawn() && !_isInPocket;
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
	
	public void offsetLocationX(float x)
	{
		super.offsetLocationX(x);
		_number.setLocationX(getLocationX());
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		_number.setLocationY(getLocationY());
	}
	
	public void setVelocityX(float x)
	{
		super.setVelocityX(x);
		_normVelocity = _velocity.getNormalized();
	}
	
	public void setVelocityY(float y)
	{
		super.setVelocityY(y);
		_normVelocity = _velocity.getNormalized();
	}
	
	public int getNumber()
	{
		return Integer.parseInt(_number.getString());
	}
	
	public void setNumber(int number)
	{
		if(number < 0) number = 0;
		_number.setString(Integer.toString(number));
	}
	
	public boolean isInPocket()
	{
		return _isInPocket;
	}
	
	public void pullOutOfPocket()
	{
		_isInPocket = false;
	}
	
	public BallType getType()
	{
		return (getNumber() == 8) ? BallType.Ball8 : _type;
	}
	
	public void setType(int ballCount)
	{
		_type = (ballCount > 0) ? ((getNumber() <= (ballCount >> 1)) ? BallType.Solids : BallType.Stripes) : BallType.Solids;
	}
	
	public void update()
	{
		if(isInGame()) {
			offsetLocation(getVelocityFractionX(), getVelocityFractionY());
			if(GameSettings.frictionOn) _updateFriction();
		}
	}
	
	public boolean collisionCheck(PoolBall other)
	{
		if(isInGame() && other.isInGame()) {
			if(super.collisionCheck(other)) {
				if(Collider.circles(_boundingBox, other._boundingBox)) {
					Vector2D delta;
					
					if(isMoving()) {
						delta = other.getLocationDelta(this);
						_fixPreDynamicCollisionLocation(delta, other._velocity.getLength(), _radius + other._radius);
					}
					
					/*if(debugBallCollision) {
						float l = Vector2D.getDelta(getLocation(), other.getLocation()).getLength();
						if(l > 23.25f || l < 22.75f) System.out.println("dyn l: " + l);
					}*/
                    
                    if(other.isMoving()) {
                    	delta = getLocationDelta(other);
                    	other._fixPreStaticCollisionLocation(delta, _radius + other._radius);
                    }
					
					if(debugBallCollision) {
						float l = Vector2D.getDelta(getLocation(), other.getLocation()).getLength();
						if(l > 23.1f) System.out.println("l: " + l + "\n\n");
					}
                    
                    delta = other.getLocationDelta(this);
					delta.normalize();
					
					Vector2D V1v1 = _getPartialVelocityFromSelf(delta);
					Vector2D V1v2 = _getPartialVelocityFromOther(delta, other._velocity);
					Vector2D V2v2 = other._getPartialVelocityFromSelf(delta);
					Vector2D V2v1 = other._getPartialVelocityFromOther(delta, _velocity);
					
					setVelocity(V1v1.x + V1v2.x, V1v1.y + V1v2.y);
					other.setVelocity(V2v1.x + V2v2.x, V2v1.y + V2v2.y);
					
					if(GameSettings.frictionOn) {
						_velocity.scale(GameSettings.ballCollisionSpeedRetained);
						other._velocity.scale(GameSettings.ballCollisionSpeedRetained);
					}
					
					if(GameRules.firstHitType == BallType.OpenTable) GameRules.firstHitType = other.getType();
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean collisionCheck(RoundBumper bumper)
	{
		if(isInGame() && bumper.isDrawn()) {
			if(super.collisionCheck(bumper)) {
				if(Collider.circles(_boundingBox, bumper.getBoundingBox())) {
					Vector2D delta = bumper.getLocationDelta(this);
					_fixPreStaticCollisionLocation(delta, _radius + bumper.getRadius());
					
					delta = bumper.getLocationDelta(this);
					delta.normalize();
					
					Vector2D V1v1 = _getPartialVelocityFromSelf(delta);
					Vector2D V1v2 = _getPartialVelocityFromOther(delta, _velocity);
					
					setVelocity(V1v1.x - V1v2.x, V1v1.y - V1v2.y);
					if(GameSettings.frictionOn) _velocity.scale(GameSettings.bumperCollisionSpeedRetained);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean collisionCheck(SegmentBumper bumper)
	{
		if(isInGame() && bumper.isDrawn()) {
			if(super.collisionCheck(bumper)) {
                Vector2D A = getLocationDelta(bumper.getStart());
                float distanceA = bumper.getNormParallel().getDotProduct(A);

                if(distanceA > 0) {
	                Vector2D B = getLocationDelta(bumper.getEnd());
	                float distanceB = bumper.getNormInverseParallel().getDotProduct(B);
	                
	                if(distanceB > 0) {
	                	float inacurracyLength = _radius - bumper.getNormPerpendicular().getDotProduct(A);
	                	
	                	if(inacurracyLength > 0) {
	                		Vector2D inacurracy = bumper.getNormPerpendicular();
	                		inacurracy.scale(inacurracyLength);
	                		offsetLocation(inacurracy.x, inacurracy.y);
	                		
	    					Vector2D V1v1 = _getPartialVelocityFromSelf(bumper.getNormPerpendicular());
	    					Vector2D V1v2 = _getPartialVelocityFromOther(bumper.getNormPerpendicular(), _velocity);
	    					
	    					setVelocity(V1v1.x - V1v2.x, V1v1.y - V1v2.y);
	    					if(GameSettings.frictionOn) _velocity.scale(GameSettings.bumperCollisionSpeedRetained);
	    					return true;
	                	}
	                }
                }
			}
		}
		return false;
	}
	
	public boolean collisionCheck(PoolPocket pocket)
	{
		if(isInGame() && pocket.isDrawn()) {
			if(super.collisionCheck(pocket)) {
				if(Collider.circlePoint(pocket.getBoundingBox(), getLocation())) {
					_isInPocket = true;
					setVelocity(0, 0);
					if(!_number.getString().equalsIgnoreCase("0")) {
						setLocationX(GameToolbox.pottedStartLocation.x + (GameRules.pottedPoolBalls.size() * (_boundingBox.width + 5)));
						setLocationY(GameToolbox.pottedStartLocation.y);
						GameRules.pottedPoolBalls.add(this);
					}
					else stopDrawing();
					return true;
				}
			}
		}
		return false;
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		if(_type == BallType.Solids) _drawSolidBall(g2d);
		else _drawStripedBall(g2d);
	}
	
	private void _updateFriction()
	{
		if(isMoving()) {
			offsetVelocity(GameSettings.ballFrictionDelta * -_normVelocity.x, GameSettings.ballFrictionDelta * -_normVelocity.y);
			if(_velocity.getLengthSquared() < GameSettings.ballFrictionDelta) _velocity.set(0, 0);
		}
	}
	
	private void _fixPreStaticCollisionLocation(Vector2D locationDelta, float radiusSum)
	{
		float b = _getQuadraticEquationArgB(locationDelta);
		float c = _getQuadraticEquationArgC(locationDelta, radiusSum);
		
		Vector2D solution = QuadraticEquation.solve(1, b, c);

        if(Math.abs(solution.x) < Math.abs(solution.y)) {
        	if(!Float.isNaN(solution.x))
        		offsetLocation(_normVelocity.x * solution.x, _normVelocity.y * solution.x);
        }
        else if(!Float.isNaN(solution.y))
        	offsetLocation(_normVelocity.x * solution.y, _normVelocity.y * solution.y);
	}
	
	private void _fixPreDynamicCollisionLocation(Vector2D locationDelta, float otherSpeed, float radiusSum)
	{
		float speedSum = _velocity.getLength() + otherSpeed;
		float speedRatio = otherSpeed / speedSum;
		
		float b = _getQuadraticEquationArgB(locationDelta);
		float c = _getQuadraticEquationArgC(locationDelta, radiusSum);
		
		Vector2D solution = QuadraticEquation.solve(1, b, c);

        if(Math.abs(solution.x) < Math.abs(solution.y)) {
        	if(!Float.isNaN(solution.x)) {
	        	float aux = Math.abs(solution.x) * speedRatio;
	            offsetLocation((_normVelocity.x * solution.x) + (_normVelocity.x * aux), (_normVelocity.y * solution.x) + (_normVelocity.y * aux));
        	}
        }
        else if(!Float.isNaN(solution.y)) {
        	float aux = Math.abs(solution.y) * speedRatio;
        	offsetLocation((_normVelocity.x * solution.y) + (_normVelocity.x * aux), (_normVelocity.y * solution.y) + (_normVelocity.y * aux));
        }
	}
	
	private float _getQuadraticEquationArgB(Vector2D delta)
	{
		return (2 * _normVelocity.x * -delta.x) + (2 * _normVelocity.y * -delta.y);
	}
	
	private float _getQuadraticEquationArgC(Vector2D delta, float radiusSum)
	{
		return (delta.x * delta.x) + (delta.y * delta.y) - (radiusSum * radiusSum);
	}
	
	private Vector2D _getPartialVelocityFromSelf(Vector2D normDelta)
	{
		return new Vector2D((_velocity.x - (normDelta.x * (normDelta.x * _velocity.x))) - (normDelta.x * (normDelta.y * _velocity.y)), 
							(_velocity.y - (normDelta.y * (normDelta.y * _velocity.y))) - (normDelta.y * (normDelta.x * _velocity.x)));
	}
	
	private Vector2D _getPartialVelocityFromOther(Vector2D normDelta, Vector2D otherSpeed)
	{
		return new Vector2D((normDelta.x * (normDelta.x * otherSpeed.x)) + (normDelta.x * (normDelta.y * otherSpeed.y)),
							(normDelta.y * (normDelta.x * otherSpeed.x)) + (normDelta.y * (normDelta.y * otherSpeed.y)));
	}
	
	private void _drawSolidBall(Graphics2D g2d)
	{
		fillEllipse(_boundingBox, _mainColor, g2d);
		
		if(!_number.getString().equalsIgnoreCase("0")) {
			fillEllipse(_getInnerCircleEllipse(), MoreColors.whiteSmoke, g2d);
			_drawNumber(g2d);
		}
	}
	
	private void _drawStripedBall(Graphics2D g2d)
	{
		fillEllipse(_boundingBox, MoreColors.whiteSmoke, g2d);
		_drawStripe(g2d);
		
		fillEllipse(_getInnerCircleEllipse(), MoreColors.whiteSmoke, g2d);
		_drawNumber(g2d);
	}
	
	private void _drawStripe(Graphics2D g2d)
	{
		Rectangle2D.Float stripe = new Rectangle2D.Float(_boundingBox.x + 2, _boundingBox.y + _boundingBox.height * 0.25f, _boundingBox.width - 4, _boundingBox.height * 0.5f);
		g2d.setColor(_mainColor);
		g2d.fill(stripe);
	}
	
	private void _drawNumber(Graphics2D g2d)
	{
		g2d.setFont(numberFont);
		g2d.setColor(fontColor);
		_number.draw(g2d);
	}
	
	private Ellipse2D.Float _getInnerCircleEllipse()
	{
		float halfRadius = _radius * 0.5f;
		return new Ellipse2D.Float(_boundingBox.x + halfRadius, _boundingBox.y + halfRadius, _radius, _radius);
	}
	
	private void _construct(float diameter, int number, int ballCount)
	{
		_isInPocket = false;
		_normVelocity = new Vector2D(0, 0);
		_number = new DrawableString("", fontColor, numberFont);
		setDiameter(diameter);
		setNumber(number);
		setType(ballCount);
	}
}