package LF.PoolGame.Logic;

import LF.PoolGame.Drawable.GameUI;
import LF.PoolGame.Math.Collider;
import LF.PoolGame.Math.Vector2D;

import java.awt.geom.Rectangle2D;

public final class HumanPlayer extends Player
{
	public GameUI gameUI;
	
	public HumanPlayer()
	{
		super();
		_type = PlayerType.Human;
		gameUI = null;
	}
	
	public HumanPlayer(String name, GameUI gameUI)
	{
		super(name);
		_type = PlayerType.Human;
		this.gameUI = gameUI;
	}
	
	public void makePlay()
	{
		if(GameRules.isCueBallInHand) _manageCueBallInHand();
		else if(GameToolbox.mouseClicked && !gameUI.handleMouseClick() && cue.isDrawn() && !cue.isMoving()) {
			float velocity = gameUI.getCueStrength() * GameToolbox.maxCueBallVelocity;
			Vector2D delta = cue.getCueBallDelta();
			delta.normalize();
			delta.scale(velocity);
			if(delta.x != 0 || delta.y != 0) cue.setVelocity(delta);
		}
	}
	
	private void _manageCueBallInHand()
	{
		if(GameToolbox.mouseClicked) {
			if(_cueBallInHandCollisionCheck(_balls[0].getBoundingBox())) {
				GameRules.isCueBallInHand = false;
		        cue.update();
		        cue.startDrawing();
			}
		}
		else if(_cueBallInHandCollisionCheck(_getNewCueBallBox()))
			_balls[0].setLocation(GameToolbox.cursorX, GameToolbox.cursorY);
	}
	
	private boolean _cueBallInHandCollisionCheck(Rectangle2D.Float newCueBallBox)
	{
		if(_table.cueBallInHandCheck(newCueBallBox)) {
			for(int i = 1; i < _balls.length; ++i)
				if(Collider.circles(newCueBallBox, _balls[i].getBoundingBox()))
					return false;
			return true;
		}
		return false;
	}
	
	private Rectangle2D.Float _getNewCueBallBox()
	{
		return new Rectangle2D.Float(GameToolbox.cursorX - _balls[0].getRadius(), GameToolbox.cursorY - _balls[0].getRadius(),
										_balls[0].getWidth(), _balls[0].getHeight());
	}
}
