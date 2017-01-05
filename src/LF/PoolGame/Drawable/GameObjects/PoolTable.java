package LF.PoolGame.Drawable.GameObjects;

import LF.PoolGame.MoreColors;
import LF.PoolGame.Drawable.DrawableObject;
import LF.PoolGame.Logic.GameToolbox;
import LF.PoolGame.Math.Collider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class PoolTable extends DrawableObject
{
	private int _tableWidth;
	private int _tableHeight;
	private Color _frameColor;
	
	private Rectangle2D.Float _safeArea;
	private Ellipse2D.Float _headSpot;
	private Rectangle2D.Float _line;
	private Ellipse2D.Float _footSpot;
	
	private Rectangle2D.Float[] _tableFrames;
	private PoolPocket[] _pockets;
	private RoundBumper[] _roundBumpers;
	private SegmentBumper[] _segmentBumpers;
	
	public PoolTable()
	{
		super();
		_construct();
		setTableSize(400, 200);
	}
	
	public PoolTable(int width, int height)
	{
		super();
		_construct();
		setTableSize(width, height);
	}
	
	public void spawn(float x, float y)
	{		
		setLocation(x, y);
        startDrawing();
	}
	
	public void offsetLocationX(float x)
	{	
		super.offsetLocationX(x);
		_safeArea.x += x;
		_headSpot.x += x;
		_line.x += x;
		_footSpot.x += x;
		
		for(int i = 0; i < _tableFrames.length; ++i)
			_tableFrames[i].x += x;

		for(int i = 0; i < _pockets.length; ++i)
			_pockets[i].offsetLocationX(x);

		for(int i = 0; i < _roundBumpers.length; ++i)
			_roundBumpers[i].offsetLocationX(x);
		
		for(int i = 0; i < _segmentBumpers.length; ++i)
			_segmentBumpers[i].offsetLocationX(x);
	}
	
	public void offsetLocationY(float y)
	{
		super.offsetLocationY(y);
		_safeArea.y += y;
		_headSpot.y += y;
		_line.y += y;
		_footSpot.y += y;
		
		for(int i = 0; i < _tableFrames.length; ++i)
			_tableFrames[i].y += y;

		for(int i = 0; i < _pockets.length; ++i)
			_pockets[i].offsetLocationY(y);

		for(int i = 0; i < _roundBumpers.length; ++i)
			_roundBumpers[i].offsetLocationY(y);
		
		for(int i = 0; i < _segmentBumpers.length; ++i)
			_segmentBumpers[i].offsetLocationY(y);
	}
	
	public int getTableWidth()
	{
		return _tableWidth;
	}
	
	public int getTableHeight()
	{
		return _tableHeight;
	}
	
	public void setTableSize(int width, int height)
	{
		_tableWidth = (width > 400) ? width : 400;
		_tableHeight = (height > 200) ? height : 200;
		
		float tableCentreX = getLocationX();
		float tableLeft = tableCentreX - (_tableWidth * 0.5f);
		float tableRight = tableLeft + _tableWidth;
		float tableCentreY = getLocationY();
		float tableTop = tableCentreY - (_tableHeight * 0.5f);
		float tableBottom = tableTop + _tableHeight;

		_setupTableSpots(tableLeft, tableRight, tableCentreY);
        _setupPockets(tableLeft, tableCentreX, tableRight, tableTop, tableBottom);
        _setupSafeArea();
        
        Point2D.Float[] auxPoints = _getTableConstructionPoints(tableLeft, tableRight, tableTop, tableBottom);
        _setupRoundBumpers(auxPoints);
        _setupSegmentBumpers(auxPoints);
        fixBoundingBox();
        _setupTableFrames();
	}
	
	public Color getFrameColor()
	{
		return _frameColor;
	}
	
	public void setFrameColor(Color color)
	{
		_frameColor = color;
	}
	
	public Color getBumperColor()
	{
		return _roundBumpers[0].getMainColor();
	}
	
	public void setBumperColor(Color color)
	{
		for(int i = 0; i < _roundBumpers.length; ++i)
			_roundBumpers[i].setMainColor(color);
		
		for(int i = 0; i < _segmentBumpers.length; ++i)
			_segmentBumpers[i].setMainColor(color);
	}
	
	public float getFootSpotX()
	{
		return (float)_footSpot.getCenterX();
	}
	
	public float getFootSpotY()
	{
		return (float)_footSpot.getCenterY();
	}
	
	public Point2D.Float getFootSpot()
	{
		return new Point2D.Float(getFootSpotX(), getFootSpotY());
	}
	
	public float getHeadSpotX()
	{
		return (float)_headSpot.getCenterX();
	}
	
	public float getHeadSpotY()
	{
		return (float)_headSpot.getCenterY();
	}
	
	public Point2D.Float getHeadSpot()
	{
		return new Point2D.Float(getHeadSpotX(), getHeadSpotY());
	}
	
	public int getPocketCount()
	{
		return _pockets.length;
	}
	
	public Rectangle2D.Float getPocketBoundingBox(int i)
	{
		return _pockets[i].getBoundingBox();
	}
	
	public Point2D.Float getPocketLocation(int i)
	{
		return _pockets[i].getLocation();
	}
	
	public boolean collisionCheck(PoolBall ball)
	{
		if(!Collider.rectangles(ball.getBoundingBox(), _safeArea)) {
			for(int i = 0; i < _pockets.length; ++i)
				if(ball.collisionCheck(_pockets[i])) return true;
			
			for(int i = 0; i < _segmentBumpers.length; ++i)
				if(ball.collisionCheck(_segmentBumpers[i])) return true;
			
			for(int i = 0; i < _roundBumpers.length; ++i)
				if(ball.collisionCheck(_roundBumpers[i])) return true;
		}
		return false;
	}
	
	public boolean cueBallInHandCheck(Rectangle2D.Float newCueBallBox)
	{
		if(newCueBallBox.getMinX() > _segmentBumpers[16].getStartX() && newCueBallBox.getMaxX() < _segmentBumpers[7].getStartX())
			if(newCueBallBox.getMinY() > _segmentBumpers[1].getStartY() && newCueBallBox.getMaxY() < _segmentBumpers[10].getStartY())
				return true;
		return false;
	}
	
	public void fixBoundingBox()
	{
        float minX = _pockets[0].getLeft();
        float maxX = _pockets[0].getRight();
        float minY = _pockets[0].getTop();
        float maxY = _pockets[0].getBottom();
        
        for(int i = 1; i < _pockets.length; ++i) {
        	if(minX > _pockets[i].getLeft()) minX = _pockets[i].getLeft();
        	if(maxX < _pockets[i].getRight()) maxX = _pockets[i].getRight();
        	if(minY > _pockets[i].getTop()) minY = _pockets[i].getTop();
        	if(maxY < _pockets[i].getBottom()) maxY = _pockets[i].getBottom();
        }
        
        for(int i = 0; i < _roundBumpers.length; ++i) {
        	if(minX > _roundBumpers[i].getLeft()) minX = _roundBumpers[i].getLeft();
        	if(maxX < _roundBumpers[i].getRight()) maxX = _roundBumpers[i].getRight();
        	if(minY > _roundBumpers[i].getTop()) minY = _roundBumpers[i].getTop();
        	if(maxY < _roundBumpers[i].getBottom()) maxY = _roundBumpers[i].getBottom();
        }
        
        for(int i = 0; i < _segmentBumpers.length; ++i) {
        	if(minX > _segmentBumpers[i].getLeft()) minX = _segmentBumpers[i].getLeft();
        	if(maxX < _segmentBumpers[i].getRight()) maxX = _segmentBumpers[i].getRight();
        	if(minY > _segmentBumpers[i].getTop()) minY = _segmentBumpers[i].getTop();
        	if(maxY < _segmentBumpers[i].getBottom()) maxY = _segmentBumpers[i].getBottom();
        }
        
        _boundingBox.x = minX;
        _boundingBox.y = minY;
        _boundingBox.width = maxX - minX;
        _boundingBox.height = maxY - minY;
	}
	
	public void drawBoundingBoxes(Graphics2D g2d)
	{
		if(isDrawn()) {			
			for(int i = 0; i < _roundBumpers.length; ++i)
				_roundBumpers[i].drawBoundingBox(g2d);
			
			for(int i = 0; i < _segmentBumpers.length; ++i)
				_segmentBumpers[i].drawBoundingBox(g2d);
			
			for(int i = 0; i < _pockets.length; ++i)
				_pockets[i].drawBoundingBox(g2d);
			
			g2d.draw(_safeArea);
		}
	}
	
	public void drawVectors(Graphics2D g2d)
	{
		if(isDrawn())
			for(int i = 0; i < _segmentBumpers.length; ++i)
				_segmentBumpers[i].drawVector(g2d);
	}

	protected void drawObject(Graphics2D g2d)
	{
		g2d.setColor(_mainColor);
		g2d.fill(_boundingBox);
		
		g2d.setColor(MoreColors.whiteSmoke);
		g2d.fill(_line);
		g2d.fill(_headSpot);
		g2d.fill(_footSpot);
		
		for(int i = 0; i < _roundBumpers.length; ++i)
			_roundBumpers[i].draw(g2d);
		
		for(int i = 0; i < _segmentBumpers.length; ++i)
			_segmentBumpers[i].draw(g2d);
		
		g2d.setColor(_frameColor);
		for(int i = 0; i < _tableFrames.length; ++i)
			g2d.fill(_tableFrames[i]);
		
		for(int i = 0; i < _pockets.length; ++i)
			_pockets[i].draw(g2d);
	}
	
	private void _setupTableSpots(float tableLeft, float tableRight, float tableCentreY)
	{
        _headSpot.x = tableLeft + (_tableWidth * 0.25f) - (_headSpot.width * 0.5f);
        _headSpot.y = tableCentreY - (_headSpot.height * 0.5f);
        _footSpot.x = tableRight - (_tableWidth * 0.25f) - (_footSpot.width * 0.5f);
        _footSpot.y = tableCentreY - (_footSpot.height * 0.5f);
        _line.x = getHeadSpotX() - 1;
        _line.y = tableCentreY - (_tableHeight * 0.5f) - 1;
        _line.height = _tableHeight + 2;
	}
	
	private void _setupPockets(float tableLeft, float tableCentreX, float tableRight, float tableTop, float tableBottom)
	{
		float pocket_45 = _pockets[0].getRadius() * 0.7071068f;
		
        _pockets[0].spawn(tableLeft - pocket_45, tableTop - pocket_45);
        _pockets[1].spawn(tableCentreX, tableTop - _pockets[1].getRadius());
        _pockets[2].spawn(tableRight + pocket_45, tableTop - pocket_45);
        _pockets[3].spawn(tableRight + pocket_45, tableBottom + pocket_45);
        _pockets[4].spawn(tableCentreX, tableBottom + _pockets[4].getRadius());
        _pockets[5].spawn(tableLeft- pocket_45, tableBottom + pocket_45);
	}
	
	private void _setupSafeArea()
	{
        _safeArea.x = _pockets[0].getRight() + GameToolbox.poolBallSize + 2;
        _safeArea.y = _pockets[0].getBottom() + GameToolbox.poolBallSize + 2;
        _safeArea.width = (_pockets[3].getLeft() - GameToolbox.poolBallSize - 2) - _safeArea.x;
        _safeArea.height = (_pockets[3].getTop() - GameToolbox.poolBallSize - 2) - _safeArea.y;
	}
	
	private void _setupRoundBumpers(Point2D.Float[] auxPoints)
	{
        _roundBumpers[0].spawn(auxPoints[3].x, auxPoints[3].y);
        _roundBumpers[1].spawn(auxPoints[4].x, auxPoints[4].y);
        _roundBumpers[2].spawn(auxPoints[11].x, auxPoints[11].y);
        _roundBumpers[3].spawn(auxPoints[12].x, auxPoints[12].y);
        _roundBumpers[4].spawn(auxPoints[19].x, auxPoints[19].y);
        _roundBumpers[5].spawn(auxPoints[20].x, auxPoints[20].y);
        _roundBumpers[6].spawn(auxPoints[27].x, auxPoints[27].y);
        _roundBumpers[7].spawn(auxPoints[28].x, auxPoints[28].y);
        _roundBumpers[8].spawn(auxPoints[35].x, auxPoints[35].y);
        _roundBumpers[9].spawn(auxPoints[36].x, auxPoints[36].y);
        _roundBumpers[10].spawn(auxPoints[43].x, auxPoints[43].y);
        _roundBumpers[11].spawn(auxPoints[44].x, auxPoints[44].y);
	}
	
	private void _setupSegmentBumpers(Point2D.Float[] auxPoints)
	{
        _segmentBumpers[0].spawn(auxPoints[0].x, auxPoints[0].y, auxPoints[1].x, auxPoints[1].y);
        _segmentBumpers[1].spawn(auxPoints[2].x, auxPoints[2].y, auxPoints[5].x, auxPoints[5].y);
        _segmentBumpers[2].spawn(auxPoints[6].x, auxPoints[6].y, auxPoints[7].x, auxPoints[7].y);
        _segmentBumpers[3].spawn(auxPoints[8].x, auxPoints[8].y, auxPoints[9].x, auxPoints[9].y);
        _segmentBumpers[4].spawn(auxPoints[10].x, auxPoints[10].y, auxPoints[13].x, auxPoints[13].y);
        _segmentBumpers[5].spawn(auxPoints[14].x, auxPoints[14].y, auxPoints[15].x, auxPoints[15].y);
        _segmentBumpers[6].spawn(auxPoints[16].x, auxPoints[16].y, auxPoints[17].x, auxPoints[17].y);
        _segmentBumpers[7].spawn(auxPoints[18].x, auxPoints[18].y, auxPoints[21].x, auxPoints[21].y);
        _segmentBumpers[8].spawn(auxPoints[22].x, auxPoints[22].y, auxPoints[23].x, auxPoints[23].y);
        _segmentBumpers[9].spawn(auxPoints[24].x, auxPoints[24].y, auxPoints[25].x, auxPoints[25].y);
        _segmentBumpers[10].spawn(auxPoints[26].x, auxPoints[26].y, auxPoints[29].x, auxPoints[29].y);
        _segmentBumpers[11].spawn(auxPoints[30].x, auxPoints[30].y, auxPoints[31].x, auxPoints[31].y);
        _segmentBumpers[12].spawn(auxPoints[32].x, auxPoints[32].y, auxPoints[33].x, auxPoints[33].y);
        _segmentBumpers[13].spawn(auxPoints[34].x, auxPoints[34].y, auxPoints[37].x, auxPoints[37].y);
        _segmentBumpers[14].spawn(auxPoints[38].x, auxPoints[38].y, auxPoints[39].x, auxPoints[39].y);
        _segmentBumpers[15].spawn(auxPoints[40].x, auxPoints[40].y, auxPoints[41].x, auxPoints[41].y);
        _segmentBumpers[16].spawn(auxPoints[42].x, auxPoints[42].y, auxPoints[45].x, auxPoints[45].y);
        _segmentBumpers[17].spawn(auxPoints[46].x, auxPoints[46].y, auxPoints[47].x, auxPoints[47].y);
	}
	
	private void _setupTableFrames()
	{
		_tableFrames[0].x = _boundingBox.x - 25;
		_tableFrames[0].y = _boundingBox.y - 15;
		_tableFrames[0].width = _boundingBox.width + 50;
		_tableFrames[0].height = _roundBumpers[0].getTop() - _tableFrames[0].y + 1;
		
		_tableFrames[1].x = _tableFrames[0].x;
		_tableFrames[1].y = _roundBumpers[6].getBottom() - 1;
		_tableFrames[1].width = _tableFrames[0].width;
		_tableFrames[1].height = _tableFrames[0].height;
		
		_tableFrames[2].x = _tableFrames[0].x;
		_tableFrames[2].y = (float)_tableFrames[0].getMaxY() - 1;
		_tableFrames[2].width = _segmentBumpers[15].getStartX() + 4 - _tableFrames[2].x;
		_tableFrames[2].height = _tableFrames[1].y - (float)_tableFrames[0].getMaxY() + 3;
		
		_tableFrames[3].x = _segmentBumpers[6].getStartX() - 2;
		_tableFrames[3].y = _tableFrames[2].y;
		_tableFrames[3].width = (float)_tableFrames[0].getMaxX() - _tableFrames[3].x;
		_tableFrames[3].height = _tableFrames[2].height;
	}
	
	private void _construct()
	{
		setMainColor(MoreColors.seaGreen);
		setFrameColor(MoreColors.saddleBrown);
		
		_safeArea = new Rectangle2D.Float();
		_line = new Rectangle2D.Float(0, 0, 3, 0);
		_headSpot = new Ellipse2D.Float(0, 0, 7, 7);
		_footSpot = new Ellipse2D.Float(0, 0, 7, 7);
		
		_tableFrames = new Rectangle2D.Float[4];
		for(int i = 0; i < _tableFrames.length; ++i)
			_tableFrames[i] = new Rectangle2D.Float();
		
		_pockets = new PoolPocket[6];
		for(int i = 0; i < _pockets.length; ++i)
			_pockets[i] = new PoolPocket(GameToolbox.poolBallSize + 20);
		
		_roundBumpers = new RoundBumper[12];
		for(int i = 0; i < _roundBumpers.length; ++i)
			_roundBumpers[i] = new RoundBumper(GameToolbox.poolBallSize, MoreColors.greenBumper);
		
		_segmentBumpers = new SegmentBumper[18];
		for(int i = 0; i < _segmentBumpers.length; ++i)
			_segmentBumpers[i] = new SegmentBumper(GameToolbox.poolBallSize + 5, MoreColors.greenBumper);
	}
	
	private Point2D.Float[] _getTableConstructionPoints(float tableLeft, float tableRight, float tableTop, float tableBottom)
	{
        float pocket_45 = _pockets[0].getRadius() * 0.7071068f;

        float r = _roundBumpers[0].getRadius();
        float a_45 = r * 0.4142136f;
        float b_45 = a_45 * 0.7071068f;

        float a_70 = r * 0.7002075f;
        float b_70 = a_70 * 0.3420201f;
        float c_70 = a_70 * 0.9396926f;
        float tg20 = 0.3639702f;
		
        Point2D.Float[] auxPoints = new Point2D.Float[48];
        for(int i = 0; i < auxPoints.length; i++) auxPoints[i] = new Point2D.Float();
        
        auxPoints[0].x = _pockets[0].getLocationX() + pocket_45;
        auxPoints[0].y = _pockets[0].getLocationY() - pocket_45;

        auxPoints[1].x = auxPoints[0].x + (tableTop - auxPoints[0].y) - b_45;
        auxPoints[1].y = tableTop - b_45;

        auxPoints[2].x = auxPoints[1].x + a_45 + b_45;
        auxPoints[2].y = tableTop;

        auxPoints[3].x = auxPoints[2].x;
        auxPoints[3].y = auxPoints[2].y - r;

        auxPoints[7].x = _pockets[1].getLeft();
        auxPoints[7].y = _pockets[1].getLocationY();

        auxPoints[6].x = auxPoints[7].x - ((tableTop - auxPoints[7].y) * tg20) + b_70;
        auxPoints[6].y = tableTop - c_70;

        auxPoints[5].x = auxPoints[7].x - ((tableTop - auxPoints[7].y) * tg20) - a_70;
        auxPoints[5].y = tableTop;

        auxPoints[4].x = auxPoints[5].x;
        auxPoints[4].y = auxPoints[5].y - r;

        auxPoints[8].x = _pockets[1].getRight();
        auxPoints[8].y = _pockets[1].getLocationY();

        auxPoints[9].x = auxPoints[8].x + ((tableTop - auxPoints[8].y) * tg20) - b_70;
        auxPoints[9].y = tableTop - c_70;

        auxPoints[10].x = auxPoints[8].x + ((tableTop - auxPoints[8].y) * tg20) + a_70;
        auxPoints[10].y = tableTop;

        auxPoints[11].x = auxPoints[10].x;
        auxPoints[11].y = auxPoints[10].y - r;

        auxPoints[15].x = _pockets[2].getLocationX() - pocket_45;
        auxPoints[15].y = _pockets[2].getLocationY() - pocket_45;

        auxPoints[14].x = auxPoints[15].x - (tableTop - auxPoints[15].y) + b_45;
        auxPoints[14].y = tableTop - b_45;

        auxPoints[13].x = auxPoints[14].x - a_45 - b_45;
        auxPoints[13].y = tableTop;

        auxPoints[12].x = auxPoints[13].x;
        auxPoints[12].y = auxPoints[13].y - r;

        auxPoints[16].x = _pockets[2].getLocationX() + pocket_45;
        auxPoints[16].y = _pockets[2].getLocationY() + pocket_45;

        auxPoints[17].x = tableRight + b_45;
        auxPoints[17].y = auxPoints[16].y + (auxPoints[16].x - tableRight) - b_45;

        auxPoints[18].x = tableRight;
        auxPoints[18].y = auxPoints[17].y + a_45 + b_45;

        auxPoints[19].x = auxPoints[18].x + r;
        auxPoints[19].y = auxPoints[18].y;

        auxPoints[23].x = _pockets[3].getLocationX() + pocket_45;
        auxPoints[23].y = _pockets[3].getLocationY() - pocket_45;

        auxPoints[22].x = tableRight + b_45;
        auxPoints[22].y = auxPoints[23].y - (auxPoints[23].x - tableRight) + b_45;

        auxPoints[21].x = tableRight;
        auxPoints[21].y = auxPoints[22].y - a_45 - b_45;

        auxPoints[20].x = auxPoints[21].x + r;
        auxPoints[20].y = auxPoints[21].y;

        auxPoints[24].x = _pockets[3].getLocationX() - pocket_45;
        auxPoints[24].y = _pockets[3].getLocationY() + pocket_45;

        auxPoints[25].x = auxPoints[24].x - (auxPoints[24].y - tableBottom) + b_45;
        auxPoints[25].y = tableBottom + b_45;

        auxPoints[26].x = auxPoints[25].x - a_45 - b_45;
        auxPoints[26].y = tableBottom;

        auxPoints[27].x = auxPoints[26].x;
        auxPoints[27].y = auxPoints[26].y + r;

        auxPoints[31].x = _pockets[4].getRight();
        auxPoints[31].y = _pockets[4].getLocationY();

        auxPoints[30].x = auxPoints[31].x + ((auxPoints[31].y - tableBottom) * tg20) - b_70;
        auxPoints[30].y = tableBottom + c_70;

        auxPoints[29].x = auxPoints[31].x + ((auxPoints[31].y - tableBottom) * tg20) + a_70;
        auxPoints[29].y = tableBottom;

        auxPoints[28].x = auxPoints[29].x;
        auxPoints[28].y = auxPoints[29].y + r;

        auxPoints[32].x = _pockets[4].getLeft();
        auxPoints[32].y = _pockets[4].getLocationY();

        auxPoints[33].x = auxPoints[32].x - ((auxPoints[32].y - tableBottom) * tg20) + b_70;
        auxPoints[33].y = tableBottom + c_70;

        auxPoints[34].x = auxPoints[32].x - ((auxPoints[32].y - tableBottom) * tg20) - a_70;
        auxPoints[34].y = tableBottom;

        auxPoints[35].x = auxPoints[34].x;
        auxPoints[35].y = auxPoints[34].y + r;

        auxPoints[39].x = _pockets[5].getLocationX() + pocket_45;
        auxPoints[39].y = _pockets[5].getLocationY() + pocket_45;

        auxPoints[38].x = auxPoints[39].x + (auxPoints[39].y - tableBottom) - b_45;
        auxPoints[38].y = tableBottom + b_45;

        auxPoints[37].x = auxPoints[38].x + a_45 + b_45;
        auxPoints[37].y = tableBottom;

        auxPoints[36].x = auxPoints[37].x;
        auxPoints[36].y = tableBottom + r;

        auxPoints[40].x = _pockets[5].getLocationX() - pocket_45;
        auxPoints[40].y = _pockets[5].getLocationY() - pocket_45;

        auxPoints[41].x = tableLeft - b_45;
        auxPoints[41].y = auxPoints[40].y - (tableLeft - auxPoints[40].x) + b_45;

        auxPoints[42].x = tableLeft;
        auxPoints[42].y = auxPoints[41].y - a_45 - b_45;

        auxPoints[43].x = auxPoints[42].x - r;
        auxPoints[43].y = auxPoints[42].y;

        auxPoints[47].x = _pockets[0].getLocationX() - pocket_45;
        auxPoints[47].y = _pockets[0].getLocationY() + pocket_45;

        auxPoints[46].x = tableLeft - b_45;
        auxPoints[46].y = auxPoints[47].y + (tableLeft- auxPoints[47].x) - b_45;

        auxPoints[45].x = tableLeft;
        auxPoints[45].y = auxPoints[46].y + a_45 + b_45;

        auxPoints[44].x = auxPoints[45].x - r;
        auxPoints[44].y = auxPoints[45].y;
        
        return auxPoints;
	}
}