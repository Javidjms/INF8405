package com.example.jms.touristapp.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.example.jms.touristapp.Interface.RequiredConnectionActivityImpl;
import com.example.jms.touristapp.R;

/**
 *  This class checks the connectivity of the device in an activity
 */
public class CheckConnectivityStatus {

    // Code variables
    public final static int CODE_INTERNET_REQUIRED = 1;

    public final static int CODE_LOCATION_REQUIRED = 2;

    public final static int CODE_BOTH_REQUIRED = 3;

    /**
     * Execute the checking connectivity by launching the desired checking method depends on the code value
     * @param r activity which required connectivity
     * @param code desired code
     */
    public static void run(RequiredConnectionActivityImpl r,int code) {
        switch(code){
            case CODE_INTERNET_REQUIRED:
                checkStatusInternet(r);
                break;
            case CODE_LOCATION_REQUIRED:
                checkStatusLocation(r);
                break;
            case CODE_BOTH_REQUIRED:
                checkStatusBoth(r);
                break;
            default:
                break;
        }

    }

    /**
     * Check the status of the internet connection
     * @param r activity which required connectivity
     */
    public static void checkStatusInternet(RequiredConnectionActivityImpl r){
        Activity activity = (Activity) r; // Get activity

        // Check internet connection
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting()){
            r.onInternetConnected(); // On Intenet method
        }
        else{
            buildAlertMessageNoInternet(activity); // Show alert message
        }


    }

    /**
     * Check the status of the gps location
     * @param r activity which required connectivity
     */
    public static void checkStatusLocation(RequiredConnectionActivityImpl r){
        Activity activity = (Activity) r; // Get activity

        //Check GPS Location
        final LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGPS(activity); // Show Alert Message
        }
        else{
            r.onLocationConnected(); // Do location method
        }


    }

    /**
     * Check if the activity required Internet AND GPS Location Connectivity
     * @param r activity which required connectivity
     */
    public static void checkStatusBoth(RequiredConnectionActivityImpl r){
        checkStatusLocation(r); // Check GPS Location
        checkStatusInternet(r); // Check Internet

    }

    /**
     * Show alert message when there is no gps location
     * @param activity
     */
    private static void buildAlertMessageNoGPS(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.check_connectivity_gps_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent =activity.getIntent();
                        activity.finish();
                        activity.startActivity(intent);
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show alert message when there is no internet
     * @param activity
     */
    private static void buildAlertMessageNoInternet(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.check_connectivity_internet_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent =activity.getIntent();
                        activity.finish();
                        activity.startActivity(intent);
                        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }
}
