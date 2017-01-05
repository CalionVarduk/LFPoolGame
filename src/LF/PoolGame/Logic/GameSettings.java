package LF.PoolGame.Logic;

public abstract class GameSettings
{
	public static int timeFractions = 20;
	public static int frameInterval = 16;
	public static boolean drawBoundingBoxes = false;
	public static boolean drawVectors = false;
	public static boolean frictionOn = false;
	public static boolean gameFrictionOn = true;
	public static boolean displayFps = true;
	public static boolean antiAliasingOn = true;
	public static float ballFrictionValue = 0.3f;
	public static float ballFrictionDelta = ballFrictionValue / ((1000 / frameInterval) * timeFractions);
	public static float ballCollisionSpeedRetained = 0.99f;
	public static float bumperCollisionSpeedRetained = 0.75f;
}
