package com.example.jms.touristapp.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jms.touristapp.Config;
import com.example.jms.touristapp.Model.POI;

/**
 * This class is our DbHelper that initilize DB and get some query on it.
 */
public class SqlLiteDbHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_FILENAME = Config.DATABASE_FILENAME;
    // Contacts table name
    private static final String DB_PATH_SUFFIX = Config.DB_PATH_SUFFIX;

    private static final String POIS_TABLE_NAME = Config.POIS_TABLE_NAME;

    private static final String ADMINS_TABLE_NAME = Config.ADMINS_TABLE_NAME;

    private Context context;

    // Columns names
    public static final String[] POI_TABLE_COLS = {"id", "name", "intro", "description",
            "image","link" , "latitude","longitude","address","phone","email","fav"
    };

    public SqlLiteDbHelper(Context context) {
        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Get all pois from the database
     * @return pois
     */
    public ArrayList<POI> getAllPOIfromDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<POI> pois = new ArrayList<POI>();
        Cursor cursor = db.rawQuery("SELECT * FROM pois", null);
        while (cursor.moveToNext()){
            POI poi = new POI(cursor.getInt(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4),
                    cursor.getString(5),cursor.getDouble(6),cursor.getDouble(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),
            cursor.getInt(11)==1);
            pois.add(poi);
        }
        cursor.close();
        db.close();
        return pois;
    }

    /**
     * Get all poi with the desired category from the database
     * @param category desired category for pois
     * @return pois
     */
    public ArrayList<POI> getPOIfromDatabasebyCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<POI> pois = new ArrayList<POI>();
        String[] args = {
                category
        };
        Cursor cursor = db.rawQuery("SELECT * FROM pois WHERE intro= ? ", args);
        while (cursor.moveToNext()){
            POI poi = new POI(cursor.getInt(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4),
                    cursor.getString(5),cursor.getDouble(6),cursor.getDouble(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getInt(11)==1);
            pois.add(poi);
        }
        cursor.close();
        db.close();
        return pois;
    }


    /**
     * Copy Asset Database into local device storage
     * @throws IOException
     */
    public void CopyDataBaseFromAsset() throws IOException{

        //Get database asset file
        InputStream myInput = context.getAssets().open(DATABASE_FILENAME);
        // Path to the just created empty db
        String outFileName = getDatabasePath();
        // if the path doesn't exist first, create it
        File f = new File(context.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
        if (!f.exists())
            f.mkdir();
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /**
     * Get the local database path
     * @return path
     */
    private String getDatabasePath() {
        return context.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_FILENAME;
    }

    /**
     * Open local database
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase openDataBase() throws SQLException{
        File dbFile = context.getDatabasePath(DATABASE_FILENAME);
        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }

        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Check if the user email match with the given password by comparing in the admin table
     * @param stremail user email
     * @param hashpassword user MD5 password
     * @return true if the password matches otherwise false
     */
    public boolean isGoodPassword(String stremail, String hashpassword) {
        boolean checkpassword = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {
                stremail,
                hashpassword
        };
        Cursor cursor = db.rawQuery("SELECT * FROM admins WHERE email = ? and password = ? ", args);
        checkpassword = cursor.moveToFirst();
        cursor.close();
        db.close();
        return checkpassword;
    }

    /**
     * Insert a new poi in the pois table
     * @param poi new poi
     * @return long id
     */
    public long insertPOI(POI poi) {
        long value = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = POI_TABLE_COLS;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[1], poi.getName());
        contentValues.put(cols[2], poi.getIntro());
        contentValues.put(cols[3], poi.getDescription());
        contentValues.put(cols[4], poi.getImage());
        contentValues.put(cols[5], poi.getLink());
        contentValues.put(cols[6], poi.getLatitude());
        contentValues.put(cols[7], poi.getLongitude());
        contentValues.put(cols[8], poi.getAddress());
        contentValues.put(cols[9], poi.getPhone());
        contentValues.put(cols[10], poi.getEmail());
        value = db.insert(POIS_TABLE_NAME, null, contentValues);
        db.close();
        return value;
    }

    /**
     * Update a poi in the pois table
     * @param poi poi to be updated
     * @return long id
     */
    public long updatePOI(POI poi) {
        long value = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = POI_TABLE_COLS;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[0], poi.getId());
        contentValues.put(cols[1], poi.getName());
        contentValues.put(cols[2], poi.getIntro());
        contentValues.put(cols[3], poi.getDescription());
        contentValues.put(cols[4], poi.getImage());
        contentValues.put(cols[5], poi.getLink());
        contentValues.put(cols[6], poi.getLatitude());
        contentValues.put(cols[7], poi.getLongitude());
        contentValues.put(cols[8], poi.getAddress());
        contentValues.put(cols[9], poi.getPhone());
        contentValues.put(cols[10], poi.getEmail());
        value = db.update(POIS_TABLE_NAME, contentValues, "id=" + poi.getId(), null);
        db.close();
        return value;

    }

    /**
     * Delete a poi in the pois table
     * @param poi poi to be deleted
     * @return long id
     */
    public long deletePOI(POI poi) {
        long value = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = POI_TABLE_COLS;
        value = db.delete(POIS_TABLE_NAME, "id=" + poi.getId(), null);
        db.close();
        return value;
    }

    /**
     *  Get all poi which name contains query from the database
     * @param query query to filter poi name
     * @return pois
     */
    public ArrayList<POI> getPOIfromDatabasebyQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<POI> pois = new ArrayList<POI>();
        String[] args = {
            "%"+query+"%"
        };
        Cursor cursor = db.rawQuery("SELECT * FROM pois where name like ?", args);
        while (cursor.moveToNext()){
            POI poi = new POI(cursor.getInt(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4),
                    cursor.getString(5),cursor.getDouble(6),cursor.getDouble(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getInt(11)==1);
            pois.add(poi);
        }
        cursor.close();
        db.close();
        return pois;
    }

    /**
     *  Get favorite status of a desired poi
     * @param id id of the desired poi
     * @return favorite status
     */
    public boolean getPOIFavoriteStatus(int id) {
        boolean favorite= false;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<POI> pois = new ArrayList<POI>();
        String[] args = {
                String.valueOf(id)
        };
        Cursor cursor = db.rawQuery("SELECT fav FROM pois where id = ?", args);
        if (cursor.moveToNext()){
            favorite = cursor.getInt(0)==1;
        }
        cursor.close();
        db.close();
        return favorite;
    }

    /**
     *  Set favorite status of a desired poi
     * @param id id of the desired poi
     * @param favorite new favorite status
     * @return
     */
    public long setPOIFavoriteStatus(int id,boolean favorite){
        long value = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = POI_TABLE_COLS;
        ContentValues contentValues = new ContentValues();
        contentValues.put(cols[11], favorite);
        value = db.update(POIS_TABLE_NAME, contentValues, "id=" + id, null);
        db.close();
        return value;

    }


    /**
     * Get all favorites status of all pois from database
     * @return list of favorite status
     */
    public ArrayList<POI> getAllFavoritesPOIfromDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<POI> pois = new ArrayList<POI>();
        Cursor cursor = db.rawQuery("SELECT * FROM pois where fav = 1", null);
        while (cursor.moveToNext()){
            POI poi = new POI(cursor.getInt(0),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getString(4),
                    cursor.getString(5),cursor.getDouble(6),cursor.getDouble(7),cursor.getString(8),cursor.getString(9),cursor.getString(10),cursor.getInt(11)==1);
            pois.add(poi);
        }
        cursor.close();
        db.close();
        return pois;
    }
}

