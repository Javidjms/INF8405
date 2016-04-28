package com.tp2.jms.meetingapp.Service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context,Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                Intent intent2 = new Intent(context, UpdateLocationService.class);
                context.stopService(intent2);
                Toast.makeText(context,"No internet connection. Please connect to internet",Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent2 = new Intent(context, UpdateLocationService.class);
                context.startService(intent2);
            }


    }

}