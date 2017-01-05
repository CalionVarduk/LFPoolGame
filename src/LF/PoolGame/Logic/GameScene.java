package LF.PoolGame.Logic;

import LF.PoolGame.InputOutcome;
import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.GameUI;
import LF.PoolGame.Drawable.GameObjects.PoolBall;
import LF.PoolGame.Drawable.GameObjects.PoolCue;
import LF.PoolGame.Drawable.GameObjects.PoolTable;
import LF.PoolGame.Drawable.Text.FoulBox;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

public final class GameScene extends Scene
{
	private GameUI gameUI;
	
	private Player player1;
	private Player player2;
	private Player currentPlayer;
	
	private PoolTable table;
	private PoolBall[] balls;
	
	public GameScene()
	{
		super();
		
		gameUI = new GameUI();
		table = new PoolTable(822, 411);
		
		balls = new PoolBall[16];
		for(int i = 0; i < balls.length; ++i)
			balls[i] = new PoolBall(GameToolbox.poolBallSize, i, balls.length);
		
		balls[0].setMainColor(MoreColors.whiteSmoke);
		balls[1].setMainColor(MoreColors.gold);
		balls[2].setMainColor(MoreColors.mediumBlue);
		balls[3].setMainColor(MoreColors.orangeRed);
		balls[4].setMainColor(MoreColors.purple);
		balls[5].setMainColor(MoreColors.darkOrange);
		balls[6].setMainColor(MoreColors.darkGreen);
		balls[7].setMainColor(MoreColors.saddleBrown);
		balls[8].setMainColor(Color.BLACK);
		balls[9].setMainColor(MoreColors.gold);
		balls[10].setMainColor(MoreColors.mediumBlue);
		balls[11].setMainColor(MoreColors.orangeRed);
		balls[12].setMainColor(MoreColors.purple);
		balls[13].setMainColor(MoreColors.darkOrange);
		balls[14].setMainColor(MoreColors.darkGreen);
        balls[15].setMainColor(MoreColors.saddleBrown);
	}
	
	public void init()
	{
		GameToolbox.pottedStartLocation.x = (GameToolbox.windowWidth - (15 * GameToolbox.poolBallSize + 70)) * 0.5f;
		GameToolbox.pottedStartLocation.y = GameToolbox.windowHeight - (GameToolbox.poolBallSize * 0.5f) - 10;
		
		_boundingBox.x = _boundingBox.y = 0;
		_boundingBox.width = GameToolbox.windowWidth;
		_boundingBox.height = GameToolbox.windowHeight;
	}
	
	public void reset(PlayerType player1Type, PlayerType player2Type)
	{		
		table.spawn(GameToolbox.windowWidth >> 1, 100 + (table.getTableHeight() >> 1));
		gameUI.spawn(table, 50);
		
		_resetPoolBalls(table.getHeadSpot(), table.getFootSpot());
		
		player1 = (player1Type == PlayerType.Human) ? new HumanPlayer("Human 1", gameUI) : new AIPlayer("AI", player1Type);
		player2 = (player2Type == PlayerType.Human) ? new HumanPlayer("Human 2", gameUI) : new AIPlayer("AI", player2Type);
		_resetPlayers();
		GameRules.reset();
        
        startUpdating();
		startDrawing();
		
		if(currentPlayer.getType() != PlayerType.Human) currentPlayer.makePlay();
	}
	
	public InputOutcome handleMouseClick()
	{		
		if(isUpdating() && !FoulBox.isDisplayed() && !GameRules.isGameEnded() && currentPlayer.getType() == PlayerType.Human)
			currentPlayer.makePlay();
		return InputOutcome.OK;
	}
	
	public InputOutcome handleMouseMove()
	{
		if(isUpdating() && currentPlayer.getType() == PlayerType.Human) currentPlayer.makePlay();
		return InputOutcome.OK;
	}
	
	public InputOutcome handleMouseDrag()
	{
		if(isUpdating() && currentPlayer.getType() == PlayerType.Human) gameUI.handleMouseClick();
		return InputOutcome.OK;
	}
	
	public InputOutcome handleKeyInput(int keyCode)
	{
		InputOutcome uiResult = gameUI.handleKeyInput(keyCode);
		
		if(uiResult == InputOutcome.Reset) {
			reset(player1.getType(), player2.getType());
			return InputOutcome.Unpause;
		}
		else if(uiResult == InputOutcome.ToMenu) return _backToMenu();
		else if(isUpdating() && uiResult == InputOutcome.OK) {
			if(keyCode == KeyEvent.VK_ENTER) {
				if(FoulBox.isDisplayed()) {				
					FoulBox.hide();
					if(GameRules.isGameEnded()) _gameEnded();
					else _gameContinuesAfterFaul();
				}
			}
			return InputOutcome.OK;
		}
		else return uiResult;
	}
	
	protected void updateScene()
	{		
		if(!GameRules.isCueBallInHand && !FoulBox.isDisplayed() && !GameRules.isGameEnded()) {
			if(currentPlayer.cue.isDrawn()) _updateCueLogic();
			else _updatePoolBallsLogic();
		}
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		table.draw(g2d);
		for(int i = 0; i < balls.length; ++i)
			if(!balls[i].isInPocket()) balls[i].draw(g2d);
		
		if(GameSettings.drawBoundingBoxes)
			_drawBoundingBoxes(g2d);
		
		if(GameSettings.drawVectors)
			_drawVectors(g2d);
		
		currentPlayer.cue.draw(g2d);
		gameUI.draw(g2d);
	}

	private void _drawBoundingBoxes(Graphics2D g2d)
	{
		currentPlayer.cue.drawBoundingBox(g2d);
		table.drawBoundingBoxes(g2d);		
		for(int i = 0; i < balls.length; ++i)
			balls[i].drawBoundingBox(g2d);
	}
	
	private void _drawVectors(Graphics2D g2d)
	{
		for(int i = 0; i < balls.length; ++i)
			if(!balls[i].isInPocket()) balls[i].drawVector(g2d);
		table.drawVectors(g2d);
	}
	
	private void _updateCueLogic()
	{
		currentPlayer.cue.update();
		
		if(currentPlayer.cue.isMoving())
			for(int time = 1; time < GameSettings.timeFractions; ++time)
				currentPlayer.cue.update();
	}
	
	private void _updatePoolBallsLogic()
	{
		for(int time = 0; time < GameSettings.timeFractions; ++time) {
			for(int i = 0; i < balls.length; ++i) balls[i].update();
			
			for(int i = 0; i < balls.length; ++i)
				for(int j = i + 1; j < balls.length; ++j)
					balls[i].collisionCheck(balls[j]);
			
			for(int i = 0; i < balls.length; ++i) table.collisionCheck(balls[i]);
		}
		
		_attemptToUpdateGameRules();
	}
	
	private void _attemptToUpdateGameRules()
	{
		if(_havePoolBallsStopped()) {
			PlayResult result = GameRules.update(currentPlayer, balls[0].isInPocket());
			if(result == PlayResult.BallTypesSet) _setOtherPlayerBallType();
			
			player1.calculatePottedCount();
			player2.calculatePottedCount();
			gameUI.updateBallTypes(player1.getBallType(), player2.getBallType());
			
			if(result.isDisplayable())
				FoulBox.display(currentPlayer.getName(), result, table.getLocation());
			else {
				if(result == PlayResult.ResetGame) reset(player1.getType(), player2.getType());
				else {
					if(result == PlayResult.SwitchPlayer) _switchPlayer();
			        currentPlayer.cue.update();
			        currentPlayer.cue.startDrawing();
			        
			        if(currentPlayer.getType() != PlayerType.Human) currentPlayer.makePlay();
				}
			}
		}
	}
	
	private boolean _havePoolBallsStopped()
	{
		for(int i = 0; i < balls.length; ++i)
			if(balls[i].getVelocity().getLengthSquared() > 0) return false;
		return true;
	}
	
	private void _setOtherPlayerBallType()
	{
		if(currentPlayer == player1)
			player2.setBallType((player1.getBallType() == BallType.Solids) ? BallType.Stripes : BallType.Solids);
		else player1.setBallType((player2.getBallType() == BallType.Solids) ? BallType.Stripes : BallType.Solids);
	}

	private void _switchPlayer()
	{
		currentPlayer = (currentPlayer == player1) ? player2 : player1;
		gameUI.switchPlayer();
	}
	
	private void _gameContinuesAfterFaul()
	{
		_switchPlayer();
		GameRules.isCueBallInHand = true;
		balls[0].setLocation(table.getHeadSpot());
		balls[0].pullOutOfPocket();
		balls[0].startDrawing();
		
		if(currentPlayer.getType() != PlayerType.Human) currentPlayer.makePlay();
	}
	
	private void _gameEnded()
	{
		PlayResult result = FoulBox.getRecentPlayResult();
		boolean isPlayer1Current = (currentPlayer == player1);
		boolean player1Won = (isPlayer1Current && (result == PlayResult.Won));
		boolean player2Lost = (!isPlayer1Current && (result != PlayResult.Won));
		gameUI.endGame(player1Won || player2Lost);
		if((isPlayer1Current && (result != PlayResult.Won)) || player2Lost) _switchPlayer();
	}
	
	private InputOutcome _backToMenu()
	{
		stopUpdating();
		stopDrawing();
		GameSettings.frictionOn = false;
		return InputOutcome.ToMenu;
	}
	
	private void _resetPlayers()
	{
		player1.cue = new PoolCue(balls[0], 450, 4.5f, 7, 50 - balls[0].getRadius(), MoreColors.rosyBrown, MoreColors.deepSkyBlue);
		player2.cue = new PoolCue(balls[0], 450, 4.5f, 7, 50 - balls[0].getRadius(), MoreColors.darkOrange, MoreColors.deepSkyBlue);
		player1.setup(table, balls);
		player2.setup(table, balls);
		
		if(player1.getType() == PlayerType.EasyAI) player1.setName("AI<easy> 1");
		else if(player1.getType() == PlayerType.MediumAI) player1.setName("AI<medium> 1");
		else if(player1.getType() == PlayerType.HardAI) player1.setName("AI<hard> 1");
		
		if(player2.getType() == PlayerType.EasyAI) player2.setName("AI<easy> 2");
		else if(player2.getType() == PlayerType.MediumAI) player2.setName("AI<medium> 2");
		else if(player2.getType() == PlayerType.HardAI) player2.setName("AI<hard> 2");
		
		currentPlayer = player1;
		currentPlayer.cue.setVelocity(0, 0);
		currentPlayer.cue.update();
		currentPlayer.cue.startDrawing();
		
		gameUI.setupPlayers(player1, player2);
	}
	
	private void _resetPoolBalls(Point2D.Float headSpot, Point2D.Float footSpot)
	{
		float ballSizeSqrt3 = GameToolbox.poolBallSize * 1.7320508f;
		float ballRadius = GameToolbox.poolBallSize * 0.5f;
		
		balls[0].spawn(headSpot.x, headSpot.y);
		
		balls[9].spawn(footSpot.x, footSpot.y);
		
		balls[10].spawn(footSpot.x + (ballSizeSqrt3 * 0.5f) + 2, footSpot.y - ballRadius - 1);
		balls[3].spawn(footSpot.x + (ballSizeSqrt3 * 0.5f) + 2, footSpot.y + ballRadius + 1);
		
		balls[1].spawn(footSpot.x + ballSizeSqrt3 + 4, footSpot.y - GameToolbox.poolBallSize - 1);
		balls[8].spawn(footSpot.x + ballSizeSqrt3 + 4, footSpot.y);
		balls[12].spawn(footSpot.x + ballSizeSqrt3 + 4, footSpot.y + GameToolbox.poolBallSize + 1);
		
		balls[11].spawn(footSpot.x + (ballSizeSqrt3 * 1.5f) + 6, footSpot.y - (ballRadius * 3) - 4);
		balls[4].spawn(footSpot.x + (ballSizeSqrt3 * 1.5f) + 6, footSpot.y - ballRadius - 2);
		balls[13].spawn(footSpot.x + (ballSizeSqrt3 * 1.5f) + 6, footSpot.y + ballRadius + 2);
		balls[6].spawn(footSpot.x + (ballSizeSqrt3 * 1.5f) + 6, footSpot.y + (ballRadius * 3) + 4);
		
		balls[2].spawn(footSpot.x + (ballSizeSqrt3 * 2) + 8, footSpot.y - (GameToolbox.poolBallSize << 1) - 2);
		balls[5].spawn(footSpot.x + (ballSizeSqrt3 * 2) + 8, footSpot.y - GameToolbox.poolBallSize - 1);
		balls[14].spawn(footSpot.x + (ballSizeSqrt3 * 2) + 8, footSpot.y);
        balls[7].spawn(footSpot.x + (ballSizeSqrt3 * 2) + 8, footSpot.y + GameToolbox.poolBallSize + 1);
        balls[15].spawn(footSpot.x + (ballSizeSqrt3 * 2) + 8, footSpot.y + (GameToolbox.poolBallSize << 1) + 2);
        
		for(int i = 0; i < balls.length; ++i) balls[i].setVelocity(0, 0);
	}
}
