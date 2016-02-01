package com.tp1.jms.flowfree;

/**
 * Created by JMS on 31/01/2016.
 */
public class Square {

    private Flow flow;
    private int x;
    private int y;


    public Square(Position pos) {
        this.flow = null;
        this.x = pos.getX();
        this.y = pos.getY();
    }

    public Square(int x, int y) {
        this.flow = null;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Flow getFlow() {
        return flow;
    }

    public boolean isEmptySquare() {
        return flow == null;
    }

    public void setEmptySquare() {
        flow = null;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }


}
