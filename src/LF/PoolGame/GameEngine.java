package LF.PoolGame;

import LF.PoolGame.GamePanel;
import LF.PoolGame.Drawable.Text.DrawableString;
import LF.PoolGame.Drawable.Text.FoulBox;
import LF.PoolGame.Logic.*;
import LF.PoolGame.FpsCounter;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class GameEngine extends GameLoop
{
	private GamePanel gamePanel;
	private FpsCounter fpsCounter;
	private DrawableString fpsString;
	
	private Scene currentScene;
	private MenuScene menuScene;
	private GameScene gameScene;
	   
	public GameEngine(GamePanel gamePanel)
	{
		super(60);
		this.gamePanel = gamePanel;
		
		menuScene = new MenuScene();
		gameScene = new GameScene();
		fpsString = new DrawableString("FPS: ", MoreColors.whiteSmoke, new Font("Arial", Font.PLAIN, 15));
		currentScene = menuScene;
		
		fpsCounter = new FpsCounter();
	}
	
	public void init()
	{
		GameToolbox.graphics = (Graphics2D)gamePanel.getGraphics();
		GameToolbox.windowWidth = gamePanel.getWidth();
		GameToolbox.windowHeight = gamePanel.getHeight();
		FoulBox.init();
		
		fpsString.spawnLeft(7, 6);
		menuScene.init();
		gameScene.init();
	}
	
	public void updateGame(float dt)
	{
		//System.out.println(dt);
		
		if(!gamePanel.isFocusOwner()) gamePanel.requestFocus();
		currentScene.update();
		
		if(fpsCounter.countFrame() && GameSettings.displayFps)
			fpsString.setString("FPS: " + String.format("%.2f", fpsCounter.getFpsCount()));
		
		gamePanel.repaint();
	}
	
	public void handleKeyInput(int keyCode)
	{
		InputOutcome outcome = currentScene.handleKeyInput(keyCode);
		
		if(outcome == InputOutcome.ToGame) {
			gameScene.reset(menuScene.getPlayer1Type(), menuScene.getPlayer2Type());
			currentScene = gameScene;
		}
		else if(outcome == InputOutcome.ToMenu) {
			menuScene.startUpdating();
			menuScene.startDrawing();
			currentScene = menuScene;
		}
		else if(outcome == InputOutcome.Pause)
			currentScene.stopUpdating();
		else if(outcome == InputOutcome.Unpause)
			currentScene.startUpdating();
	}
	
	public void handleMouseMove()
	{
		currentScene.handleMouseMove();
	}
	
	public void handleMouseDrag()
	{
		currentScene.handleMouseDrag();
	}
	
	public void handleMouseClick()
	{
		currentScene.handleMouseClick();
	}
	
	public void drawScene(Graphics2D g2d)
	{
		GameToolbox.graphics = g2d;
		
		if(GameSettings.antiAliasingOn) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
		else {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}
		
		currentScene.draw(g2d);
		if(GameSettings.displayFps) fpsString.draw(g2d);
		_drawCursor(g2d);
		
		g2d.dispose();
	}
	
	private void _drawCursor(Graphics2D g2d)
	{
		g2d.setColor(MoreColors.silver);
		g2d.fillRect(GameToolbox.cursorX - 1, GameToolbox.cursorY - 1, 3, 3);
	}
}