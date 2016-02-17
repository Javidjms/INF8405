package com.tp1.jms.flowfree.Model;

import java.util.ArrayList;

/**
 * This class describes the board which composed of squares where flows can be set on
 * Created by JMS on 31/01/2016.
 */
public class Board {

    private Square board[][];

    /**
     * Init the Board with the size desired ( size x size ) squares
     * @param size
     */
    public Board(int size) {
        board = new Square[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                board[i][j] = new Square(i, j);
            }
        }
    }

    /**
     * Get the square at the position pos
     * @param pos position where to retrieve the square
     * @return square
     */
    public Square getSquare(Position pos) {
        return getSquare(pos.getX(),pos.getY());
    }

    /**
     * Get the square at the coordinate x and y
     * @param x coordinate x of the desired square
     * @param y coordinate y of the desired square
     * @return square
     */
    public Square getSquare(int x, int y) {
        return board[x][y];
    }

    /**
     * Get the size of the board
     * @return length
     */
    public int size() {
        return board.length;
    }

    /**
     * Check if the board is filled
     * @return true if it is filled otherwise false
     */
    public boolean isFilled(){
        for (int i = 0; i < this.size(); ++i) {
            for (int j = 0; j < this.size(); ++j) {
               if(board[i][j].isEmptySquare())
                   return false;
            }
        }
        return true;
    }

    /**
     * Check if the flow is a flow point
     * @param pos position of the square
     * @return true it it is a floww point otherwise false
     */
    public Flow isFlowPoint(Position pos) {
        Square square = getSquare(pos);
        if(square.isFlowPoint()){
            return square.getFlow();
        }
        return null;
    }

    /**
     * Check if the flow is a flow path point
     * @param pos position of the square
     * @return true it it is a floww path point otherwise false
     */
    public Flow isFlowPathPoint(Position pos) {
        Square square = getSquare(pos);
        if(square.isFlowPathPoint()){
            return square.getFlow();
        }
        return null;
    }

    /**
     * Remove a flow path point from a flow at the desired square
     * @param flow flow to edit flowpath
     * @param pos position of the square
     */
    public void removeFlowPathFrom(Flow flow ,Position pos) {
        ArrayList<Position> f = flow.getFlowPathPositions();
        int index = f.indexOf(pos);
        if (index >= 0) {
            for (int i = f.size() - 1; i > index; i--) {
                Position removedpos = f.get(i);
                getSquare(removedpos).setEmptySquare();
            }
        }

    }

    /**
     * Add a flow path point from a flow at the desired square
     * @param flow flow to edit flowpath
     * @param position position of the square
     */
    public void addFlowPath(Flow flow,Position position) {
        if(!flow.getFlowPath().isEmpty()) {
            ArrayList<Position> f = flow.getFlowPathPositions();
            int index = f.indexOf(position);
            if (index < 0) {
                getSquare(position).setFlow(flow);
            } else {
                for (int i = f.size() - 1; i > index; i--) {
                    Position removedpos = f.get(i);
                    getSquare(removedpos).setEmptySquare();
                }
            }
        }
    }

    /**
     * Remove a flow path point from a flow
     * @param flow flow to edit flowpath
     */
    public void removeFlowPathFrom(Flow flow) {
        ArrayList<Position> f = flow.getFlowPathPositions();
        for (int i = f.size() - 1; i >=0; i--) {
            Position removedpos = f.get(i);
            getSquare(removedpos).setEmptySquare();
        }
    }

    /**
     * Get the board
     * @return board
     */
    public Square[][] getBoard() {
        return board;
    }
}
