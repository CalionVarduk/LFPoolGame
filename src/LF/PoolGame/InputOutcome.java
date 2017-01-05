package LF.PoolGame;

public class InputOutcome
{
	public static final InputOutcome OK = new InputOutcome("OK");
	public static final InputOutcome ChangeScene = new InputOutcome("Change Scene");
	public static final InputOutcome ToMenu = new InputOutcome("Menu");
	public static final InputOutcome ToGame = new InputOutcome("Game");
	public static final InputOutcome Pause = new InputOutcome("Pause");
	public static final InputOutcome Unpause = new InputOutcome("Unpause");
	public static final InputOutcome Reset = new InputOutcome("Reset");
	
	public String msg;
	
	public InputOutcome(String msg)
	{
		this.msg = msg;
	}
	
	public boolean equals(InputOutcome outcome)
	{
		return msg.equalsIgnoreCase(outcome.msg);
	}
}
