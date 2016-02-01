package com.tp1.jms.flowfree;

/**
 * Created by JMS on 31/01/2016.
 */
public class Board {

    private Square board[][];


    public Board(int size) {
        board = new Square[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                board[i][j] = new Square(i, j);
            }
        }
    }

    public Square getSquare(int x, int y) {
        return board[x][y];
    }

    public int size() {
        return board.length;
    }

    public boolean isFilled(){
        for (int i = 0; i < this.size(); ++i) {
            for (int j = 0; j < this.size(); ++j) {
               if(board[i][j].isEmptySquare())
                   return false;
            }
        }
        return true;
    }

}
