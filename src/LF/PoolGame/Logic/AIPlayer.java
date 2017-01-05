package LF.PoolGame.Logic;

import LF.PoolGame.Math.Collider;
import LF.PoolGame.Math.Vector2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public final class AIPlayer extends Player
{
	public static boolean debugMakePlay = true;
	private static Random _rng = new Random(System.currentTimeMillis());
	
	private TrailVerdict[][] _trailVerdicts;
	
	private static final float _pocketAngleScoreMult = 60;
	private static final float _pocketMiddleMaxAngleX = 0.75f;
	private static final float _invertHitAngleScore = 300;
	private static final float _trailCollisionBaseScore = 200;
	private static final float _cueBallTrailMult = 3;
	private static final float _pocketTrailMult = 1.25f;
	
	public AIPlayer()
	{
		super();
		_type = PlayerType.MediumAI;
		_trailVerdicts = new TrailVerdict[16][6];
		for(int i = 1; i < _trailVerdicts.length; ++i)
			for(int j = 0; j < _trailVerdicts[i].length; ++j)
				_trailVerdicts[i][j] = new TrailVerdict(i, j);
	}
	
	public AIPlayer(String name, PlayerType type)
	{
		super(name);
		_type = type;
		_trailVerdicts = new TrailVerdict[16][6];
		for(int i = 1; i < _trailVerdicts.length; ++i)
			for(int j = 0; j < _trailVerdicts[i].length; ++j)
				_trailVerdicts[i][j] = new TrailVerdict(i, j);
	}
	
	public void makePlay()
	{
		if(GameRules.isCueBallInHand) {
			// TODO
			GameRules.isCueBallInHand = false;
	        cue.update();
	        cue.startDrawing();
			makePlay();
		}
		else {
			Point2D.Float[] realPocketLocation = new Point2D.Float[_table.getPocketCount()];
			Point2D.Float[] preferredPocketLocation = new Point2D.Float[_table.getPocketCount()];
			Rectangle2D.Float[] enhancedPocketBox = new Rectangle2D.Float[_table.getPocketCount()];

			for(int i = 0; i < realPocketLocation.length; ++i) {
				realPocketLocation[i] = _table.getPocketLocation(i);
				preferredPocketLocation[i] = _getPreferredPocketLocation(i);
				enhancedPocketBox[i] = _table.getPocketBoundingBox(i);
				enhancedPocketBox[i].x -= 5;
				enhancedPocketBox[i].y -= 5;
				enhancedPocketBox[i].width += 10;
				enhancedPocketBox[i].height += 10;
			}
			
			_initializeTrailVerdicts();
			_calculateTrailVerdicts(realPocketLocation, preferredPocketLocation, enhancedPocketBox);
			TrailVerdict trail = _getBestTrail();
			
			if(debugMakePlay) System.out.println(trail.iBall + " " + trail.iPocket + "\n\n\n");
			
			Point2D.Float target = trail.isSafe ? trail.cueBallTarget : _balls[trail.iBall].getLocation();
			target.x += _getRandomTargetOffset();
			target.y += _getRandomTargetOffset();
			cue.setPointerLocation(target.x, target.y);
			
			cue.update();
			
			float maxDistance = (float)Math.sqrt((_table.getWidth() * _table.getWidth()) + (_table.getHeight() * _table.getHeight()));
			maxDistance += maxDistance;
			
			System.out.println("hi");
			
			float velocity = GameToolbox.maxCueBallVelocity * ((trail.pocketDistance + trail.targetDistance) / maxDistance) * _getRandomVelocityOffset();
			if(GameRules.breakTime()) velocity *= 1.25f;
			else if(!trail.isSafe) velocity *= 0.4f;
			Vector2D delta = cue.getCueBallDelta();
			delta.normalize();
			delta.scale(velocity);
			cue.setVelocity(delta);
		}
	}
	
	private float _getRandomTargetOffset()
	{
		if(_type == PlayerType.EasyAI) return (_rng.nextFloat() * 12.0f) - 6.0f;
		else if(_type == PlayerType.MediumAI) return (_rng.nextFloat() * 5.5f) - 2.75f;
		return (_rng.nextFloat() * 0.35f) - 0.175f;
	}
	
	private float _getRandomVelocityOffset()
	{
		if(_type == PlayerType.EasyAI) return (_rng.nextFloat() * 1.1f) + 0.4f;
		else if(_type == PlayerType.MediumAI) return (_rng.nextFloat() * 0.6f) + 0.6f;
		return (_rng.nextFloat() * 0.2f) + 0.65f;
	}
	
	private TrailVerdict _getBestTrail()
	{
		int iBall = 1, iPocket = 0;
		for(int i = 1; i < _trailVerdicts.length; ++i) {
			for(int j = 0; j < _trailVerdicts[i].length; ++j) {
				if(_trailVerdicts[iBall][iPocket].penalty > _trailVerdicts[i][j].penalty) {
					iBall = i;
					iPocket = j;
				}
			}
		}

		return _trailVerdicts[iBall][iPocket];
	}
	
	private void _calculateTrailVerdicts(Point2D.Float[] realPocketLocation, Point2D.Float[] preferredPocketLocation, Rectangle2D.Float[] enhancedPocketBox)
	{
		//Vector2D ballParallel = new Vector2D();
		//Vector2D ballPerpendicular = new Vector2D();
		Vector2D targetParallel = new Vector2D();
		Vector2D targetPerpendicular = new Vector2D();
		Vector2D realPocketParallel = new Vector2D();
		Vector2D preferredPocketParallel = new Vector2D();
		
		for(int i = 1; i < _trailVerdicts.length; ++i) {
			if(!_balls[i].isInPocket()) {
				for(int j = 0; j < _trailVerdicts[i].length; ++j) {
					// pocket vectors and distance
					realPocketParallel = Vector2D.getDelta(realPocketLocation[j], _balls[i].getLocation());
					preferredPocketParallel = Vector2D.getDelta(preferredPocketLocation[j], _balls[i].getLocation());
					_trailVerdicts[i][j].pocketDistance = realPocketParallel.getLength();
					
					// set cue ball target point
					if(j != 1 && j != 4 && Collider.circles(_balls[i].getBoundingBox(), enhancedPocketBox[j]))
						_trailVerdicts[i][j].cueBallTarget.setLocation(preferredPocketLocation[j].x, preferredPocketLocation[j].y);
					else {
						Vector2D targetOffset = preferredPocketParallel.getNormalized();
						targetOffset.scale(GameToolbox.poolBallSize);
						_trailVerdicts[i][j].cueBallTarget.setLocation(_balls[i].getLocationX() - targetOffset.x, _balls[i].getLocationY() - targetOffset.y);
					}
					
					// ball/target vectors and distance
					/*ballParallel = Vector2D.getDelta(_balls[i].getLocation(), _balls[0].getLocation());
					ballParallel.normalize();
					ballPerpendicular.x = -ballParallel.y;
					ballPerpendicular.y = ballParallel.x;*/
					
					targetParallel = Vector2D.getDelta(_trailVerdicts[i][j].cueBallTarget, _balls[0].getLocation());
					_trailVerdicts[i][j].targetDistance = targetParallel.getLength();
					targetParallel.normalize();
					targetPerpendicular.x = -targetParallel.y;
					targetPerpendicular.y = targetParallel.x;
					
					// distance x angle check
					_trailVerdicts[i][j].collisionAngle.x = targetParallel.getDotProduct(preferredPocketParallel);
					_trailVerdicts[i][j].collisionAngle.y = targetPerpendicular.getDotProduct(preferredPocketParallel);
					_trailVerdicts[i][j].collisionAngle.normalize();
					
					preferredPocketParallel.normalize();
					
					// special for middle pockets
					if(j == 1 || j == 4) {
						float dx = _pocketMiddleMaxAngleX - Math.abs(preferredPocketParallel.x);
						if(dx < 0) {
							dx *= _pocketAngleScoreMult;
							_trailVerdicts[i][j].penalty += dx * dx;
						}
					}
					
					float penalty = 0;
					if(_trailVerdicts[i][j].collisionAngle.x < 0) {
						penalty = _invertHitAngleScore;
						_trailVerdicts[i][j].isSafe = false;
					}
					float angleMultiplier = 1.02f - _trailVerdicts[i][j].collisionAngle.x;
					float distanceMultiplier = _trailVerdicts[i][j].targetDistance + _trailVerdicts[i][j].pocketDistance * 2;
					_trailVerdicts[i][j].penalty += (penalty + (distanceMultiplier * (angleMultiplier * angleMultiplier)));
					
					if(debugMakePlay) System.out.println("angle values[" + i + "][" + j + "]: " + Float.toString(_trailVerdicts[i][j].penalty));
					
					// cue ball x target point trail check
					float maxPenalty = 0;
					for(int k = 1; k < i; ++k) {
						penalty = _checkTrailCollision(_balls[0].getLocation(), _trailVerdicts[i][j].cueBallTarget, _balls[k].getLocation(), _cueBallTrailMult);
						if(penalty > maxPenalty) maxPenalty = penalty;
					}
					for(int k = i + 1; k < _balls.length; ++k) {
						penalty = _checkTrailCollision(_balls[0].getLocation(), _trailVerdicts[i][j].cueBallTarget, _balls[k].getLocation(), _cueBallTrailMult);
						if(penalty > maxPenalty) maxPenalty = penalty;
					}
					_trailVerdicts[i][j].penalty += maxPenalty;
					if(maxPenalty > 0) _trailVerdicts[i][j].isSafe = false;
					
					// pocket x target ball trail check
					maxPenalty = 0;
					for(int k = 1; k < i; ++k) {
						penalty = _checkTrailCollision(_balls[i].getLocation(), preferredPocketLocation[j], _balls[k].getLocation(), _pocketTrailMult);
						if(penalty > maxPenalty) maxPenalty = penalty;
					}
					for(int k = i + 1; k < _balls.length; ++k) {
						penalty = _checkTrailCollision(_balls[i].getLocation(), preferredPocketLocation[j], _balls[k].getLocation(), _pocketTrailMult);
						if(penalty > maxPenalty) maxPenalty = penalty;
					}
					_trailVerdicts[i][j].penalty += maxPenalty;
					if(maxPenalty > 0) _trailVerdicts[i][j].isSafe = false;
					
					if(debugMakePlay) System.out.println("+trail values[" + i + "][" + j + "]: " + Float.toString(_trailVerdicts[i][j].penalty));
					
					// cue ball potting danger check
					
					// random penalty offset
					_trailVerdicts[i][j].penalty += _getRandomTrailScoreEffect(_trailVerdicts[i][j].penalty);
					
					if(debugMakePlay) System.out.println("total values[" + i + "][" + j + "]: " + Float.toString(_trailVerdicts[i][j].penalty));
				}
			}
		}
	}
	
	private float _getRandomTrailScoreEffect(float penalty)
	{
		if(_type == PlayerType.EasyAI) return (_rng.nextFloat() * penalty * 10) + _rng.nextInt(400);
		else if(_type == PlayerType.MediumAI) return (_rng.nextFloat() * penalty * 4) + _rng.nextInt(200);
		return (_rng.nextFloat() * penalty * 0.15f) + _rng.nextInt(5);
	}
	
	private void _initializeTrailVerdicts()
	{
		for(int i = 1; i < _trailVerdicts.length; ++i) {
			for(int j = 0; j < _trailVerdicts[i].length; ++j)
				_trailVerdicts[i][j].reset();
			
			if(_balls[i].isInPocket() || ((_ballType != BallType.OpenTable) && (_ballType != _balls[i].getType())))
				for(int j = 0; j < _trailVerdicts[i].length; ++j)
					_trailVerdicts[i][j].penalty = 1000000;
		}
	}
	
	private float _checkTrailCollision(Point2D.Float trailStart, Point2D.Float trailEnd, Point2D.Float checkLocation, float penaltyMultiplier)
	{
		Vector2D parallel = Vector2D.getDelta(trailEnd, trailStart).getNormalized();
		Vector2D invParallel = parallel.getNegated();
		Vector2D perpendicular = new Vector2D(-parallel.y, parallel.x);
		
        Vector2D A = Vector2D.getDelta(checkLocation, trailStart);
        float distanceA = parallel.getDotProduct(A);

        if(distanceA > 0) {
            Vector2D B = Vector2D.getDelta(checkLocation, trailEnd);
            float distanceB = invParallel.getDotProduct(B);
            
            if(distanceB > 0) {
            	float inacurracyLength = Math.abs(perpendicular.getDotProduct(A));
            	return (inacurracyLength < (GameToolbox.poolBallSize + 1)) ?
            			(_trailCollisionBaseScore + (inacurracyLength * inacurracyLength)) * penaltyMultiplier : 0;
            }
        }
        return 0;
	}
	
	private Point2D.Float _getPreferredPocketLocation(int iPocket)
	{
		Rectangle2D.Float boundingBox = _table.getPocketBoundingBox(iPocket);
		if(iPocket == 0) return new Point2D.Float((float)boundingBox.getMaxX(), (float)boundingBox.getMaxY());
		else if(iPocket == 1) return new Point2D.Float((float)boundingBox.getCenterX(), (float)boundingBox.getMaxY() - (_balls[0].getRadius() * 0.4f));
		else if(iPocket == 2) return new Point2D.Float((float)boundingBox.getMinX(), (float)boundingBox.getMaxY());
		else if(iPocket == 3) return new Point2D.Float((float)boundingBox.getMinX(), (float)boundingBox.getMinY());
		else if(iPocket == 4) return new Point2D.Float((float)boundingBox.getCenterX(), (float)boundingBox.getMinY() + (_balls[0].getRadius() * 0.4f));
		else if(iPocket == 5) return new Point2D.Float((float)boundingBox.getMaxX(), (float)boundingBox.getMinY());
		return new Point2D.Float(-1, -1);
	}
	
	private class TrailVerdict
	{
		public float penalty;
		public boolean isSafe;
		public Point2D.Float cueBallTarget;
		public float targetDistance;
		public float pocketDistance;
		public Vector2D collisionAngle;
		public int iBall;
		public int iPocket;
		
		public TrailVerdict(int iBall, int iPocket)
		{
			this.iBall = iBall;
			this.iPocket = iPocket;
			cueBallTarget = new Point2D.Float();
			collisionAngle = new Vector2D();
			reset();
		}
		
		public void reset()
		{
			penalty = 0;
			isSafe = true;
			cueBallTarget.setLocation(0, 0);
			targetDistance = 0;
			pocketDistance = 0;
			collisionAngle.set(0, 0);
		}
	}
}