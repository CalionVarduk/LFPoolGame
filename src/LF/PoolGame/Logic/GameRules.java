package LF.PoolGame.Logic;

import LF.PoolGame.Drawable.GameObjects.PoolBall;

import java.util.ArrayList;

public abstract class GameRules
{
	public static ArrayList<PoolBall> pottedPoolBalls = new ArrayList<PoolBall>(15);
	public static boolean isCueBallInHand = false;
	public static BallType firstHitType = BallType.OpenTable;
	private static boolean _isGameEnded = false;
	private static int _lastPottedIndex = -1;
	private static boolean _break = true;
	
	private static boolean _potted8Ball = false;
	private static boolean _pottedCueBall = false;
	private static boolean _pottedSolid = false;
	private static boolean _pottedStriped = false;
	private static boolean _wrongFirst = false;
	private static boolean _noneHit = true;
	private static int _pottedCount = 0;
	
	public static void reset()
	{
		pottedPoolBalls.clear();
		_lastPottedIndex = -1;
		_break = true;
		firstHitType = BallType.OpenTable;
		isCueBallInHand = false;
		_isGameEnded = false;
	}
	
	public static boolean breakTime()
	{
		return _break;
	}
	
	public static boolean isGameEnded()
	{
		return _isGameEnded;
	}
	
	public static PlayResult update(Player currentPlayer, boolean isCueBallPotted)
	{
		_resetRuleTrackers(isCueBallPotted);
		
		if(firstHitType != BallType.OpenTable)
			_scanForFauls(currentPlayer);
		
		if(_break) return _breakRulesResult(currentPlayer);
		if(currentPlayer.getBallType() == BallType.OpenTable) return _openTableRulesResult(currentPlayer);
		if(currentPlayer.getBallType() != BallType.Ball8) return _normalRulesResult(currentPlayer);
		return _8BallRulesResult(currentPlayer);
	}
	
	private static void _scanForFauls(Player currentPlayer)
	{
		if(currentPlayer.getBallType() == BallType.OpenTable) {
			if(firstHitType == BallType.Ball8) _wrongFirst = true;
		}
		else if(firstHitType != currentPlayer.getBallType()) _wrongFirst = true;
		
		for(int i = _lastPottedIndex + 1; i < pottedPoolBalls.size(); ++i) {
			if(pottedPoolBalls.get(i).getType() == BallType.Solids)	_pottedSolid = true;
			else if(pottedPoolBalls.get(i).getType() == BallType.Stripes) _pottedStriped = true;
			else _potted8Ball = true;
		}
		
		_noneHit = false;
		_lastPottedIndex = pottedPoolBalls.size() - 1;
		firstHitType = BallType.OpenTable;
	}
	
	private static PlayResult _breakRulesResult(Player currentPlayer)
	{
		_break = false;
		if(_potted8Ball || _pottedCueBall || _noneHit || _wrongFirst) return PlayResult.ResetGame;
		
		if(_pottedCount > 0) {
			if(_pottedSolid != _pottedStriped) {
				currentPlayer.setBallType((_pottedSolid) ? BallType.Solids : BallType.Stripes);
				return PlayResult.BallTypesSet;
			}
			else return PlayResult.Continue;
		}
		return PlayResult.SwitchPlayer;
	}
	
	private static PlayResult _openTableRulesResult(Player currentPlayer)
	{
		if(_potted8Ball) {
			_isGameEnded = true;
			return PlayResult.Potted8Ball;
		}
		if(_pottedCueBall) return PlayResult.PottedCueBall;
		if(_noneHit) return PlayResult.NoneHit;
		if(_wrongFirst) return PlayResult.WrongFirstHit;
		
		if(_pottedCount > 0) {
			if(_pottedSolid != _pottedStriped) {
				currentPlayer.setBallType((_pottedSolid) ? BallType.Solids : BallType.Stripes);
				return PlayResult.BallTypesSet;
			}
			else return PlayResult.Continue;
		}
		return PlayResult.SwitchPlayer;
	}
	
	private static PlayResult _normalRulesResult(Player currentPlayer)
	{
		if(_potted8Ball) {
			_isGameEnded = true;
			return PlayResult.Potted8Ball;			
		}
		if(_pottedCueBall) return PlayResult.PottedCueBall;
		if(_noneHit) return PlayResult.NoneHit;
		if(_wrongFirst) return PlayResult.WrongFirstHit;
		if(_pottedCount > 0) {
			if((currentPlayer.getBallType() == BallType.Solids && _pottedStriped) ||
				(currentPlayer.getBallType() == BallType.Stripes && _pottedSolid))
					return PlayResult.PottedWrong;
			return PlayResult.Continue;
		}
		return PlayResult.SwitchPlayer;
	}
	
	private static PlayResult _8BallRulesResult(Player currentPlayer)
	{
		if(_potted8Ball) {
			_isGameEnded = true;
			return (_pottedCueBall || _wrongFirst || _pottedCount > 1) ? PlayResult.Lost : PlayResult.Won;
		}
		if(_pottedCueBall) return PlayResult.PottedCueBall;
		if(_noneHit) return PlayResult.NoneHit;
		if(_wrongFirst) return PlayResult.WrongFirstHit;
		if(_pottedCount > 0) return PlayResult.PottedWrong;
		return PlayResult.SwitchPlayer;
	}
		
	private static void _resetRuleTrackers(boolean isCueBallPotted)
	{
		_potted8Ball = false;
		_pottedCueBall = isCueBallPotted;
		_pottedSolid = false;
		_pottedStriped = false;
		_wrongFirst = false;
		_noneHit = true;
		_pottedCount = pottedPoolBalls.size() - _lastPottedIndex - 1;
	}
}
