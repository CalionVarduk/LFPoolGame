package LF.PoolGame.Logic;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public abstract class GameToolbox
{
	public static Point2D.Float pottedStartLocation = new Point2D.Float();
	public static int windowWidth = 0;
	public static int windowHeight = 0;
	public static boolean mouseClicked = false;
	public static int cursorX = 0;
	public static int cursorY = 0;
	public static int poolBallSize = 23;
	public static float maxCueBallVelocity = 40;
	public static Graphics2D graphics = null;
}
