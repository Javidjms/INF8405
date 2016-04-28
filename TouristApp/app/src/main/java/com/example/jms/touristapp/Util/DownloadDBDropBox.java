package com.example.jms.touristapp.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.jms.touristapp.Config;
import com.example.jms.touristapp.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * This class permits to download the database from the dropbox link in asynchrounous mode
 */
public class DownloadDBDropBox extends AsyncTask<String, Integer, String> {

    // String path varaibles
    private String DATABASE_FOLDER_PATH;
    private String DATABASE_PATH;
    private String POI_IMAGES_FOLDER_PATH;
    private String POI_IMAGES_ZIP_PATH;

    private ProgressDialog mProgressDialog; // Progress bar
    private Context context; // context
    private PowerManager.WakeLock mWakeLock; // WakeLock
    private String message; // Progress bar message

    public DownloadDBDropBox(Context context,ProgressDialog progressDialog) {
        this.context = context;
        mProgressDialog = progressDialog;
        message = context.getString(R.string.progress_bar_downloading_message);
        // Set the cancel/abort button on the progress bar
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.dialog_abort), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancel(true);
            }
        });

        DATABASE_FOLDER_PATH = Config.getDatabaseFolderPath(context);
        DATABASE_PATH = Config.getDatabasePath(context);
        POI_IMAGES_FOLDER_PATH = context.getApplicationInfo().dataDir + Config.POI_IMAGES_FOLDER_NAME;
        POI_IMAGES_ZIP_PATH =context.getApplicationInfo().dataDir + Config.POI_IMAGES_FOLDER_NAME+ Config.POI_IMAGES_ZIP_FILENAME;
    }


    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            // Firstly - Download the Sqlite Database
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();


            // download the file
            input = connection.getInputStream();
            // If folder not exists so creates them
            File f = new File(DATABASE_FOLDER_PATH);
            if (!f.exists())
                f.mkdir();

            output = new FileOutputStream(DATABASE_PATH);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        try {
            // Secondly - Download the POI Images of the Database
            message = context.getString(R.string.progress_bar_downloading_message_img);
            URL url = new URL(sUrl[1]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            File folder = new File(POI_IMAGES_FOLDER_PATH);
            File f = new File(POI_IMAGES_ZIP_PATH);
            // Check if the folder exists otherwise we create it
            // By the way we test if the dropbox image are recently modified or not in order to avoid
            // to download wastly old images but doesnt work with dropbox because no Header value for
            // Last-Modified Tag
            if (!folder.exists())
                folder.mkdir();
            else if(f.exists()){
                long c1 = connection.getLastModified();
                long c2 = f.lastModified();
                if(c1!=0 && c1<= c2 || fileLength == f.length()){
                    return null;
                }
            }

            if(fileLength <= 0){
                fileLength = (int) f.length();
            }


            // download the file
            input = connection.getInputStream();

            output = new FileOutputStream(POI_IMAGES_ZIP_PATH);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        File file = new File(POI_IMAGES_ZIP_PATH);
        FileInputStream fileInputStream = null;
        try {
            // Thirdly - Unzip the poi images archive
            message = context.getString(R.string.progress_bar_unzipping_message);
            fileInputStream = new FileInputStream(file);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                //create dir if required while unzipping
                if (zipEntry.isDirectory()) {
                    File f = new File(POI_IMAGES_FOLDER_PATH+zipEntry.getName());
                    f.mkdir();
                } else {
                    message = String.format(context.getString(R.string.progress_bar_single_unzip_message), zipEntry.getName());
                    publishProgress(100);
                    FileOutputStream fileOutputStream = new FileOutputStream(POI_IMAGES_FOLDER_PATH + zipEntry.getName()); // LINK
                    for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                        fileOutputStream.write(c);
                    }
                    zipInputStream.closeEntry();
                    fileOutputStream.close();
                }

            }
            zipInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(Math.min(progress[0], 100));
        mProgressDialog.setMessage(message);

    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null)
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        // BatteryLevel notification
        BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
        Toast.makeText(context, String.format(context.getString(R.string.battery_download_usage), String.valueOf(b.getLastBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
        Toast.makeText(context, String.format(context.getString(R.string.battery_total_usage), String.valueOf(b.getTotalBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
    }
}
