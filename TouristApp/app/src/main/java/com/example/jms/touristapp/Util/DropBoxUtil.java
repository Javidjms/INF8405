package com.example.jms.touristapp.Util;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;


/**
 * Dropbox util which deals with Dropbox API
 */
public class DropBoxUtil {

    // APP key and secret key ( May be hidden or changed in Production Mode)
    final static private String APP_KEY     = "l6co3ozxz40fyem";
    final static private String APP_SECRET  = "qj8i24aoq9afj31";

    private DropboxAPI<AndroidAuthSession> mDBApi; // Dropbox API


    public DropBoxUtil() {
        AppKeyPair appKeys = new AppKeyPair(APP_KEY,APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
    }

    /**
     * Get Dropbox API
     * @return dropbox api
     */
    public  DropboxAPI<AndroidAuthSession> getDBApi(){
        return mDBApi;
    }


}
