package com.tp1.jms.flowfree.Model;

/**
 * This class describes a square from the board which can contain a flow path
 * Created by JMS on 31/01/2016.
 */
public class Square {

    private boolean notErasable; // Special boolean in order to not erase the flowpath at the startpoint of the square
    private Flow flow;
    private Position position;

    public Square(Position pos) {
        this.flow = null;
        position = pos ;
    }

    public Square(int x, int y) {
        this.flow = null;
        position = new Position(x,y);
        notErasable = false;
    }

    /**
     * Get the x coordinate of this square
     * @return x
     */
    public int getX() {
        return position.getX();
    }

    /**
     * Get the y coordinate of this square
     * @return y
     */
    public int getY() {
        return position.getY();
    }

    /**
     * Get the flow on this square
     * @return flow
     */
    public Flow getFlow() {
        return flow;
    }

    /**
     * Check if the square is an empty square
     * @return true if it is empty otherwise false
     */
    public boolean isEmptySquare() {
        return flow == null;
    }

    /**
     * Set this square an empty square
     */
    public void setEmptySquare() {
        if(!this.notErasable) {
            flow = null;
        }
    }

    /**
     * Set a flow on this square with the property of the flag notErasable
     * @param flow flow to set on this square
     * @param notErasable boolean if the square is permanent like a flow point
     */
    public void setFlow(Flow flow,boolean notErasable) {
        if(!this.notErasable) {
            this.flow = flow;
            this.notErasable = notErasable;
        }
    }

    /**
     * Set a flow on this square
     * @param flow flow to set on this square
     */
    public void setFlow(Flow flow) {
        setFlow(flow,false);
    }

    /**
     * Check if the flow is a flow point
     * @return true it it is a floww point otherwise false
     */
    public boolean isFlowPoint(){
        if(isEmptySquare()){
            return false;
        }
        else{
            return flow.getStart().equals(position) || flow.getEnd().equals(position);
        }

    }

    /**
     * Check if the flow is a flow path point
     * @return true it it is a floww path point otherwise false
     */
    public boolean isFlowPathPoint(){
        if(isEmptySquare()){
            return false;
        }
        else{
            return !isFlowPoint();
        }

    }

    /**
     * Check if the flow is a flow start point
     * @return true it it is a floww start point otherwise false
     */
    public boolean isFlowStartPoint(){
        if(isEmptySquare()){
            return false;
        }
        else{
            return flow.getStart().equals(position);
        }

    }

}