package com.example.jms.touristapp.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.example.jms.touristapp.R;

/**
 * This class is a broadcastReceiver which will handle each network interruption
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent )
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if ( activeNetInfo == null ) {
            Toast.makeText(context, R.string.connectivity_change_no_internet_message, Toast.LENGTH_LONG).show();

            // CAN DO OTHER THINGS HERE
        }

    }
}