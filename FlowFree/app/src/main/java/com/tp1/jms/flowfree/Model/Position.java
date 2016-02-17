package com.tp1.jms.flowfree.Model;

/**
 * This class describes the position in coordinate (x,y)
 * Created by JMS on 31/01/2016.
 */
public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Set the x coordinate
     * @param newX new x coordinate
     */
    public void setX(int newX) {
        x = newX;
    }

    /**
     * Set the y coordinate
     * @param newY new y coordinate
     */
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
