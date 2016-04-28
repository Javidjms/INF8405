package com.example.jms.touristapp;


import android.content.Context;

/**
 * Config class with all path variables
 */
public class Config {

    public static String DATABASE_DROPBOX_URL = "https://dl.dropboxusercontent.com/s/k2pe2ni1ly139ph/touristAppDB.sqlite";

    public static String DATABASE_POI_IMAGES_URL = "https://www.dropbox.com/sh/xepa37agqave88w/AABXBaAE-skrPD7ck0LkwwMAa?dl=1";

    public static String PROJECT_FOLDER = "TouristApp/";

    public static String DATABASE_FILENAME = "touristAppDB.sqlite";

    public static String DATABASE_ABSOLUTE_PATH ;

    public static final String DB_PATH_SUFFIX = "/databases/";

    public static final String POIS_TABLE_NAME = "pois";

    public static final String ADMINS_TABLE_NAME = "admins";

    public static final String POI_IMAGES_FOLDER_NAME = "/poi/";

    public static final String POI_IMAGES_ZIP_FILENAME = "poi.zip";

    public static String getDatabasePath(Context context) {
        DATABASE_ABSOLUTE_PATH = context.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_FILENAME;
        return DATABASE_ABSOLUTE_PATH;
    }

    public static String getDatabaseFolderPath(Context context) {
        return  context.getApplicationInfo().dataDir + DB_PATH_SUFFIX ;
    }

    public static final String GOOGLE_MAP_STATIC_API_LINK = "https://maps.googleapis.com/maps/api/staticmap?center=%s,%s&zoom=14&size=400x400&key=AIzaSyBcwaHPmZIEqEMHL6O2wQDLQfJoetYXAdY";

    public static final String GOOGLE_MAP_DIRECTION_API_LINK = "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s" ;

}
