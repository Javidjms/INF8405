package com.tp1.jms.flowfree.Controller;

import android.content.Context;
import android.widget.Toast;
import com.tp1.jms.flowfree.Db.LevelAdapter;
import com.tp1.jms.flowfree.Model.Board;
import com.tp1.jms.flowfree.Model.Flow;
import com.tp1.jms.flowfree.Model.FlowPath;
import com.tp1.jms.flowfree.Model.Level;
import com.tp1.jms.flowfree.Model.Position;
import com.tp1.jms.flowfree.Model.Square;
import com.tp1.jms.flowfree.R;

import java.util.ArrayList;

/**
 * This class describes the main game state of a level . It could be initialized, onplayed ,finished and losed
 * Created by JMS on 03/02/2016.
 */
public class GameState {
    private Level currentLevel;
    private Board board;
    private Flow currentFlow;
    private int moves;

    /**
     * First initalisation of the level
     * @param level level to initialise
     */
    public GameState(Level level){
        initialize(level);
    }

    /**
     * Get the current level
     * @return current level
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Get the flows of the current level
     * @return flows
     */
    public ArrayList<Flow> getLevelFlows(){
        return currentLevel.getFlows();
    }

    /**
     * Reset the  current level
     */
    public void reset() {
        ArrayList<Flow> flows = getLevelFlows();
        for (Flow flow : flows) {
            if (flow.getFlowPath() != null) {
                flow.getFlowPath().reset();
            }
        }
        for(Square[] squares:board.getBoard()){
            for(Square s:squares){
                s.setEmptySquare();
            }
        }
        currentFlow = null;
        moves = 0;
    }

    /**
     * Initialise the level by setting the flows point on the board
     * @param level level to initialize
     */
    public void initialize(Level level) {
        currentLevel = level;
        board = new Board(level.getSize());
        currentFlow = null;
        moves = 0;
        for(Flow f:level.getFlows()){
            Position start = f.getStart();
            Position end = f.getEnd();
            board.getSquare(start).setFlow(f,true);
            board.getSquare(end).setFlow(f,true);
        }
    }

    /**
     * Check if the desired point is a flow point
     * @param pos position of the desired square
     * @return true if it is a flow point otherwise false
     */
    public Flow isFlowPoint(Position pos) {
        return board.isFlowPoint(pos);
    }
    /**
     * Check if the desired point is a flow path point
     * @param pos position of the desired square
     * @return true if it is a flow path point otherwise false
     */
    public Flow isFlowPathPoint(Position pos) {
        return board.isFlowPathPoint(pos);
    }

    /**
     * Check if the two position are neighbours
     * @param pos1 position of the first square to compare
     * @param pos2 position of the second square to compare
     * @return true if it is neighbours otherwise false
     */
    public boolean areNeighbours(Position pos1, Position pos2) {
        return areNeighbours(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
    }

    /**
     * /**
     * Check if the two position are neighbours
     * @param x1 coordinate x of the first square to compare
     * @param y1 coordinate y of the first square to compare
     * @param x2 coordinate x of the second  square to compare
     * @param y2 coordinate y of the second square to compare
     * @return true if it is neighbours otherwise false
     */
    public boolean areNeighbours(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2) == 1;
    }

    /**
     * Create or retrieve the flow path (by start point of unfinished flow path point)
     * @param position position of the desired square
     * @return current flow
     */
    public Flow addFlowPath(Position position) {
        Flow onFlowPoint = isFlowPoint(position);
        Flow onFlowPathPoint = isFlowPathPoint(position);
        if (onFlowPathPoint != null) { // Check if the selected square is a flow path point
            board.addFlowPath(onFlowPathPoint, position); // Set the flow on the square
            onFlowPathPoint.getFlowPath().append(position); // Append position on the flow path
            if(currentFlow != onFlowPathPoint){ // Update moves
                moves++;
            }
            currentFlow = onFlowPathPoint; // Update currentflow

        } else if (onFlowPoint != null) { // Check if the selected square is a flow point (start point or end point )
            board.removeFlowPathFrom(onFlowPoint); // Remove the flow on the square
            FlowPath fp = new FlowPath(); // Recreate a new flow path
            board.addFlowPath(onFlowPoint, position); // Add the flow on the square
            fp.append(position); // Append position on the new flow path
            onFlowPoint.setFlowPath(fp); // Set the flow path on the flow
            if(currentFlow != onFlowPoint){ // Update moves
                moves++;
            }
            currentFlow = onFlowPoint; // Update currentflow
        }
        else {
            currentFlow = null;
        }
        return currentFlow;
    }

    /**
     * Update the flow path when the user are moving from a current flow
     * @param position moved position
     * @return true if the flow path was updated otherwise false
     */
    public boolean updateFlowPath(Position position) {
        if (currentFlow != null) {
            if (currentFlow.getFlowPath()!= null && !currentFlow.getFlowPath().isEmpty()) { // Check if the flow path is not empty
                ArrayList<Position> positionList = currentFlow.getFlowPathPositions(); // Get all flow path positions
                Position lastPosition = positionList.get(positionList.size() - 1); // Get the last positon on the flow path
                Flow onFlowPoint = isFlowPoint(position);
                Flow onFlowPathPoint = isFlowPathPoint(position);
                if (!(currentFlow.finished() && onFlowPathPoint != currentFlow) && // Check if the flow is not finished and if we cross another
                                                                                    // flow path point
                        areNeighbours(position,lastPosition) && // Check if the last position and the new selected position are neighbour
                        (onFlowPoint == null || onFlowPoint == currentFlow)) { // Check if the flow point is null or if we arrive to end
                                                                                // point we assure that it belongs to the current flow
                    board.addFlowPath(currentFlow, position); // add flow on the selected square
                    currentFlow.getFlowPath().append(position); // append the flow on the flow path
                    if (onFlowPathPoint != null && onFlowPathPoint != currentFlow) { // if we cross another flow path point
                        board.removeFlowPathFrom(onFlowPathPoint,position); // We removed this crossed flow path point on the square
                        onFlowPathPoint.getFlowPath().removeFrom(position); // We removed this crossed flow path point its flow path
                    }
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Check it the player won the game
     * @param position of the last selected square
     * @return true if the player won otherwise false
     */
    public boolean checkWin(Position position) {
        Flow onFlowPoint = isFlowPoint(position);
        // check if the player covers all the board and all flows are finished
        return onFlowPoint == currentFlow && checkAllFlowsFinished() && checkBoardFilled();
    }

    /**
     * Check if the board are completely covered
     * @return true if the board is filled otherwise false
     */
    public boolean checkBoardFilled() {
        return board.isFilled();
    }

    /**
     * Check if all flows are completely linked (finished)
     * @return true if all flows are finished otherwise false
     */
    public boolean checkAllFlowsFinished() {
        ArrayList<Flow> flows = getLevelFlows();
        for (Flow f : flows) {
            if (!f.finished()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the current number of finished flows
     * @return number of finished flows
     */
    public int getnbFinishedFlow(){
       int finishedflow = 0;
        ArrayList<Flow> flows = getLevelFlows();
        for (Flow f : flows) {
            if (f.finished()) {
                finishedflow++;
            }
        }
        return finishedflow;
    }

    /**
     * Get the number of flows
     * @return number of flows
     */
    public int getnbFlow(){
        return getLevelFlows().size();
    }

    /**
     * Get the number of empty square
     * @return number of empty sqaure
     */
    public int getnbEmptySquare(){
        int nbemptysquare =0;
        for(Square[] line:board.getBoard()){
            for(Square s:line){
                if(s.isEmptySquare() || (s.isFlowStartPoint() && !s.getFlow().finished())){
                    nbemptysquare++;
                }
            }
        }
        return nbemptysquare;
    }


    /**
     * Get the number of moves of the current level
     * @return moves
     */
    public int getMoves() {
        return moves;
    }

    /**
     * Get the high score of the current level
     * @return high score
     */
    public int getHighScore() {
        return currentLevel.getHighScore();
    }

    /**
     * Victory event when the player won the game
     * @param context current context
     */
    public void victory(Context context) {
        int nextlevelnumber = currentLevel.getNumber() +1;
        LevelAdapter leveladapter = new LevelAdapter(context);
        leveladapter.setLevelSuccess(currentLevel.getSize(), currentLevel.getNumber());
        int h = leveladapter.getLevelHighscore(currentLevel.getSize(), currentLevel.getNumber());
        if( h> moves || h==0){
            leveladapter.setLevelHighScore(currentLevel.getSize(), currentLevel.getNumber(), moves);
            Toast.makeText(context, R.string.new_highscore_notification, Toast.LENGTH_SHORT).show();
        }
        if(leveladapter.getLevelNumbers(currentLevel.getSize()).contains(nextlevelnumber) && !leveladapter.getSucceedLevelNumbers(currentLevel.getSize()).contains(nextlevelnumber)) {
            leveladapter.setLevelUnlocked(currentLevel.getSize(), nextlevelnumber);
            Toast.makeText(context, R.string.new_level_unlocked_notification, Toast.LENGTH_SHORT).show();
        }
    }
}
