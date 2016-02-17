package com.tp1.jms.flowfree.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tp1.jms.flowfree.Model.Flow;
import com.tp1.jms.flowfree.Model.Level;
import com.tp1.jms.flowfree.Model.Position;


import java.util.ArrayList;

/**
 * This class is the adapter of the level object which retrieves object directly on the database
 * Created by JMS on 02/02/2016.
 */
public class LevelAdapter {

    private SQLiteDatabase db;
    private LevelDBHelper leveldbHelper;
    private Context context;


    public LevelAdapter(Context c) {
        context = c;
    }

    /**
     * Open the database in order to do a CRUD action on it.
     * @return
     */
    public LevelAdapter open() {
        leveldbHelper = new LevelDBHelper(context);
        db = leveldbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close the database
     */
    public void close() {
        db.close();
    }

    /**
     * Convert Flow object into String format
     * @param flows arraylist of flows
     * @return string format of flows
     */
    public String stringformatFlow(ArrayList<Flow> flows) {
        String strflows = "";
        for(Flow flow:flows){
            strflows+="#"+flow.getStart().getX() + "-";
            strflows+=flow.getStart().getY();
            strflows+="|"+flow.getEnd().getX() + "-";
            strflows+=flow.getEnd().getY();
            strflows+="|"+flow.getColor();
        }
        return strflows;

    }

    /**
     * Convert String format into Flow object
     * @param strflows flows in string format
     * @return arraylist of flows
     */
    public ArrayList<Flow> stringdeformatFlow(String strflows) {
        ArrayList<Flow> flows = new ArrayList<Flow>();
        String[] allflows = strflows.split("#");
        for(String af:allflows){
            if(!af.isEmpty()){
                String[] flowparts = af.split("\\|");
                String[] startpart = flowparts[0].split("-");
                String[] endpart = flowparts[1].split("-");
                String color = flowparts[2];
                Flow flow = new Flow(new Position(Integer.parseInt(startpart[0]),Integer.parseInt(startpart[1])),
                        new Position(Integer.parseInt(endpart[0]),Integer.parseInt(endpart[1])),
                        Integer.parseInt(color));
                flows.add(flow);
            }
        }

        return  flows;
    }

    /**
     * Insert a level on the database
     * @param level level that you want to insert on the db
     * @return id of the level
     */
    public long insertLevel(Level level) {
        long value = -1;
        open();
        String[] cols = LevelDBHelper.TableLevelsCols;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[0], level.getId());
        contentValues.put(cols[1], level.getSize());
        contentValues.put(cols[2], level.getNumber());
        contentValues.put(cols[3], stringformatFlow(level.getFlows()));
        contentValues.put(cols[4], level.hasSucceed() ? 1 : 0);
        contentValues.put(cols[5], level.isUnlocked() ? 1 : 0);
        contentValues.put(cols[6], 0);
        String query = "SELECT _id FROM " + LevelDBHelper.TableLevels +
                " WHERE _id='" +level.getId() + "'";
        Cursor c = db.rawQuery(query, new String[]{});
        if (!c.moveToFirst()) {
            value = db.insert(LevelDBHelper.TableLevels, null, contentValues);
        }
        else{
            value = level.getId();
        }
        close();
        return value;
    }

    /**
     * Get a level with a desired id from the datebase
     * @param _id id of the level
     * @return the level with the  desired id
     */
    public Level getLevel(int _id) {
        open();
        String query = "SELECT _id,size,number,flows,succeed,unlocked,highscore FROM " + LevelDBHelper.TableLevels +
                " WHERE _id='" + _id + "'";
        Cursor c = db.rawQuery(query, new String[]{});

        Level level = null;
        if (c.moveToFirst()) {
            level = new Level(c.getInt(0), stringdeformatFlow(c.getString(3)),c.getInt(1),c.getInt(2), c.getInt(4)==1, c.getInt(5)==1);
            level.setHighScore(c.getInt(6));
        }
        close();
        return level;
    }

    /**
     * Get a level with a desired size and level from the datebase
     * @param size size of the level
     * @param number number of the level
     * @return the level with the desired parameter
     */
    public Level getLevel(int size,int number) {
        open();
        String query = "SELECT _id,size,number,flows,succeed,unlocked,highscore FROM " + LevelDBHelper.TableLevels +
                " WHERE size="+size+" AND number="+number;
        Cursor c = db.rawQuery(query, new String[]{});

        Level level = null;
        if (c.moveToFirst()) {
            level = new Level(c.getInt(0), stringdeformatFlow(c.getString(3)),c.getInt(1),c.getInt(2), c.getInt(4)==1, c.getInt(5)==1);
            level.setHighScore(c.getInt(6));
        }
        close();
        return level;
    }

    /**
     * Get all possibles level grid sizes
     * @return arraylist of sizes
     */
    public ArrayList<Integer> getLevelSizes() {
        ArrayList<Integer> sizes = new ArrayList<Integer>();
        open();
        String query = "SELECT DISTINCT size FROM " + LevelDBHelper.TableLevels;
        Cursor c = db.rawQuery(query, new String[]{});

        while (c.moveToNext()) {
            sizes.add(c.getInt(0));
        }

        close();
        return sizes;
    }

    /**
     * Get all possibles level numbers with the desired size
     * @param size size of the level
     * @return arraylist of level numbers
     */
    public ArrayList<Integer> getLevelNumbers(int size) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        open();
        String query = "SELECT DISTINCT number FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size ;
        Cursor c = db.rawQuery(query, new String[]{});

        while (c.moveToNext()) {
            numbers.add(c.getInt(0));
        }

        close();
        return numbers;
    }

    /**
     * Get all unlocked level numbers with the desired size
     * @param size size of the level
     * @return arraylist of unlocked level numbers
     */
    public ArrayList<Integer> getUnlockedLevelNumbers(int size) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        open();
        String query = "SELECT DISTINCT number FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size + " and unlocked=1 ";
        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToNext()) {
            numbers.add(c.getInt(0));
        }

        close();
        return numbers;
    }

    /**
     * Get all succeed level numbers with the desired size
     * @param size size of the level
     * @return arraylist of succeed level number
     */
    public ArrayList<Integer> getSucceedLevelNumbers(int size) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        open();
        String query = "SELECT DISTINCT number FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size + " and succeed=1 ";
        Cursor c = db.rawQuery(query, new String[]{});

        while (c.moveToNext()) {
            numbers.add(c.getInt(0));
        }

        close();
        return numbers;
    }

    /**
     * Get the level highscore from a desired size and number
     * @param size size of the level
     * @param number number of the level
     * @return highscore of the level
     */
    public int getLevelHighscore(int size,int number) {
        int highscore =0;
        open();
        String query = "SELECT highscore FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size + " AND number="+ number ;
        Cursor c = db.rawQuery(query, new String[]{});

        if (c.getCount() == 1 && c.moveToFirst()) {
            highscore = c.getInt(0);
        }

        close();
        return highscore;
    }

    /**
     * Set the level succeed (done)
     * @param size size of the level
     * @param number number of the level
     * @return id of the level
     */
    public long setLevelSuccess(int size,int number) {
        long value = -1;
        open();
        String query = "SELECT _id FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size + " AND number="+ number;
        Cursor c = db.rawQuery(query, new String[]{});
        c.moveToFirst();
        String[] cols = LevelDBHelper.TableLevelsCols;
        ContentValues contentValues = new ContentValues();
        int succeed = 1;
        contentValues.put(cols[4], succeed);
        value = db.update(LevelDBHelper.TableLevels, contentValues, "_id=" + c.getLong(0), null);
        close();
        return value;
    }

    /**
     * Set the level unlocked
     * @param size size of the level
     * @param number number of the level
     * @return id of the level
     */
    public long setLevelUnlocked(int size,int number) {
        long value = -1;
        open();
        String query = "SELECT _id FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size + " AND number="+ number;
        Cursor c = db.rawQuery(query, new String[]{});
        c.moveToFirst();
        String[] cols = LevelDBHelper.TableLevelsCols;
        ContentValues contentValues = new ContentValues();
        int unlocked = 1;
        contentValues.put(cols[5], unlocked);
        value = db.update(LevelDBHelper.TableLevels, contentValues, "_id=" + c.getLong(0), null);
        close();
        return value;
    }

    /**
     * Set the new level highscore
     * @param size size of the level
     * @param number number of the level
     * @param highscore new highscore of the level
     * @return id of the level
     */
    public long setLevelHighScore(int size,int number,int highscore) {
        long value = -1;
        open();
        String query = "SELECT _id FROM " + LevelDBHelper.TableLevels+
                " WHERE size=" + size + " AND number="+ number;
        Cursor c = db.rawQuery(query, new String[]{});
        c.moveToFirst();
        String[] cols = LevelDBHelper.TableLevelsCols;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[6], highscore);
        value = db.update(LevelDBHelper.TableLevels, contentValues, "_id=" + c.getLong(0), null);
        close();
        return value;
    }

    /**
     * Init the table with the XML Level Parser
     */
    public void initTableLevels() {
        ArrayList<Level> levels = XMLLevelParser.readLevels(context);
        for (Level level : levels) {
			level.setUnlocked(level.getNumber() == 1);
            long id = this.insertLevel(level);
        }

    }


    /**
     * Get the number of flows of a level
     * @param size size of the level
     * @param number number of the level
     * @return number of flows of the desired level
     */
    public int getNbLevelFlows(int size, int number) {
    open();
    String query = "SELECT flows FROM " + LevelDBHelper.TableLevels+
            " WHERE size=" + size + " AND number="+ number;
    Cursor c = db.rawQuery(query, new String[]{});

    int nbflows =0;
    if (c.moveToFirst()) {
        nbflows= stringdeformatFlow(c.getString(0)).size();
    }
    close();
    return nbflows;
    }
}
