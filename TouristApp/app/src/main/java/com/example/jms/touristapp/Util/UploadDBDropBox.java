package com.example.jms.touristapp.Util;

import android.content.Context;
import android.os.AsyncTask;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.example.jms.touristapp.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This class uploads the local database in dropbox in asynchrounous mode after authentification
 */
public class UploadDBDropBox extends AsyncTask<Void,String,String> {

    private Context context; // context kept in case of interaction with user if errors

    private DropboxAPI<AndroidAuthSession> mdbApi;  // DropBox API
    // path variables
    private final String PROJECT_FOLDER = Config.PROJECT_FOLDER ;
    private final String DATABASE_FILENAME = Config.DATABASE_FILENAME ;
    private final String DROPBOX_UPLOAD_URL = PROJECT_FOLDER+ DATABASE_FILENAME;
    private String DATABASE_ABSOLUTE_PATH;

    public UploadDBDropBox(Context context, DropboxAPI<AndroidAuthSession> mdbApi) {
        this.context = context;
        this.mdbApi = mdbApi;
        DATABASE_ABSOLUTE_PATH=Config.getDatabasePath(context);
    }

    @Override
    protected String doInBackground(Void... params) {

        File file = new File(DATABASE_ABSOLUTE_PATH);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            DropboxAPI.Entry response = mdbApi.putFileOverwrite(DROPBOX_UPLOAD_URL,inputStream,file.length(),null);
        } catch (DropboxException e) {
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
