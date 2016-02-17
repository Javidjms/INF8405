package com.tp1.jms.flowfree.Model;

import java.util.ArrayList;

/**
 * This class defines a Level object. In fact it is for the DAO Level (LevelAdapter)
 * Created by JMS on 01/02/2016.
 */
public class Level {
    private int id;
    private ArrayList<Flow> flows;
    private int size;
    private int number;
    private boolean succeed;
    private boolean unlocked;
    private int highScore;

    /**
     * Create a level
     * @param id id of the level
     * @param flows arraylist of flows of the level
     * @param size size of the level
     * @param number number of the level
     * @param succeed success condition of the level
     * @param unlocked unlocked condition of the level
     */
    public Level(int id,ArrayList<Flow> flows, int size, int number,boolean succeed, boolean unlocked) {
        this.id = id;
        this.flows = flows;
        this.size = size;
        this.number = number;
        this.succeed = succeed;
        this.unlocked = unlocked;
        this.highScore = 0;
    }

    /**
     * Create a level
     * @param id id of the level
     * @param flows arraylist of flows of the level
     * @param size size of the level
     * @param number number of the level
     */
    public Level(int id,ArrayList<Flow> flows, int size, int number) {
        this(id,flows,size,number,false,false);
    }

    /**
     * Get the id of the level
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id of the level
     * @param id new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the flows of the level
     * @return flows
     */
    public ArrayList<Flow> getFlows() {
        return flows;
    }

    /**
     * Set the flows of the level
     * @param flows new arraylist of flows
     */
    public void setFlows(ArrayList<Flow> flows) {
        this.flows = flows;
    }

    /**
     * Get the grid size of the level
     * @return size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the grid size of the level
     * @param size new grid size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Check if the level is finished
     * @return true if it is succeed otherwise false
     */
    public boolean hasSucceed() {
        return succeed;
    }

    /**
     * Set the succeed condition of the level
     * @param succeed succeed condition
     */
    public void setSucceed(boolean succeed){
        this.succeed = succeed;
    }

    /**
     * Check if the level is unlocked
     * @return true if it is unlocked otherwise false
     */
    public boolean isUnlocked() {
        return unlocked;
    }
    /**
     * Set the unlocked condition of the level
     * @param unlocked unlocked condition
     */
    public void setUnlocked(boolean unlocked){
        this.unlocked = unlocked;
    }

    /**
     * Get the number of the level
     * @return number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set the number of the level
     * @param number new number of the level
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Get the high score of the level
     * @return high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Set the new high score of the level
     * @param highScore new high score of the level
     */
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }





}
