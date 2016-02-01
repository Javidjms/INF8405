package com.tp1.jms.flowfree;

/**
 * Created by JMS on 31/01/2016.
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) {
            return false;
        }
        Position pos = (Position) o;
        return pos.getX() == this.getX() && pos.getY() == this.getY();
    }
}
