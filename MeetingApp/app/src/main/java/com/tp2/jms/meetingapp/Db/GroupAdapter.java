package com.tp2.jms.meetingapp.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tp2.jms.meetingapp.Model.Group;
import com.tp2.jms.meetingapp.Model.Profile;

import java.util.ArrayList;

/**
 * Created by JMS on 16/03/2016.
 */
public class GroupAdapter {

    private SQLiteDatabase db;
    private MeetingDBHelper meetingdbHelper;
    private Context context;


    public GroupAdapter(Context c) {
        context = c;
    }

    /**
     * Open the database in order to do a CRUD action on it.
     * @return
     */
    public GroupAdapter open() {
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


    public long insertMember(String group_name,Profile profile) {
        long value = -1;
        open();
        String[] cols = MeetingDBHelper.TableGroupsCols;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[1], profile.getId());
        contentValues.put(cols[2], group_name);
        value = db.insert(MeetingDBHelper.DB_TABLE_GROUPS, null, contentValues);
        close();
        return value;
    }

    public long insertMember(int group_id,String group_name,Profile profile) {
        long value = -1;
        open();
        String[] cols = MeetingDBHelper.TableGroupsCols;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[0], group_id);
        contentValues.put(cols[1], profile.getId());
        contentValues.put(cols[2], group_name);
        String query = "SELECT _id,user_id FROM " + MeetingDBHelper.DB_TABLE_GROUPS +
                " WHERE _id=" +group_id + " and user_id="+profile.getId();
        Cursor c = db.rawQuery(query, new String[]{});
        if (!c.moveToFirst()) {
            value = db.insert(MeetingDBHelper.DB_TABLE_GROUPS, null, contentValues);
        }
        else{
            value = c.getInt(0);
        }
        close();
        return value;
    }

    public Group getGroup(int group_id) {
        open();
        String query = "SELECT _id,user_id,name FROM " + MeetingDBHelper.DB_TABLE_GROUPS +
                " WHERE _id=" + group_id ;
        Cursor c = db.rawQuery(query, new String[]{});

        Group group = new Group();
        ProfileAdapter pa = new ProfileAdapter(this.context);
        while (c.moveToFirst()) {
           Profile profile = pa.getProfile(c.getInt(1));
            group.getGroup().add(profile);
            group.setGroupname(c.getString(2));
        }
        close();
        return group;
    }

    public ArrayList<String> getGroupNames(){
        open();
        ArrayList<String> groupnames = new ArrayList<String>() ;
        String query = "SELECT UNIQUE name FROM " + MeetingDBHelper.DB_TABLE_GROUPS;
        Cursor c = db.rawQuery(query, new String[]{});
        while (c.moveToFirst()) {
            groupnames.add(c.getString(0));
        }
        close();
        return  groupnames;
    }

    public String getGroupName(int group_id){
        open();
        String query = "SELECT UNIQUE _id,name FROM " + MeetingDBHelper.DB_TABLE_GROUPS
                +" WHERE _id="+group_id;
        Cursor c = db.rawQuery(query, new String[]{});
        String groupname = "";
        if (c.moveToFirst()) {
            groupname = c.getString(1);
        }
        close();
        return  groupname;
    }




    public boolean isEmpty(){
        open();
        String query = "Select count(*) FROM "+ MeetingDBHelper.DB_TABLE_GROUPS;
        Cursor c = db.rawQuery(query, new String[]{});
        c.moveToFirst();
        int count = c.getInt(0);
        return count==0;
    }

}
