package LF.PoolGame.Logic;

public final class BallType
{
	public static final BallType OpenTable = new BallType("open table");
	public static final BallType Solids = new BallType("solids");
	public static final BallType Stripes = new BallType("stripes");
	public static final BallType Ball8 = new BallType("8-ball");
	
	public final String type;
	
	public BallType(String type)
	{
		this.type = type;
	}
}