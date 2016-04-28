package com.tp2.jms.meetingapp.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by JMS on 15/03/2016.
 */
public class MeetingDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "MEETING";
    public static final int DB_VERSION = 19;
    public static final String DB_TABLE_PROFILES = "PROFILES";
    public static final String DB_TABLE_GROUPS = "GROUPS";
    //Our main table
    private static final String sqlCreateTableProfiles =
            "CREATE TABLE " + DB_TABLE_PROFILES + "(" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " name TEXT NOT NULL," +
                    " email TEXT NOT NULL," +
                    " mac TEXT NOT NULL, " +
                    " preference1 TEXT NOT NULL, " +
                    " preference2 TEXT NOT NULL, " +
                    " preference3 TEXT NOT NULL " +
                    ");";
    private static final String sqlDropTableProfiles =  "DROP TABLE IF EXISTS " + DB_TABLE_PROFILES;
    public static final String[] TableProfilesCols = {"_id", "name", "email", "mac","preference1","preference2","preference3"};

    private static final String sqlCreateTableGroups =
            "CREATE TABLE " + DB_TABLE_GROUPS + "(" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " user_id INTEGER ," +
                    " name TEXT NOT NULL ,"+
                    " FOREIGN KEY(user_id) REFERENCES " +DB_TABLE_PROFILES+"(_id) "+
                    ");";
    private static final String sqlDropTableGroups =  "DROP TABLE IF EXISTS " + DB_TABLE_GROUPS;
    public static final String[] TableGroupsCols = {"_id", "user_id", "name"};

    /**
     * Create the database
     * @param context context of the activity which launches this class
     */
    public MeetingDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateTableProfiles);
        db.execSQL(sqlCreateTableGroups);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(sqlDropTableProfiles);
        db.execSQL(sqlDropTableGroups);
        onCreate(db);
    }

}
