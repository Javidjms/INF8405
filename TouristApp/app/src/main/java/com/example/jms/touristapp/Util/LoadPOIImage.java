package com.example.jms.touristapp.Util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This class permits to load poi images from internet link or from local storage in asynchrounous mode
 */
public class LoadPOIImage extends AsyncTask<Object,Void,Drawable> {

    private ImageView imageView;
    private String path;
    private Context context;



    public LoadPOIImage(Context context, ImageView imageview, String path) {
        this.imageView = imageview;
        this.path = path;
        this.context = context;
    }

    @Override
    protected Drawable doInBackground(Object... params) {
        Drawable drawable = null;
        boolean isfromURL = path.contains("http");;
        if(!isfromURL) {
            try {
                InputStream is = new FileInputStream(context.getApplicationInfo().dataDir +"/"+path);
                drawable = Drawable.createFromStream(is, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else{
            try {
                InputStream is = (InputStream) new URL(path).getContent();
                drawable = Drawable.createFromStream(is, null);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    @Override
    protected void onPostExecute(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }
}
