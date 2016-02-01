package com.tp1.jms.flowfree;

import android.graphics.Color;

/**
 * Created by JMS on 31/01/2016.
 */
public class Flow {

    private Position start;
    private Position end;
    private int color;
    private FlowPath flowPath;

    public static int[] colors = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN,
            Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.MAGENTA, Color.WHITE
        };

    public Flow(Position start, Position end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getEnd() {
        return end;
    }

    public void setEnd(Position end) {
        this.end = end;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public FlowPath getFlowPath() {
        return flowPath;
    }

    public void setFlowPath(FlowPath flowPath) {
        this.flowPath = flowPath;
    }

    public boolean finished() {
        if (null != this.flowPath) {
            return this.flowPath.contains(this.start) && this.flowPath.contains(this.end);
        }
        return false;
    }


}
