package LF.PoolGame.Logic;

import LF.PoolGame.InputOutcome;
import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.GameObjects.PoolBall;
import LF.PoolGame.Drawable.GameObjects.RoundBumper;
import LF.PoolGame.Drawable.GameObjects.SegmentBumper;
import LF.PoolGame.Drawable.Text.*;
import LF.PoolGame.Math.Collider;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public final class MenuScene extends Scene
{
	private static final int inMainMenu = 0;
	private static final int inStartGameMenu = 1;
	private static final int inSettingsMenu = 2;
	
	private int mode;
	
	private DrawableString title;
	private DrawableString version;
	private DrawableString author;
	
	private MenuStringList mainMenu;
	
	private DrawableString startGameTitle;
	private MenuStringList startGameMenu;
	private MenuStringList startGameStateMenu;
	
	private DrawableString settingsTitle;
	private MenuStringList settingsMenu;
	private MenuStringList settingsStateMenu;
	
	private Random rng;
	private int ballCount;
	private ArrayList<PoolBall> balls;
	private RoundBumper[] roundBumpers;
	private SegmentBumper[] segmentBumpers;
	
	public MenuScene()
	{
		super();
		_constructBackground();
		_constructStrings();
	}
	
	public void init()
	{
		_boundingBox.x = _boundingBox.y = 0;
		_boundingBox.width = GameToolbox.windowWidth;
		_boundingBox.height = GameToolbox.windowHeight;
		
		_initBackground();
		_initStrings();		
		startUpdating();
		startDrawing();
	}
	
	public PlayerType getPlayer1Type()
	{
		if(startGameStateMenu.getMenuString(0).getString().equalsIgnoreCase("Human"))
			return PlayerType.Human;
		else if(startGameStateMenu.getMenuString(0).getString().equalsIgnoreCase("Easy AI"))
			return PlayerType.EasyAI;
		else if(startGameStateMenu.getMenuString(0).getString().equalsIgnoreCase("Medium AI"))
			return PlayerType.MediumAI;
		
		return PlayerType.HardAI;
	}
	
	public PlayerType getPlayer2Type()
	{
		if(startGameStateMenu.getMenuString(1).getString().equalsIgnoreCase("Human"))
			return PlayerType.Human;
		else if(startGameStateMenu.getMenuString(1).getString().equalsIgnoreCase("Easy AI"))
			return PlayerType.EasyAI;
		else if(startGameStateMenu.getMenuString(1).getString().equalsIgnoreCase("Medium AI"))
			return PlayerType.MediumAI;
		
		return PlayerType.HardAI;
	}
	
	public InputOutcome handleKeyInput(int keyCode)
	{
		if(keyCode == KeyEvent.VK_DOWN) _selectNext();
		else if(keyCode == KeyEvent.VK_UP) _selectPrevious();
		else if(keyCode == KeyEvent.VK_ENTER) return _confirmChoice();
		else if(keyCode == KeyEvent.VK_ESCAPE) _toPreviousMenu();
		else if(keyCode == KeyEvent.VK_LEFT) _changeToLeft();
		else if(keyCode == KeyEvent.VK_RIGHT) _changeToRight();
		return InputOutcome.OK;
	}
	
	protected void updateScene()
	{		
		for(int time = 0; time < GameSettings.timeFractions; ++time) {
			for(int i = 0; i < ballCount; ++i)
				balls.get(i).update();
			
			for(int i = 0; i < ballCount; ++i)
				for(int j = 0; j < segmentBumpers.length; ++j)
					balls.get(i).collisionCheck(segmentBumpers[j]);
			
			for(int i = 0; i < ballCount; ++i)
				for(int j = 0; j < roundBumpers.length; ++j)
					balls.get(i).collisionCheck(roundBumpers[j]);
			
			for(int i = 0; i < ballCount; ++i)
				for(int j = i + 1; j < ballCount; ++j)
					balls.get(i).collisionCheck(balls.get(j));
		}
	}
	
	protected void drawObject(Graphics2D g2d)
	{
		_drawBackground(g2d);
		_drawStrings(g2d);
	}
	
	private void _drawStrings(Graphics2D g2d)
	{
		title.draw(g2d);
		version.draw(g2d);
		author.draw(g2d);
		
		mainMenu.draw(g2d);
		
		startGameTitle.draw(g2d);
		startGameMenu.draw(g2d);
		startGameStateMenu.draw(g2d);
		
		settingsTitle.draw(g2d);
		settingsMenu.draw(g2d);
		settingsStateMenu.draw(g2d);
	}
	
	private void _drawBackground(Graphics2D g2d)
	{
		for(int i = 0; i < segmentBumpers.length; ++i)
			segmentBumpers[i].draw(g2d);
		
		for(int i = 0; i < roundBumpers.length; ++i)
			roundBumpers[i].draw(g2d);
		
		for(int i = 0; i < ballCount; ++i)
			balls.get(i).draw(g2d);
		
		if(GameSettings.drawBoundingBoxes)
			_drawBackgroundBoundingBoxes(g2d);
		
		if(GameSettings.drawVectors)
			_drawBackgroundVectors(g2d);
	}
	
	private void _drawBackgroundBoundingBoxes(Graphics2D g2d)
	{
		for(int i = 0; i < segmentBumpers.length; ++i)
			segmentBumpers[i].drawBoundingBox(g2d);
		
		for(int i = 0; i < roundBumpers.length; ++i)
			roundBumpers[i].drawBoundingBox(g2d);
		
		for(int i = 0; i < ballCount; ++i)
			balls.get(i).drawBoundingBox(g2d);
	}
	
	private void _drawBackgroundVectors(Graphics2D g2d)
	{
		for(int i = 0; i < ballCount; ++i)
			balls.get(i).drawVector(g2d);
		
		for(int i = 0; i < segmentBumpers.length; ++i)
			segmentBumpers[i].drawVector(g2d);
	}
	
	private void _spawnBackgroundBall(int i)
	{
		balls.get(i).setVelocity((rng.nextFloat() * 6) - 3, (rng.nextFloat() * 6) - 3);
		for(int j = 0; j < i; ++j) balls.get(j).setType(i + 2);
		
		while(true) {
			boolean correctSpawn = true;
			balls.get(i).spawn(rng.nextInt(GameToolbox.windowWidth - 40) + 20, rng.nextInt(GameToolbox.windowHeight - 40) + 20);
			
			for(int j = 0; j < roundBumpers.length; ++j)
				if(Collider.circles(balls.get(i).getBoundingBox(), roundBumpers[j].getBoundingBox()))
					{ correctSpawn = false; break; }
			
			if(correctSpawn) {
				for(int j = 0; j < segmentBumpers.length; ++j)
					if(Collider.rectangles(balls.get(i).getBoundingBox(), segmentBumpers[j].getBoundingBox()))
						{ correctSpawn = false; break; }
				
				if(correctSpawn) {
					for(int j = 0; j < i; ++j)
						if(Collider.circles(balls.get(i).getBoundingBox(), balls.get(j).getBoundingBox()))
							{ correctSpawn = false; break; }
					
					if(correctSpawn) break;
				}
			}
		}		
	}
	
	private void _selectPrevious()
	{
		if(mode == inMainMenu) mainMenu.selectPrevious();
		else if(mode == inStartGameMenu) startGameMenu.selectPrevious();
		else if(mode == inSettingsMenu) settingsMenu.selectPrevious();
	}
	
	private void _selectNext()
	{
		if(mode == inMainMenu) mainMenu.selectNext();
		else if(mode == inStartGameMenu) startGameMenu.selectNext();
		else if(mode == inSettingsMenu) settingsMenu.selectNext();
	}
	
	private void _changeToLeft()
	{
		if(mode == inStartGameMenu) _previousPlayerType();
		else if(mode == inSettingsMenu) _decreaseBallCount();
	}
	
	private void _changeToRight()
	{
		if(mode == inStartGameMenu) _nextPlayerType();
		else if(mode == inSettingsMenu) _increaseBallCount();
	}
	
	private void _previousPlayerType()
	{
		if(startGameMenu.isSelected(0)) {
			PlayerType player1 = getPlayer1Type();
			if(player1 == PlayerType.Human)
				startGameStateMenu.getMenuString(0).setString("Hard AI");
			else if(player1 == PlayerType.EasyAI)
				startGameStateMenu.getMenuString(0).setString("Human");
			else if(player1 == PlayerType.MediumAI)
				startGameStateMenu.getMenuString(0).setString("Easy AI");
			else startGameStateMenu.getMenuString(0).setString("Medium AI");
		}
		else if(startGameMenu.isSelected(1)) {
			PlayerType player2 = getPlayer2Type();
			if(player2 == PlayerType.Human)
				startGameStateMenu.getMenuString(1).setString("Hard AI");
			else if(player2 == PlayerType.EasyAI)
				startGameStateMenu.getMenuString(1).setString("Human");
			else if(player2 == PlayerType.MediumAI)
				startGameStateMenu.getMenuString(1).setString("Easy AI");
			else startGameStateMenu.getMenuString(1).setString("Medium AI");
		}
	}
	
	private void _nextPlayerType()
	{
		if(startGameMenu.isSelected(0)) {
			PlayerType player1 = getPlayer1Type();
			if(player1 == PlayerType.Human)
				startGameStateMenu.getMenuString(0).setString("Easy AI");
			else if(player1 == PlayerType.EasyAI)
				startGameStateMenu.getMenuString(0).setString("Medium AI");
			else if(player1 == PlayerType.MediumAI)
				startGameStateMenu.getMenuString(0).setString("Hard AI");
			else startGameStateMenu.getMenuString(0).setString("Human");
		}
		else if(startGameMenu.isSelected(1)) {
			PlayerType player2 = getPlayer2Type();
			if(player2 == PlayerType.Human)
				startGameStateMenu.getMenuString(1).setString("Easy AI");
			else if(player2 == PlayerType.EasyAI)
				startGameStateMenu.getMenuString(1).setString("Medium AI");
			else if(player2 == PlayerType.MediumAI)
				startGameStateMenu.getMenuString(1).setString("Hard AI");
			else startGameStateMenu.getMenuString(1).setString("Human");
		}
	}
	
	private void _decreaseBallCount()
	{
		if(settingsMenu.isSelected(0)) {
			if(ballCount > 0) {
				balls.remove(--ballCount);
				for(int i = 0; i < ballCount; ++i) balls.get(i).setType(ballCount + 1);
				settingsStateMenu.getMenuString(0).setString(Integer.toString(ballCount));
			}
		}
	}
	
	private void _increaseBallCount()
	{
		if(settingsMenu.isSelected(0)) {
			if(ballCount < 999) {
				balls.add(new PoolBall(GameToolbox.poolBallSize, ballCount + 1, ballCount + 2, MoreColors.steelBlue));
				_spawnBackgroundBall(ballCount);
				settingsStateMenu.getMenuString(0).setString(Integer.toString(++ballCount));
			}
		}
	}
	
	private InputOutcome _confirmChoice()
	{
		if(mode == inMainMenu) _mainConfirmChoice();
		else if(mode == inStartGameMenu) return _startGameConfirmChoice();
		else if(mode == inSettingsMenu) _settingsConfirmChoice();
		return InputOutcome.OK;
	}
	
	private void _toPreviousMenu()
	{
		if(mode == inStartGameMenu) {
			mainMenu.startDrawing();
			startGameTitle.stopDrawing();
			startGameMenu.stopDrawing();
			startGameStateMenu.stopDrawing();
			startGameMenu.deselectAll();
			mode = inMainMenu;
		}
		else if(mode == inSettingsMenu) {
			mainMenu.startDrawing();
			settingsTitle.stopDrawing();
			settingsMenu.stopDrawing();
			settingsStateMenu.stopDrawing();
			settingsMenu.deselectAll();
			mode = inMainMenu;
		}
	}
	
	private void _mainConfirmChoice()
	{
		int selected = mainMenu.getFirstSelected();
		if(selected == 0) {
			mainMenu.stopDrawing();
			startGameTitle.startDrawing();
			startGameMenu.startDrawing();
			startGameStateMenu.startDrawing();
			startGameMenu.select(2);
			mode = inStartGameMenu;
		}
		else if(selected == 1) {
			mainMenu.stopDrawing();
			settingsTitle.startDrawing();
			settingsMenu.startDrawing();
			settingsStateMenu.startDrawing();
			settingsMenu.select(6);
			mode = inSettingsMenu;
		}
		else System.exit(0);
	}
	
	private InputOutcome _startGameConfirmChoice()
	{
		int selected = startGameMenu.getFirstSelected();
		if(selected == 2) {
			stopUpdating();
			stopDrawing();
			mainMenu.startDrawing();
			startGameTitle.stopDrawing();
			startGameMenu.stopDrawing();
			startGameStateMenu.stopDrawing();
			startGameMenu.deselectAll();
			GameSettings.frictionOn = GameSettings.gameFrictionOn;
			mode = inMainMenu;
			return InputOutcome.ToGame;
		}
		else if(selected == 3) {
			mainMenu.startDrawing();
			startGameTitle.stopDrawing();
			startGameMenu.stopDrawing();
			startGameStateMenu.stopDrawing();
			startGameMenu.deselectAll();
			mode = inMainMenu;
		}
		return InputOutcome.OK;
	}
	
	private void _settingsConfirmChoice()
	{
		int selected = settingsMenu.getFirstSelected();
		if(selected == 1) {
			GameSettings.drawBoundingBoxes = !GameSettings.drawBoundingBoxes;
			settingsStateMenu.getMenuString(selected).setString((GameSettings.drawBoundingBoxes) ? "On" : "Off");
		}
		else if(selected == 2) {
			GameSettings.drawVectors = !GameSettings.drawVectors;
			settingsStateMenu.getMenuString(selected).setString((GameSettings.drawVectors) ? "On" : "Off");
		}
		else if(selected == 3) {
			GameSettings.displayFps = !GameSettings.displayFps;
			settingsStateMenu.getMenuString(selected).setString((GameSettings.displayFps) ? "On" : "Off");
		}
		else if(selected == 4) {
			GameSettings.antiAliasingOn = !GameSettings.antiAliasingOn;
			settingsStateMenu.getMenuString(selected).setString((GameSettings.antiAliasingOn) ? "On" : "Off");
		}
		else if(selected == 5) {
			GameSettings.gameFrictionOn = !GameSettings.gameFrictionOn;
			settingsStateMenu.getMenuString(selected).setString((GameSettings.gameFrictionOn) ? "On" : "Off");
		}
		else if(selected == 6) {
			mainMenu.startDrawing();
			settingsTitle.stopDrawing();
			settingsMenu.stopDrawing();
			settingsStateMenu.stopDrawing();
			mode = inMainMenu;
		}
	}
	
	private void _initStrings()
	{
		float halfWidth = GameToolbox.windowWidth * 0.5f;
		
		title.spawnCentre(halfWidth, GameToolbox.windowHeight * 0.125f);
		version.spawnLeft(title.getLeft() + 20, (int)title.getBottom());
		author.spawnLeft(10, GameToolbox.windowHeight);
		author.offsetLocationY(-author.getHeight() - 10);
		
		mainMenu.spawnCentre(halfWidth, title.getBottom() + 130);
		
		startGameTitle.spawnCentre(halfWidth, title.getBottom() + 80);
		startGameMenu.spawnCentre(halfWidth, startGameTitle.getBottom() + 35);
		startGameStateMenu.spawnLeft(0, startGameMenu.getTop());
		startGameMenu.getMenuString(2).spawnCentre(halfWidth, startGameMenu.getMenuString(1).getBottom() + startGameMenu.getSpacing() + 20);
		startGameMenu.getMenuString(3).spawnCentre(halfWidth, startGameMenu.getMenuString(2).getBottom() + startGameMenu.getSpacing());
		
		for(int i = 0; i < 2; ++i) {
			MenuString title = startGameMenu.getMenuString(i);
			MenuString state = startGameStateMenu.getMenuString(i);
			title.offsetLocationX(-(state.getWidth() * 0.5f) - 5);
			state.setLocationX(title.getRight() + (state.getWidth() * 0.5f) + 10);
		}
		
		startGameMenu.fixBoundingBox();
		startGameStateMenu.fixBoundingBox();
		
		startGameTitle.stopDrawing();
		startGameMenu.stopDrawing();
		startGameStateMenu.stopDrawing();
		startGameMenu.select(2);
		
		settingsTitle.spawnCentre(halfWidth, title.getBottom() + 80);
		settingsMenu.spawnCentre(halfWidth, settingsTitle.getBottom() + 35);
		settingsStateMenu.spawnLeft(0, settingsMenu.getTop());
		settingsMenu.getMenuString(6).setFont(mainMenu.getFont());
		settingsMenu.getMenuString(6).spawnCentre(halfWidth, GameToolbox.windowHeight - (GameToolbox.windowHeight >> 2));
		
		for(int i = 0; i < 6; ++i) {
			MenuString title = settingsMenu.getMenuString(i);
			MenuString state = settingsStateMenu.getMenuString(i);
			title.offsetLocationX(-(state.getWidth() * 0.5f) - 5);
			state.setLocationX(title.getRight() + (state.getWidth() * 0.5f) + 10);
		}
		
		settingsMenu.fixBoundingBox();
		settingsStateMenu.fixBoundingBox();
		
		settingsTitle.stopDrawing();
		settingsMenu.stopDrawing();
		settingsStateMenu.stopDrawing();
		mainMenu.select(0);
	}
	
	private void _initBackground()
	{
		roundBumpers[0].spawn(GameToolbox.windowWidth * 0.25f, GameToolbox.windowHeight * 0.25f);
		roundBumpers[1].spawn(GameToolbox.windowWidth * 0.25f, GameToolbox.windowHeight * 0.75f);
		roundBumpers[2].spawn(GameToolbox.windowWidth * 0.75f, GameToolbox.windowHeight * 0.25f);
		roundBumpers[3].spawn(GameToolbox.windowWidth * 0.75f, GameToolbox.windowHeight * 0.75f);
		
		segmentBumpers[0].spawn(0, 5, GameToolbox.windowWidth, 5);
		segmentBumpers[1].spawn(GameToolbox.windowWidth - 5, 0, GameToolbox.windowWidth - 5, GameToolbox.windowHeight);
		segmentBumpers[2].spawn(GameToolbox.windowWidth, GameToolbox.windowHeight - 5, 0, GameToolbox.windowHeight - 5);
		segmentBumpers[3].spawn(5, GameToolbox.windowHeight, 5, 0);
		
		for(int i = 0; i < ballCount; ++i) _spawnBackgroundBall(i);
	}
	
	private void _constructStrings()
	{
		mode = inMainMenu;
		
		title = new DrawableString("Pool Game", MoreColors.gold, new Font("Arial", Font.BOLD | Font.ITALIC, 60));
		version = new DrawableString("v. 0.5", MoreColors.gold, new Font("Arial", Font.BOLD | Font.ITALIC, 15));
		author = new DrawableString("Author: £ukasz Furlepa (2016)", MoreColors.silver, version.getFont());
		
		mainMenu = new MenuStringList(30, new Font("Arial", Font.BOLD, 40));
		mainMenu.addMenuString("Start Game", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		mainMenu.addMenuString("Settings", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		mainMenu.addMenuString("Exit Game", MoreColors.silver, MoreColors.whiteSmoke);
		
		startGameTitle = new DrawableString("Start Game", MoreColors.gold, mainMenu.getFont());
		startGameMenu = new MenuStringList(20, mainMenu.getFont());
		startGameMenu.addMenuString("Player 1: ", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		startGameMenu.addMenuString("Player 2: ", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		startGameMenu.addMenuString("Start!", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		startGameMenu.addMenuString("Back to Menu", MoreColors.silver, MoreColors.whiteSmoke);
		
		startGameStateMenu = new MenuStringList(20, mainMenu.getFont());
		startGameStateMenu.addMenuString("Human", MoreColors.silver, MoreColors.silver);
		startGameStateMenu.addMenuString("Human", MoreColors.silver, MoreColors.silver);
		
		settingsTitle = new DrawableString("Settings", MoreColors.gold, mainMenu.getFont());
		settingsMenu = new MenuStringList(15, new Font("Arial", Font.BOLD, 18));
		settingsMenu.addMenuString("Pool balls in menu screen:", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		settingsMenu.addMenuString("Draw bounding boxes:", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		settingsMenu.addMenuString("Draw vectors:", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		settingsMenu.addMenuString("Display FPS:", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		settingsMenu.addMenuString("Anti-aliasing:", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		settingsMenu.addMenuString("Friction:", MoreColors.paleGoldenrod, MoreColors.whiteSmoke);
		settingsMenu.addMenuString("Back to Menu", MoreColors.silver, MoreColors.whiteSmoke);
		
		settingsStateMenu = new MenuStringList(15, settingsMenu.getFont());
		settingsStateMenu.addMenuString(Integer.toString(balls.size()), MoreColors.silver, MoreColors.whiteSmoke);
		settingsStateMenu.addMenuString((GameSettings.drawBoundingBoxes) ? "On" : "Off", MoreColors.silver, MoreColors.silver);
		settingsStateMenu.addMenuString((GameSettings.drawVectors) ? "On" : "Off", MoreColors.silver, MoreColors.silver);
		settingsStateMenu.addMenuString((GameSettings.displayFps) ? "On" : "Off", MoreColors.silver, MoreColors.silver);
		settingsStateMenu.addMenuString((GameSettings.antiAliasingOn) ? "On" : "Off", MoreColors.silver, MoreColors.silver);
		settingsStateMenu.addMenuString((GameSettings.gameFrictionOn) ? "On" : "Off", MoreColors.silver, MoreColors.silver);
	}
	
	private void _constructBackground()
	{
		rng = new Random(System.currentTimeMillis());
		
		roundBumpers = new RoundBumper[4];
		for(int i = 0; i < roundBumpers.length; ++i)
			roundBumpers[i] = new RoundBumper(90, MoreColors.darkGreenBumper);
		
		segmentBumpers = new SegmentBumper[4];
		for(int i = 0; i < segmentBumpers.length; ++i)
			segmentBumpers[i] = new SegmentBumper(6, MoreColors.darkGreenBumper);
		
		ballCount  = 16;
		balls = new ArrayList<PoolBall>(ballCount);
		for(int i = 0; i < ballCount; ++i)
			balls.add(new PoolBall(GameToolbox.poolBallSize, i + 1, ballCount + 1, MoreColors.steelBlue));
	}
}
