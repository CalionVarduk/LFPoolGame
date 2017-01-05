package LF.PoolGame;

import LF.PoolGame.GameEngine;
import LF.PoolGame.Logic.GameToolbox;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public final class GamePanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener
{
	private GameEngine gameEngine;
	
	public GamePanel(int width, int height)
	{
		super(true);
		setBackground(MoreColors.greenBackground);
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);

		setPreferredSize(new Dimension(width, height));
		
		gameEngine = new GameEngine(this);
	}
	 
	public void initGameEngine()
	{
		gameEngine.init();
		gameEngine.start();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		gameEngine.drawScene((Graphics2D)g);
	}
	   
	public void mouseMoved(MouseEvent e)
	{
		GameToolbox.cursorX = e.getX();
		GameToolbox.cursorY = e.getY();
		GameToolbox.mouseClicked = false;
		gameEngine.handleMouseMove();
	}

	public void mouseDragged(MouseEvent e)
	{
		GameToolbox.cursorX = e.getX();
		GameToolbox.cursorY = e.getY();
		GameToolbox.mouseClicked = false;
		gameEngine.handleMouseDrag();
	}
	
	public void mousePressed(MouseEvent e)
	{		
		if(e.getButton() == 1) {
			GameToolbox.cursorX = e.getX();
			GameToolbox.cursorY = e.getY();
			GameToolbox.mouseClicked = true;
			gameEngine.handleMouseClick();
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		//System.out.println(e.getKeyCode());
		gameEngine.handleKeyInput(e.getKeyCode());
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}