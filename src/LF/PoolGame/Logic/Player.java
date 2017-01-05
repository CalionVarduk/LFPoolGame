package LF.PoolGame.Logic;

import LF.PoolGame.Drawable.GameObjects.PoolBall;
import LF.PoolGame.Drawable.GameObjects.PoolCue;
import LF.PoolGame.Drawable.GameObjects.PoolTable;

public abstract class Player
{	
	public PoolCue cue;
	
	protected PoolTable _table;
	protected PoolBall[] _balls;
	
	protected PlayerType _type;
	protected String _name;
	protected BallType _ballType;
	private int _pottedCount;
	
	public Player()
	{
		_construct("");
	}
	
	public Player(String name)
	{
		_construct(name);
	}
	
	public void setup(PoolTable table, PoolBall[] balls)
	{
		_table = table;
		_balls = balls;
		cue.setOwner(this);
	}
	
	public void setName(String name)
	{
		_name = (name.length() <= 12) ? name : name.substring(0, 11);
	}
	
	public String getName()
	{
		return _name;
	}
	
	public void setBallType(BallType type)
	{
		_ballType = type;
	}
	
	public BallType getBallType()
	{
		return _ballType;
	}
	
	public int getPottedCount()
	{
		return _pottedCount;
	}
	
	public void calculatePottedCount()
	{
		if(_ballType != BallType.OpenTable && _ballType != BallType.Ball8) {
			_pottedCount = 0;
			for(int i = 0; i < GameRules.pottedPoolBalls.size(); ++i)
				if(_ballType == GameRules.pottedPoolBalls.get(i).getType()) ++_pottedCount;
			if(_pottedCount == 7) _ballType = BallType.Ball8;
		}
	}
	
	public PlayerType getType()
	{
		return _type;
	}
	
	public abstract void makePlay();
	
	private void _construct(String name)
	{
		setName(name);
		cue = null;
		_table = null;
		_balls = null;
		_pottedCount = 0;
		_ballType = BallType.OpenTable;
		_type = PlayerType.Human;
	}
}