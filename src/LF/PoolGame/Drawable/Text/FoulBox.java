package LF.PoolGame.Drawable.Text;

import LF.PoolGame.MoreColors;
import LF.PoolGame.Logic.PlayResult;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public abstract class FoulBox
{
	private static MessageBox _box = new MessageBox(30, 30, 20, Color.BLACK, MoreColors.lightSteelBlue, new Font("Arial", Font.BOLD | Font.ITALIC, 15));
	private static PlayResult _recentResult = null;
	
	public static void init()
	{
		_recentResult = null;
		_box.addString("");
		_box.addString("");
		_box.addString("Continue").select();
		_box.getBoxString(2).setSelectedColor(MoreColors.whiteSmoke);
		_box.spawn(0, 0);
		_box.stopDrawing();
	}
	
	public static boolean isDisplayed()
	{
		return _box.isDrawn();
	}
	
	public static PlayResult getRecentPlayResult()
	{
		return _recentResult;
	}
	
	public static void display(String playerName, PlayResult result, Point2D.Float location)
	{
		_recentResult = result;
		_box.getBoxString(0).setString(result.title);
		_box.getBoxString(1).setString(playerName + result.info);
		_box.spawn(0, 0);
		_box.setLocation(location.x, location.y);
		_box.startDrawing();
	}
	
	public static void hide()
	{
		_box.stopDrawing();
	}
	
	public static void draw(Graphics2D g2d)
	{
		_box.draw(g2d);
	}
}