package LF.PoolGame;

import LF.PoolGame.GamePanel;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/* TODO:
 * GameUI refactor (new class?: PauseBox)
 * MenuScene refactor (new class/classes?: SubMenu - for inMainMenu, inStartGameMenu, inSettingsMenu)
 * teach AI to handle cue ball in hand event
 * teach AI to check for potential cue ball potting
 * reteach AI to check for trail collision (calculate pointer point during checking and make check with it rather than with ball's centre)
 * teach AI to use table bumpers
 * teach AI not to always aim to pot
 */

class Program
{
	public static void main(String[] args)
	{
    	  javax.swing.SwingUtilities.invokeLater(new Runnable()
    	  {
    		  public void run()
    		  {   			  
    			  GamePanel panel = new GamePanel(1024, 768);

    			  JFrame jFrame = new JFrame("LF PoolGame");
    			  jFrame.add(panel);

    			  jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    			  jFrame.setResizable(false);
    			  jFrame.setVisible(true);
    			  
    			  jFrame.pack();
    			  Insets insets = jFrame.getInsets();
    			  jFrame.setSize(panel.getWidth() + insets.left + insets.right, panel.getHeight() + insets.top + insets.bottom);
    			  
    			  panel.setFocusable(true);
    			  panel.requestFocus();
    			  panel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
    					  new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor"));
    			  
    			  System.out.println(jFrame.getSize());
    			  System.out.println(panel.getSize());
    			  
    			  panel.initGameEngine();
    		  }
    	  });
	}
}