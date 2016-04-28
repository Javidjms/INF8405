package com.tp2.jms.meetingapp.Model;

import java.util.ArrayList;

/**
 * Created by JMS on 16/03/2016.
 */
public class Group  {

    private String groupname;
    private ArrayList<Profile> group;

    public Group(String groupname,Profile profile){
        group = new ArrayList<Profile>();
        this.groupname = groupname;
        group.add(profile);
    }
    public Group(){
        group = new ArrayList<Profile>();
    }

    public Profile getAdmin(){
        return group.get(0);
    }


    public String getGroupname() {
        return groupname;
    }

    public ArrayList<Profile> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<Profile> group) {
        this.group = group;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }


}
