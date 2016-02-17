package com.tp1.jms.flowfree.Model;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * This class describes a flow object which content a start and end point and its will be linked by the flow path
 * Created by JMS on 31/01/2016.
 */
public class Flow {

    private Position start;
    private Position end;
    private int color;
    private FlowPath flowPath;

    /**
     * Create a flow object with its color, its start and end point
     * @param start start position of the start point
     * @param end end position of the end point
     * @param color color of the flows point
     */
    public Flow(Position start, Position end, int color) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.flowPath = new FlowPath();
    }

    /**
     * Get the start point
     * @return start point
     */
    public Position getStart() {
        return start;
    }

    /**
     * Set the start point
     * @param start new start point
     */
    public void setStart(Position start) {
        this.start = start;
    }

    /**
     * Get the end point
     * @return end point
     */
    public Position getEnd() {
        return end;
    }

    /**
     * Set the end point
     * @param end new end point
     */
    public void setEnd(Position end) {
        this.end = end;
    }

    /**
     * Get the color of the flow
     * @return color
     */
    public int getColor() {
        return color;
    }

    /**
     * Set the color of the flow
     * @param color new color to set on it
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * Get the flow path object
     * @return flowpath
     */
    public FlowPath getFlowPath() {
        return flowPath;
    }

    /**
     * Get all of the flow path point positions
     * @return flowpath positions
     */
    public ArrayList<Position> getFlowPathPositions(){
        return  flowPath.getFlowPathPositions();
    }

    /**
     * Set the flow path object
     * @param flowPath  new flowpath
     */
    public void setFlowPath(FlowPath flowPath) {
        this.flowPath = flowPath;
    }

    /**
     * Check if the flow is finished (ie if the start point and the point are linked )
     * @return
     */
    public boolean finished() {
        if (null != this.flowPath) {
            return this.flowPath.contains(this.start) && this.flowPath.contains(this.end);
        }
        return false;
    }


}
