package com.tp2.jms.meetingapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tp2.jms.meetingapp.Model.Profile;

import java.util.ArrayList;

/**
 * Created by JMS on 15/03/2016.
 */
public class ProfileAdapter {

    private SQLiteDatabase db;
    private MeetingDBHelper meetingdbHelper;
    private Context context;


    public ProfileAdapter(Context c) {
        context = c;
    }

    /**
     * Open the database in order to do a CRUD action on it.
     * @return
     */
    public ProfileAdapter open() {
        meetingdbHelper = new MeetingDBHelper(context);
        db = meetingdbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Close the database
     */
    public void close() {
        db.close();
    }

    public long insertProfile(Profile profile) {
        long value = -1;
        open();
        String[] cols = MeetingDBHelper.TableProfilesCols;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[1], profile.getName());
        contentValues.put(cols[2], profile.getEmail());
        contentValues.put(cols[3], profile.getMac());
        contentValues.put(cols[4], profile.getPreferences().get(0));
        contentValues.put(cols[5], profile.getPreferences().get(1));
        contentValues.put(cols[6], profile.getPreferences().get(2));
        String query = "SELECT mac FROM " + MeetingDBHelper.DB_TABLE_PROFILES +
                " WHERE mac='" +profile.getMac() + "'";
        Cursor c = db.rawQuery(query, new String[]{});
        if (!c.moveToFirst()) {
            value = db.insert(MeetingDBHelper.DB_TABLE_PROFILES, null, contentValues);
        }
        else{
            value = c.getInt(0);
        }
        close();
        return value;
    }

    public Profile getProfile(String mac) {
        open();
        String query = "SELECT _id,name,email,mac,preference1,preference2,preference3 FROM " + MeetingDBHelper.DB_TABLE_PROFILES +
                " WHERE mac='" + mac + "'";
        Cursor c = db.rawQuery(query, new String[]{});

        Profile profile = null;
        if (c.moveToFirst()) {
            ArrayList<String> preferences = new ArrayList<String>();
            preferences.add(c.getString(4));preferences.add(c.getString(5));preferences.add(c.getString(6));
            profile = new Profile(c.getString(1),c.getString(3), c.getString(2),preferences);
            profile.setId(c.getInt(0));
        }
        close();
        return profile;
    }

    public Profile getProfile(int id) {
        open();
        String query = "SELECT _id,name,email,mac,preference1,preference2,preference3 FROM " + MeetingDBHelper.DB_TABLE_PROFILES +
                " WHERE _id=" + id ;
        Cursor c = db.rawQuery(query, new String[]{});

        Profile profile = null;
        if (c.moveToFirst()) {
            ArrayList<String> preferences = new ArrayList<String>();
            preferences.add(c.getString(4));preferences.add(c.getString(5));preferences.add(c.getString(6));
            profile = new Profile(c.getString(1),c.getString(3), c.getString(2),preferences);
            profile.setId(c.getInt(0));
        }
        close();
        return profile;
    }


    public long updateProfile(Profile profile) {
        long value = -1;
        open();
        String[] cols = MeetingDBHelper.TableProfilesCols;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[1], profile.getName());
        contentValues.put(cols[2], profile.getEmail());
        contentValues.put(cols[3], profile.getMac());
        contentValues.put(cols[4], profile.getPreferences().get(0));
        contentValues.put(cols[5], profile.getPreferences().get(1));
        contentValues.put(cols[6], profile.getPreferences().get(2));
        String query = "SELECT _id,mac FROM " + MeetingDBHelper.DB_TABLE_PROFILES +
                " WHERE mac='" +profile.getMac() + "'";
        Cursor c = db.rawQuery(query, new String[]{});
        if (!c.moveToFirst()) {
            value = db.insert(MeetingDBHelper.DB_TABLE_PROFILES, null, contentValues);
        }
        else{
            value = db.update(MeetingDBHelper.DB_TABLE_PROFILES, contentValues, "_id=" + c.getLong(0), null);
        }
        close();
        return value;
    }

    public boolean isEmpty(){
        open();
        String query = "Select count(*) FROM "+ MeetingDBHelper.DB_TABLE_PROFILES;
        Cursor c = db.rawQuery(query, new String[]{});
        c.moveToFirst();
        int count = c.getInt(0);
        return count==0;
    }



}
