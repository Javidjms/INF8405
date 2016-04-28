package com.example.jms.touristapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.jms.touristapp.Config;
import com.example.jms.touristapp.Interface.RequiredConnectionActivityImpl;
import com.example.jms.touristapp.R;
import com.example.jms.touristapp.Util.BatteryLevelMonitor;
import com.example.jms.touristapp.Util.DownloadDBDropBox;
import com.example.jms.touristapp.Util.CheckConnectivityStatus;
import com.example.jms.touristapp.View.AboutDialog;

/**
 * This is the main title activity
 */
public class MainActivity extends AppCompatActivity implements RequiredConnectionActivityImpl {

    private ProgressDialog mProgressDialog; // progress bar
    private boolean exit = false; // Flag for exiting the main app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Load buttons
        Button startbutton = (Button) findViewById(R.id.title_start_button);
        Button editdbbutton = (Button) findViewById(R.id.edit_db_button);
        Button aboutbutton = (Button) findViewById(R.id.about_button);
        //Add event listener to buttons
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,POIListActivity.class);
                intent.putExtra("category","ALL");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        editdbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        aboutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AboutDialog(MainActivity.this);
            }
        });

        // Initialize the progress bar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.progress_bar_downloading_message));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        // Check if there is an internet connection
        int codeConnection= CheckConnectivityStatus.CODE_INTERNET_REQUIRED;
        CheckConnectivityStatus.run(this, codeConnection);
    }

    @Override
    protected void onResume() {
        //BatteryLevelMonitor initialization and level save point
        IntentFilter intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(BatteryLevelMonitor.getInstance(), intentfilter);
        BatteryLevelMonitor bm = BatteryLevelMonitor.getInstance();
        bm.initializeBatteryLevel();
        bm.saveCurrentBatteryLevel();
        exit = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        //BatteryLevelMonitor unregistration when activity or app exit
        this.unregisterReceiver(BatteryLevelMonitor.getInstance());
        super.onPause();
    }

    @Override
    public void onInternetConnected() {
        // Show dialog box if the user want to download the latest db
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Start of downloading the db with images + Launch of the progress bar
                        final DownloadDBDropBox downloadTask = new DownloadDBDropBox(MainActivity.this,mProgressDialog);
                        downloadTask.execute(
                                Config.DATABASE_DROPBOX_URL,
                                Config.DATABASE_POI_IMAGES_URL
                        );
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.title_ask_database_download).setPositiveButton(R.string.dialog_yes, dialogClickListener)
                .setNegativeButton(R.string.dialog_no, dialogClickListener).show();
    }

    @Override
    public void onLocationConnected() {

    }

    @Override
    public void onBackPressed() {
        // Displays a confirmation Toast message for exiting the main app
        if(!exit){
            Toast.makeText(this, R.string.title_exit_confirmation, Toast.LENGTH_SHORT).show();
            exit = true;
        }
        else{
            finish();
        }
    }
}
