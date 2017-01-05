package LF.PoolGame.Drawable;

import LF.PoolGame.InputOutcome;
import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.GameObjects.PoolTable;
import LF.PoolGame.Drawable.Text.*;
import LF.PoolGame.Logic.BallType;
import LF.PoolGame.Logic.GameRules;
import LF.PoolGame.Logic.GameToolbox;
import LF.PoolGame.Logic.Player;
import LF.PoolGame.Math.Collider;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class GameUI extends DrawableObject
{
	private CueGauge _gauge;
	private Rectangle2D.Float _mainLine;
	private Path2D.Float _pottedFrame;
	private DrawableString _gaugeTitle;
	private DrawableString _pottedTitle;
	private MenuString _player1;
	private MenuString _player2;
	private DrawableString _player1BallType;
	private DrawableString _player2BallType;
	private DrawableString _cueBallInHand;
	
	private DrawableString _pauseTitle;
	private MenuStringList _pauseMenu;
	private Rectangle2D.Float _pauseBox;
	private Path2D.Float _pauseFrame;

	public GameUI()
	{
		super();
		setMainColor(Color.BLACK);
		_mainLine = new Rectangle2D.Float();
		_pottedFrame = new Path2D.Float();
		
		_pottedTitle = new DrawableString("Potted Pool Balls:", MoreColors.deepSkyBlue, new Font("Arial", Font.BOLD | Font.ITALIC, 20));
		_gaugeTitle = new DrawableString("Cue Strength Gauge:", MoreColors.deepSkyBlue, _pottedTitle.getFont());
		
		_player1 = new MenuString("Player 1:", MoreColors.silver, MoreColors.whiteSmoke, _pottedTitle.getFont());
		_player2 = new MenuString("Player 2:", MoreColors.silver, MoreColors.whiteSmoke, _pottedTitle.getFont());
		_player1BallType = new DrawableString("open table", MoreColors.whiteSmoke, _pottedTitle.getFont());
		_player2BallType = new DrawableString("open table", MoreColors.lightSteelBlue, _pottedTitle.getFont());
		
		_cueBallInHand = new DrawableString("Cue Ball in Hand", MoreColors.greenTransparent, _pottedTitle.getFont());
		
		_gauge = new CueGauge(300, 30, MoreColors.whiteSmoke, MoreColors.deepSkyBlue);
		
		_pauseTitle = new DrawableString("Pause", MoreColors.gold, new Font("Arial", Font.BOLD, 30));
		
		_pauseMenu = new MenuStringList(20, _pauseTitle.getFont());
		_pauseMenu.addMenuString("Back to Game", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		_pauseMenu.addMenuString("Reset Game", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		_pauseMenu.addMenuString("Back to Menu", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		
		_pauseBox = new Rectangle2D.Float(0, 0, _pauseMenu.getWidth() + 80, _pauseMenu.getHeight() + 50);
		_pauseFrame = new Path2D.Float();
	}
	
	public void spawn(PoolTable table, float offset)
	{
		_boundingBox.x = 0;
		_boundingBox.y = table.getBottom() + offset;
		_boundingBox.width = GameToolbox.windowWidth;
		_boundingBox.height = GameToolbox.windowHeight - _boundingBox.y;
		
		_mainLine.x = 0;
		_mainLine.y = _boundingBox.y - 1;
		_mainLine.width = _boundingBox.width;
		_mainLine.height = 3;
		
		float frameLeft = GameToolbox.pottedStartLocation.x - (GameToolbox.poolBallSize * 0.5f) - 6;
		float frameRight = GameToolbox.pottedStartLocation.x + (15 * GameToolbox.poolBallSize) + 76;
		float frameTop = GameToolbox.pottedStartLocation.y - (GameToolbox.poolBallSize * 0.5f) - 11;
		getRectangleFrame(_pottedFrame, new Rectangle2D.Float(frameLeft, frameTop, frameRight - frameLeft, getBottom() - frameTop + 3), 3);
		
		_spawnStrings(frameTop, table.getLocationY());
		_spawnPauseBox();
		
		_gauge.spawn(GameToolbox.windowWidth * 0.5f, _gaugeTitle.getBottom() + (_gauge.getHeight() * 0.5f) + 10);
		
		startDrawing();
	}
	
	public void setupPlayers(Player player1, Player player2)
	{
		_player1.setString(player1.getName() + ":");
		_player2.setString(player2.getName() + ":");
		_player1BallType.setString(player1.getBallType().type);
		_player2BallType.setString(player2.getBallType().type);
		
		_player1.spawnLeft(15, 0);
		_player2.spawnLeft(15, 0);
		_player1.setLocationY(_boundingBox.y + _boundingBox.height * 0.25f);
		_player2.setLocationY(_player1.getBottom() + 30);
		
		float typeLoc = Math.max(_player1.getRight(), _player2.getRight());
		_player1BallType.spawnLeft(typeLoc + 15, _player1.getTop());
		_player2BallType.spawnLeft(typeLoc + 15, _player2.getTop());
		_player1.select();
		_player2.deselect();
		_player1BallType.setMainColor(MoreColors.whiteSmoke);
		_player2BallType.setMainColor(MoreColors.lightSteelBlue);
	}
	
	public void endGame(boolean player1Won)
	{
		if(player1Won) {
			_player1BallType.setString("winner");
			_player2BallType.setString("loser");
		}
		else {
			_player1BallType.setString("loser");
			_player2BallType.setString("winner");
		}
	}
	
	public boolean handleMouseClick()
	{
		if(!_pauseMenu.isDrawn()) {
			if(Collider.rectanglePoint(_boundingBox, new Point2D.Float(GameToolbox.cursorX, GameToolbox.cursorY))) {
				_gauge.trySetCueStrength();
				return true;
			}
		}
		return false;
	}
	
	public InputOutcome handleKeyInput(int keyCode)
	{
		if(_pauseMenu.isDrawn()) {
			if(keyCode == KeyEvent.VK_ENTER) {
				int selected = _pauseMenu.getFirstSelected();
				_pauseMenu.stopDrawing();
				_pauseTitle.stopDrawing();
				return (selected == 2) ? InputOutcome.ToMenu : (selected == 1) ? InputOutcome.Reset : InputOutcome.Unpause;
			}
			else if(keyCode == KeyEvent.VK_ESCAPE) {
				_pauseMenu.stopDrawing();
				_pauseTitle.stopDrawing();
				return InputOutcome.Unpause;
			}
			else if(keyCode == KeyEvent.VK_UP) _pauseMenu.selectPrevious();
			else if(keyCode == KeyEvent.VK_DOWN) _pauseMenu.selectNext();
		}
		else if(keyCode == KeyEvent.VK_ESCAPE) {
			_pauseMenu.deselectAll();
			_pauseMenu.select(0);
			_pauseMenu.startDrawing();
			_pauseTitle.startDrawing();
			return InputOutcome.Pause;
		}
		return InputOutcome.OK;
	}
	
	public float getCueStrength()
	{
		return _gauge.getCueStrength();
	}
	
	public void switchPlayer()
	{
		if(_player1.isSelected()) {
			_player1.deselect();
			_player2.select();
			_player1BallType.setMainColor(MoreColors.lightSteelBlue);
			_player2BallType.setMainColor(MoreColors.whiteSmoke);
		}
		else {
			_player1.select();
			_player2.deselect();
			_player1BallType.setMainColor(MoreColors.whiteSmoke);
			_player2BallType.setMainColor(MoreColors.lightSteelBlue);
		}
	}
	
	public void updateBallTypes(BallType player1, BallType player2)
	{
		_player1BallType.setString(player1.type);
		_player2BallType.setString(player2.type);
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		g2d.setColor(_mainColor);
		g2d.fill(_boundingBox);
		g2d.setColor(MoreColors.deepSkyBlue);
		g2d.fill(_mainLine);
		g2d.fill(_pottedFrame);
		
		_pottedTitle.draw(g2d);
		_gaugeTitle.draw(g2d);
		_gauge.draw(g2d);
		
		_player1.draw(g2d);
		_player2.draw(g2d);
		_player1BallType.draw(g2d);
		_player2BallType.draw(g2d);
		
		FoulBox.draw(g2d);
		if(GameRules.isCueBallInHand) _cueBallInHand.draw(g2d);
		
		for(int i = 0; i < GameRules.pottedPoolBalls.size(); ++i)
			GameRules.pottedPoolBalls.get(i).draw(g2d);
		
		if(_pauseMenu.isDrawn()) _drawPauseBox(g2d);
	}
	
	private void _drawPauseBox(Graphics2D g2d)
	{
		g2d.setColor(MoreColors.darkTransparent);
		g2d.fillRect(0, 0, GameToolbox.windowWidth, GameToolbox.windowHeight);
		
		g2d.setColor(Color.BLACK);
		g2d.fill(_pauseBox);
		g2d.setColor(MoreColors.gold);
		g2d.fill(_pauseFrame);
		
		_pauseTitle.draw(g2d);
		_pauseMenu.draw(g2d);
	}
	
	private void _spawnStrings(float pottedFrameTop, float tableLocationY)
	{
		_pottedTitle.spawnCentre(GameToolbox.windowWidth * 0.5f, 0);
		_pottedTitle.setLocationY(pottedFrameTop - _pottedTitle.getHeight());
		_gaugeTitle.spawnCentre(GameToolbox.windowWidth * 0.5f, getTop() + 10);
		
		_player1.spawnLeft(15, 0);
		_player2.spawnLeft(15, 0);
		_player1.setLocationY(_boundingBox.y + _boundingBox.height * 0.25f);
		_player2.setLocationY(_player1.getBottom() + 30);
		_player1BallType.spawnLeft(_player1.getRight() + 15, _player1.getTop());
		_player2BallType.spawnLeft(_player1.getRight() + 15, _player2.getTop());
		_player1.select();
		
		_cueBallInHand.spawnCentre(GameToolbox.windowWidth * 0.5f, 0);
		_cueBallInHand.setLocationY(tableLocationY);
	}
	
	private void _spawnPauseBox()
	{
		_pauseTitle.spawnCentre(0, 0);
		_pauseMenu.spawnCentre(0, 0);
		
		_pauseMenu.setLocation(GameToolbox.windowWidth * 0.5f, GameToolbox.windowHeight * 0.5f);
		_pauseMenu.deselectAll();
		_pauseMenu.select(0);
		_pauseTitle.setLocation(_pauseMenu.getLocationX(), _pauseMenu.getTop() - (_pauseTitle.getHeight() * 0.5f) - _pauseMenu.getSpacing() - 10);
		
		_pauseBox.width = _pauseMenu.getWidth() + 80;
		_pauseBox.height = _pauseMenu.getBottom() - _pauseTitle.getTop() + 50;
		_pauseBox.x = _pauseMenu.getLeft() - 40;
		_pauseBox.y = _pauseTitle.getTop() - 25;
		getRectangleFrame(_pauseFrame, _pauseBox, 5);
		
		_pauseMenu.stopDrawing();
		_pauseTitle.stopDrawing();
	}
}
