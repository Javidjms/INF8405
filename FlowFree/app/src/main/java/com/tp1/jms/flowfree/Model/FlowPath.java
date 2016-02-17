package com.tp1.jms.flowfree.Model;

import java.util.ArrayList;

/**
 * This class defines the path from the start to the end point of a flow
 * Created by JMS on 31/01/2016.
 */
public class FlowPath {

    private ArrayList<Position> flowpath = new ArrayList<Position>();

    /**
     * Append a point position on the flow path
     * @param pos position to add on the path
     */
    public void append(Position pos) {
        int index = flowpath.indexOf(pos);
        if(index < 0) {
            flowpath.add(pos);
        }
        else{
            removeFrom(index+1);
        }

    }

    /**
     * Remove all point position after the desired position on the flow path
     * @param pos position to remove
     */
    public void removeFrom(Position pos) {
        int index = flowpath.indexOf(pos);
        if (index >= 0) {
            removeFrom(index);
        }
    }

    /**
     * Remove all point position after the desired index point on the flow path
     * @param index index of the point position to remove
     */
    public void removeFrom(int index) {
        for (int i = flowpath.size() - 1; i >= index; i--) {
            flowpath.remove(i);
        }
    }

    /**
     * Check if the flow path contains the desired position
     * @param pos position to check
     * @return true if the flow path contains the position otherwise false
     */
    public boolean contains(Position pos) {
        return (flowpath.indexOf(pos) >= 0);
    }

    /**
     * Get the flow path
     * @return flowpath
     */
    public ArrayList<Position> getFlowPathPositions() {
        return flowpath;
    }

    /**
     * Reset the flow path
     */
    public void reset() {
        if (flowpath != null) {
            flowpath.clear();
        }
    }

    /**
     * Get the size of the flow path
     * @return length
     */
    public int size() {
        return flowpath.size();
    }

    /**
     * Check if the flow path is empty
     * @return true if its empty otherwise false
     */
    public boolean isEmpty() {
        return flowpath.isEmpty();
    }
}
