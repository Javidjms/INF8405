package com.tp1.jms.flowfree;

import java.util.ArrayList;

/**
 * Created by JMS on 31/01/2016.
 */
public class FlowPath {

    private ArrayList<Position> flowpath = new ArrayList<Position>();

    public void append(Position pos) {
        int index = flowpath.indexOf(pos);
        if(index < 0) {
            flowpath.add(pos);
        }
        else{
            removeFrom(index);
        }

    }

    public void remove(Position pos) {
        int index = flowpath.indexOf(pos);
        if (index >= 0) {
            removeFrom(index);
        }
    }

    public void removeFrom(int index) {
        for (int i = flowpath.size() - 1; i >= index; i--) {
            flowpath.remove(i);
        }
    }

    public boolean contains(Position pos) {
        return (flowpath.indexOf(pos) >= 0);
    }

    public ArrayList<Position> getFlowPathPositions() {
        return flowpath;
    }

    public void reset() {
        if (flowpath != null) {
            flowpath.clear();
        }
    }

    public int size() {
        return flowpath.size();
    }

    public boolean isEmpty() {
        return flowpath.isEmpty();
    }
}
