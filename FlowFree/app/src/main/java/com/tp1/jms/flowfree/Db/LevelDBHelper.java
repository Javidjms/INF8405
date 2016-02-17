package com.tp1.jms.flowfree.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * This class is a DBHelper which creates the database and inputs data on it
 * Created by JMS on 01/02/2016.
 */
public class LevelDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "FLOW_FREE_DB";
    public static final int DB_VERSION = 19;
    public static final String TableLevels = "levels";
    //Our main level table
    private static final String sqlCreateTableLevels =
            "CREATE TABLE " + TableLevels + "(" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " size INTEGER NOT NULL," +
                    " number INTEGER NOT NULL," +
                    " flows TEXT NOT NULL UNIQUE," +
                    " succeed INTEGER NOT NULL," +
                    " unlocked INTEGER NOT NULL," +
                    " highscore INTEGER" +
                    ");";
    private static final String sqlDropTableLevels =  "DROP TABLE IF EXISTS " + TableLevels;
    public static final String[] TableLevelsCols = {"_id", "size", "number", "flows",
            "succeed","unlocked" , "highscore"
    };

    /**
     * Create the database
     * @param context context of the activity which launches this class
     */
    public LevelDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTableLevels);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(sqlDropTableLevels);
        onCreate(db);
    }
}
