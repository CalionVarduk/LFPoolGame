package LF.PoolGame;

import LF.PoolGame.Logic.GameSettings;

public final class FpsCounter
{
	private long _startTime;
	private long _prevUpdate;
	private int _ticksPerUpdate;
	private long _updateInterval;
	private float _framesPerSecond;
	
	public FpsCounter()
	{
		_updateInterval = 1000000000L;
		_prevUpdate = 0;
		_ticksPerUpdate = 0;
		_framesPerSecond = 1000.0f / GameSettings.frameInterval;
		_startTime = System.nanoTime();
	}

	public boolean countFrame()
	{
		++_ticksPerUpdate;
		long currentUpdate = System.nanoTime() - _startTime;
		long timeDifference = currentUpdate - _prevUpdate;
		
		if(timeDifference >= _updateInterval) {
			_framesPerSecond = (_ticksPerUpdate * _updateInterval) / (float)timeDifference;
			_ticksPerUpdate = 0;
			_prevUpdate = currentUpdate;
			return true;
		}
		return false;
	}

	public float getFpsCount()
	{
		return _framesPerSecond;
	}
}
