package com.tp1.jms.flowfree.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tp1.jms.flowfree.Activity.GameActivity;
import com.tp1.jms.flowfree.Model.Flow;
import com.tp1.jms.flowfree.Controller.GameState;
import com.tp1.jms.flowfree.Model.Level;
import com.tp1.jms.flowfree.Model.Position;

import java.util.ArrayList;

/**
 * This class describes a board from a custom view which represents the graphical grid of the game
 */
public class BoardView extends View {

    private int MAX_NB_CELLS = 5;
    private int cellWidth,cellHeight;
    private Paint paintGrid,paintPath,paintCircles,paintHighLightSquare;
    private Path path;
    private GameState gameState;
    private GameActivity gameactivity;
    private int levelnumber;
    private boolean once = true; // Flag for showing the dialog message only once



    public BoardView(Context context) {
        super(context);
        init(null, 0);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Init the main attribute of the grid
     * @param attrs attributes
     * @param defStyle styles definition
     */
    private void init(AttributeSet attrs, int defStyle) {
        //Init paint object
        gameactivity = (GameActivity) getContext();
        paintGrid = new Paint();
        paintPath = new Paint();
        paintCircles = new Paint();
        paintHighLightSquare =  new Paint();
        path = new Path();
        //Set Grid Color
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setColor(Color.WHITE);
        //Set Default style for the flow path
        paintPath.setStyle(Paint.Style.STROKE);
        paintPath.setColor(Color.WHITE);
        paintPath.setStrokeCap(Paint.Cap.ROUND);
        paintPath.setStrokeJoin(Paint.Join.ROUND);
        paintPath.setAntiAlias(true);
        //Set Default style for the highlighting flow path
        paintHighLightSquare.setStyle(Paint.Style.FILL);

    }

    /**
     * Convert position x to col coordinate
     * @param x real coordinate
     * @return colomn coordinate
     */
    private int xToCol(int x) {
        return (x - getPaddingLeft()) / cellWidth;
    }

    /**
     * Convert position y to row coordinate
     * @param y real coordinate
     * @return row coordinate
     */
    private int yToRow(int y) {
        return (y - getPaddingTop()) / cellHeight;
    }

    /**
     * Convert col coordinate to  position x
     * @param col coordinate in the grid
     * @return the real coordinate x
     */
    private int colToX(int col) {
        return col * cellWidth + getPaddingLeft();
    }

    /**
     * Convert row coordinate to  position y
     * @param row coordinate in the grid
     * @return the real coordinate y
     */
    private int rowToY(int row) {
        return row * cellHeight + getPaddingTop();
    }

    public int getLevelNumber() {
        return levelnumber;
    }

    public void setLevelNumber(int level) {
        this.levelnumber = level;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }


    public void setNbCells(int nbCells) {
        this.MAX_NB_CELLS = nbCells;
    }


    /**
     * Draw flows on the canvas (grid)
     * @param canvas canvas to drawn
     */
    public void drawFlows(Canvas canvas){
        ArrayList<Flow> flows = gameState.getLevelFlows();
        for (Flow f : flows) {
            paintCircles.setColor(f.getColor());
            canvas.drawCircle(colToX(f.getStart().getX()) + cellWidth / 2, rowToY(f.getStart().getY()) + cellHeight / 2, cellWidth / 4, paintCircles);
            canvas.drawCircle(colToX(f.getEnd().getX()) + cellWidth / 2, rowToY(f.getEnd().getY()) + cellHeight / 2, cellWidth / 4, paintCircles);
        }

    }

    /**
     * Draw grid lines on the canvas (grid)
     * @param canvas canvas to drawn
     */
    public void drawGrid(Canvas canvas){
        for (int r = 0; r < MAX_NB_CELLS; ++r) {
            for (int c = 0; c < MAX_NB_CELLS; ++c) {
                int x = colToX(c);
                int y = rowToY(r);
                Rect rect = new Rect();
                rect.set(x, y, x + cellWidth, y + cellHeight);
                canvas.drawRect(rect, paintGrid);
            }
        }
    }

    /**
     * Draw flow path line on the canvas (grid)
     * @param canvas canvas to drawn
     */
    public void drawFlowPath(Canvas canvas){
        ArrayList<Flow> flows = gameState.getLevelFlows();
        for (Flow f : flows) {
            path.reset();
            paintPath.setColor(f.getColor());
            paintHighLightSquare.setColor(f.getColor());
            paintHighLightSquare.setAlpha(32);
            if (f.getFlowPath()!=null && f.getFlowPath().size() > 0) {
                ArrayList<Position> positionList = f.getFlowPathPositions();
                Position pos = positionList.get(0);
                path.moveTo(colToX(pos.getX()) + cellWidth / 2, rowToY(pos.getY()) + cellHeight / 2);
                Rect rect = new Rect();
                rect.set(colToX(pos.getX()), rowToY(pos.getY()), colToX(pos.getX()) + cellWidth, rowToY(pos.getY()) + cellHeight);
                canvas.drawRect(rect, paintHighLightSquare);
                for (int j = 1; j < positionList.size(); j++) {
                    pos = positionList.get(j);
                    path.lineTo(colToX(pos.getX()) + cellWidth / 2,rowToY(pos.getY()) + cellHeight / 2);
                    Rect rectt = new Rect();
                    rectt.set(colToX(pos.getX()), rowToY(pos.getY()), colToX(pos.getX()) + cellWidth, rowToY(pos.getY()) + cellHeight);
                    canvas.drawRect(rectt, paintHighLightSquare);
                }

            }
            canvas.drawPath(path, paintPath);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawFlows(canvas);
        drawFlowPath(canvas);

    }

    /**
     * Reset the grid
     */
    public void reset() {
        gameState.reset();
        once = true;
        this.invalidate();
    }

    /**
     * Initialize the grid with the desired level
     * @param level
     */
    public void initialize(Level level) {
        gameState.initialize(level);
        MAX_NB_CELLS = level.getSize();
        levelnumber = level.getNumber();
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(width, height);

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());

        this.paintPath.setStrokeWidth(cellWidth / 4);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        int sw = Math.max(1, (int) paintGrid.getStrokeWidth());
        cellWidth = (xNew - getPaddingLeft() - getPaddingRight() - sw) / MAX_NB_CELLS;
        cellHeight = (yNew - getPaddingTop() - getPaddingBottom() - sw) / MAX_NB_CELLS;
        this.paintPath.setStrokeWidth(cellWidth / 4);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int c = xToCol(x);
        int r = yToRow(y);
        //If outside of the grid
        if (c >= MAX_NB_CELLS || r >= MAX_NB_CELLS || c< 0 || r <0) {
            return true;
        }
        //If inside of the grid
        Position pressedPosition = new Position(c, r);
        if (event.getAction() == MotionEvent.ACTION_DOWN) { // Touch event - Create or retrieve flows path
            gameState.addFlowPath(pressedPosition);
            this.invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) { // Move event - Update the flows path
           boolean updated = gameState.updateFlowPath(pressedPosition);
            if(updated){
                this.invalidate();
            }
            boolean won = gameState.checkWin(pressedPosition); // Check if won
            if(won && once){
                gameState.victory(gameactivity);
                gameactivity.victory();
                gameactivity.updateUnlockNextLevelButton();
                gameactivity.setHighScoreLabel();
                once = false;
            }
            else if(!gameState.checkBoardFilled() && gameState.checkAllFlowsFinished() && once){ // Check if defeat
                ((GameActivity) getContext()).defeat();
                once = false;
            }

        }
        return false;
    }

}
