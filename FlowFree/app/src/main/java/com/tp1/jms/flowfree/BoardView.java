package com.tp1.jms.flowfree;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends View {

    private int NUM_CELLS = 8;
    private int cellWidth;
    private int cellHeight;
    private Paint paintGrid;
    private Paint paintPath;
    private Paint paintCircles;
    private ArrayList<Flow> flows; ;
    private Path path;
    private Flow currentFlow;


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

    private void init(AttributeSet attrs, int defStyle) {

        paintGrid = new Paint();
        paintPath = new Paint();
        paintCircles = new Paint();
        path = new Path();
        // Load attributes
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setColor(Color.GRAY);

        paintPath.setStyle(Paint.Style.STROKE);
        //paintPath.setColor(Color.GREEN);
        paintPath.setStrokeCap(Paint.Cap.ROUND);
        paintPath.setStrokeJoin(Paint.Join.ROUND);
        paintPath.setAntiAlias(true);

        flows = new ArrayList<Flow>();
        Flow flow1 = new Flow(new Position(3,0),new Position(0,3),Color.GREEN);
        Flow flow2 = new Flow(new Position(1,1),new Position(0,4),Color.BLUE);
        Flow flow3 = new Flow(new Position(2,1),new Position(3,3),Color.RED);
        Flow flow4 = new Flow(new Position(4,0),new Position(4,4),Color.MAGENTA);
        flows.add(flow1);
        flows.add(flow2);
        flows.add(flow3);
        flows.add(flow4);
    }

    private int xToCol(int x) {
        return (x - getPaddingLeft()) / this.cellWidth;
    }

    private int yToRow(int y) {
        return (y - getPaddingTop()) / this.cellHeight;
    }

    private int colToX(int col) {
        return col * this.cellWidth + getPaddingLeft();
    }

    private int rowToY(int row) {
        return row * this.cellHeight + getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        for (Flow f : flows) {
            paintCircles.setColor(f.getColor());
            canvas.drawCircle(colToX(f.getStart().getX()) + cellWidth / 2, rowToY(f.getStart().getY()) + cellHeight / 2, cellWidth / 4, paintCircles);
            canvas.drawCircle(colToX(f.getEnd().getX()) + cellWidth / 2, rowToY(f.getEnd().getY()) + cellHeight / 2, cellWidth / 4, paintCircles);
        }

        //draw the grid
        for (int r = 0; r < NUM_CELLS; ++r) {
            for (int c = 0; c < NUM_CELLS; ++c) {
                int x = colToX(c);
                int y = rowToY(r);
                Rect rect = new Rect();
                rect.set(x, y, x + cellWidth, y + cellHeight);
                canvas.drawRect(rect, paintGrid);
            }
        }

        //draw all cellpaths
        for (Flow flow : flows) {
            path.reset();
            paintPath.setColor(flow.getColor());
            if (null != flow.getFlowPath() && flow.getFlowPath().size() > 0) {
                ArrayList<Position> positionlist = flow.getFlowPath().getFlowPathPositions();
                Position pos = positionlist.get(0);
                path.moveTo(colToX(pos.getX()) + cellWidth / 2,
                        rowToY(pos.getY()) + cellHeight / 2);
                for (int j = 1; j < positionlist.size(); j++) {
                    pos = positionlist.get(j);
                    path.lineTo(colToX(pos.getX()) + cellWidth / 2,
                            rowToY(pos.getY()) + cellHeight / 2);
                }
            }
            canvas.drawPath(path, paintPath);
        }
    }

    private boolean areNeighbours(int c1, int r1, int c2, int r2) {
        return Math.abs(c1 - c2) + Math.abs(r1 - r2) == 1;
    }

    public void setColor(int color) {
        paintPath.setColor(color);
        invalidate();
    }

    public void reset() {
        for (Flow flow : flows) {
            if (flow.getFlowPath() != null) {
                flow.getFlowPath().reset();
            }
        }
        this.invalidate();
    }

    public void initializeBoard() {

    }

    public Flow isInFlowPoints(Position pos) {
        for (Flow flow : flows) {
            if (flow.getStart().equals(pos) || flow.getEnd().equals(pos)) {
                return flow;
            }
        }
        return null;
    }

    public Flow isInFlowPaths(Position pos) {
        for (Flow flow : flows) {
            if (null != flow.getFlowPath()) {
                if (flow.getFlowPath().contains(pos)) {
                    return flow;
                }
            }
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(width, height);

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());

        this.paintPath.setStrokeWidth(cellWidth/4);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        int sw = Math.max(1, (int) paintGrid.getStrokeWidth());
        cellWidth = (xNew - getPaddingLeft() - getPaddingRight() - sw) / NUM_CELLS;
        cellHeight = (yNew - getPaddingTop() - getPaddingBottom() - sw) / NUM_CELLS;

        this.paintPath.setStrokeWidth(cellWidth/4);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();         // NOTE: event.getHistorical... might be needed.
        int y = (int) event.getY();
        int c = xToCol(x);
        int r = yToRow(y);

        if (c >= NUM_CELLS || r >= NUM_CELLS) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            Position pressed = new Position(c, r);
            Flow onPoint = isInFlowPoints(pressed);
            Flow onPath = isInFlowPaths(pressed);
            if (onPath != null) {
                onPath.getFlowPath().append(new Position(c, r));
                currentFlow = onPath;
            } else if (onPoint != null) {
                FlowPath newCellPath = new FlowPath();
                newCellPath.append(new Position(c, r));
                onPoint.setFlowPath(newCellPath);
                currentFlow = onPoint;
                }
            else {
                currentFlow = null;
            }
            this.invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (currentFlow != null) {
                if (null != currentFlow.getFlowPath() && !currentFlow.getFlowPath().isEmpty()) {
                    Position movedTo = new Position(c, r);
                    ArrayList<Position> PositionList = currentFlow.getFlowPath().getFlowPathPositions();
                    Position last = PositionList.get(PositionList.size() - 1);
                    Flow cross = isInFlowPaths(movedTo);
                    Flow onPoint = isInFlowPoints(movedTo);
                    if (areNeighbours(last.getX(), last.getY(), c, r) &&
                            //(cross == null || cross == currentFlow) && //check if no other path is crossed
                            !(currentFlow.finished() && cross != currentFlow) && //check if not extending current path over the endpoint
                            (onPoint == null || onPoint == currentFlow)) { //check if no other point is crossed
                        currentFlow.getFlowPath().append(movedTo);
                        if (cross != null && cross != currentFlow) {
                            cross.getFlowPath().remove(movedTo);
                        }
                        //check if all flows have been finished
                        boolean finished = true;
                        if (onPoint == currentFlow) {
                           for (Flow flow : this.flows) {
                                if (!flow.finished()) {
                                    finished = false;
                                }
                            }
                            //check if all Positions have been covered
                            if (finished) {
                                for (int i = 0; i < NUM_CELLS; i++) {
                                    for (int j = 0; j < NUM_CELLS; j++) {
                                        if (null == isInFlowPaths(new Position(i, j))) {
                                            finished = false;
                                            break;
                                        }
                                    }

                                }
                            }
                            if (finished) {
                                // custom dialog
                                final Dialog dialog = new Dialog(getContext());
                                //dialog.setContentView(R.layout.level_won_dialog_layout);
                                dialog.setTitle("Congrats - YOU WON ...");
/*
                                Button againButton = (Button) dialog.findViewById(R.id.level_again);
                                Button backMenuButton = (Button) dialog.findViewById(R.id.back_to_menu);
                                Button nextLevelButton = (Button) dialog.findViewById(R.id.next_level);


                                againButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        reset();
                                        dialog.dismiss();
                                    }
                                });
                                backMenuButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        mp.release();
                                        ((Activity) getContext()).finish();
                                    }
                                });
                                nextLevelButton.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        setNextPuzzle();
                                    }
                                });

*/
                                dialog.show();
                            }
                        }
                        this.invalidate();
                    }
                }
            }
        }

        return true;
    }


}
