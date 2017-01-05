package LF.PoolGame.Logic;

import LF.PoolGame.InputOutcome;
import LF.PoolGame.Drawable.DrawableObject;

public abstract class Scene extends DrawableObject
{
	private boolean _isUpdating;
	
	public Scene()
	{
		super();
		stopUpdating();
	}
	
	public final void startUpdating()
	{
		_isUpdating = true;
	}
	
	public final void stopUpdating()
	{
		_isUpdating = false;
	}
	
	public final boolean isUpdating()
	{
		return _isUpdating;
	}
	
	public final void update()
	{
		if(_isUpdating)
			updateScene();
	}
	
	public InputOutcome handleKeyInput(int keyCode)
	{
		return InputOutcome.OK;
	}
	
	public InputOutcome handleMouseMove()
	{
		return InputOutcome.OK;
	}
	
	public InputOutcome handleMouseDrag()
	{
		return InputOutcome.OK;
	}
	
	public InputOutcome handleMouseClick()
	{
		return InputOutcome.OK;
	}
	
	protected abstract void updateScene();
}
