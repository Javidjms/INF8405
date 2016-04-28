package com.tp2.jms.meetingapp.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tp2.jms.meetingapp.R;
import com.tp2.jms.meetingapp.Service.UpdateLocationService;

import java.util.ArrayList;


public class MenuActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Get buttons
        Button groupbutton = (Button) findViewById(R.id.but_grp);
        Button joinbutton = (Button) findViewById(R.id.join_button);
        Button select_group_button = (Button) findViewById(R.id.select_group_button);
        Button editbutton = (Button) findViewById(R.id.edit_button);

        //Set eventlistener on buttons
        groupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GroupCreationActivity.class);
                startActivity(intent);
                //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        select_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SelectGroupActivity.class);
                startActivity(intent);
            }
        });

        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, JoinGroupActivity.class);
                startActivity(intent);
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProfileEditionActivity.class);
                startActivity(intent);
            }
        });


        final Firebase ref = new Firebase("https://fiery-fire-6445.firebaseio.com/groups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot d : data.child("members").getChildren()) {
                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wInfo = wifiManager.getConnectionInfo();
                        String macAddress = wInfo.getMacAddress();
                        if (macAddress.equals(d.child("mac").getValue().toString())) {
                            data.child("invitations").getRef().addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(MenuActivity.this)
                                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                            .setContentTitle("Meeting App - New invititation")
                                            .setWhen(System.currentTimeMillis())
                                            .setContentText("You have a new invitation of " + dataSnapshot.child("name").getValue().toString() + " for the group " + data.getKey().toString());
                                    Intent resultIntent = new Intent(MenuActivity.this, TitleActivity.class);

                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MenuActivity.this);
                                    stackBuilder.addParentStack(MenuActivity.class);
                                    stackBuilder.addNextIntent(resultIntent);
                                    PendingIntent resultPendingIntent =
                                            stackBuilder.getPendingIntent(
                                                    0,
                                                    PendingIntent.FLAG_UPDATE_CURRENT
                                            );
                                    mBuilder.setContentIntent(resultPendingIntent);
                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.notify(0, mBuilder.build());
                                    Toast.makeText(MenuActivity.this, "You have new invitations", Toast.LENGTH_LONG).show();


                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });

                        }
                        break;
                    }

                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(intent);
    }
}
