package com.tp2.jms.meetingapp.Model;

import java.util.ArrayList;

/**
 * Created by JMS on 15/03/2016.
 */
public class Profile {

    private String name;
    private String mac;
    private String email;
    private int id;
    private ArrayList<String> preferences;
    private String preference1,preference2,preference3;

    public Profile(String name, String mac, String email,ArrayList<String> preferences) {
        this.preferences= preferences;
        this.name = name;
        this.mac = mac;
        this.email = email;
    }

    public Profile(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(ArrayList<String> preferences) {
        this.preferences = preferences;
        preference1 = preferences.get(0);
        preference2 = preferences.get(1);
        preference3 = preferences.get(2);
    }

    public String getPreference1() {
        return preference1;
    }

    public void setPreference1(String preference1) {
        this.preference1 = preference1;
    }

    public String getPreference2() {
        return preference2;
    }

    public void setPreference2(String preference2) {
        this.preference2 = preference2;
    }

    public String getPreference3() {
        return preference3;
    }

    public void setPreference3(String preference3) {
        this.preference3 = preference3;
    }
}
