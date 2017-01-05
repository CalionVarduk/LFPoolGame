package LF.PoolGame.Logic;

public final class PlayResult
{
	public static final PlayResult Continue = new PlayResult("", "");
	public static final PlayResult SwitchPlayer = new PlayResult("", "");
	public static final PlayResult ResetGame = new PlayResult("", "");
	public static final PlayResult BallTypesSet = new PlayResult("", "");
	public static final PlayResult Won = new PlayResult("Game Over", " has won the game!");
	public static final PlayResult Lost = new PlayResult("Game Over", " has lost the game!");
	public static final PlayResult NoneHit = new PlayResult("Faul!", " has failed to hit any pool ball.");
	public static final PlayResult WrongFirstHit = new PlayResult("Faul!", " has hit a pool ball of a wrong type first.");
	public static final PlayResult PottedWrong = new PlayResult("Faul!", " has potted a pool ball of a wrong type.");
	public static final PlayResult PottedCueBall = new PlayResult("Faul!", " has potted the cue ball.");
	public static final PlayResult Potted8Ball = new PlayResult("Game Over", " has potted the 8-ball.");
	
	public final String title;
	public final String info;
	
	public PlayResult(String title, String info)
	{
		this.title = title;
		this.info = info;
	}
	
	public boolean isDisplayable()
	{
		return (info.length() > 0);
	}
}