package LF.PoolGame;

public abstract class GameLoop extends Thread
{
	private double _targetInterval;
	private double _currentInterval;
	private long _timeDelta;
	private int _targetFps;
	
	public GameLoop(int targetFps)
	{
		setTargetFps(targetFps);
		_timeDelta = (long)_targetInterval;
		_currentInterval = _targetInterval;
	}
	
	public final void setTargetFps(int targetFps)
	{
		_targetFps = (targetFps <= 0) ? 1 : (targetFps > 300) ? 300 : targetFps;
		_targetInterval = 1000000000.0 / _targetFps;
	}
	
	public final int getTargetFps()
	{
		return _targetFps;
	}
	
	public final double getTargetInterval()
	{
		return _targetInterval;
	}
	
	public final void run()
	{
		while(true) {
			long startTime = System.nanoTime();
			updateGame((float)(_timeDelta * 0.000000001));
			long timeTaken = System.nanoTime() - startTime;
			
			double interval = _currentInterval - timeTaken;

			if(interval > 0) {
				int floorMs = (int)(interval * 0.000001);
				
				try {
					sleep(floorMs);
				} catch (InterruptedException e) { e.printStackTrace(); }
				
				interval -= (floorMs * 1000000);
				
				long busyWaitStart = System.nanoTime();
				while(busyWaitStart + interval >= System.nanoTime());
			}
			
			_timeDelta = System.nanoTime() - startTime;
			_currentInterval = _targetInterval + (_currentInterval - _timeDelta);
		}
	}
	
	public abstract void updateGame(float dt);
}