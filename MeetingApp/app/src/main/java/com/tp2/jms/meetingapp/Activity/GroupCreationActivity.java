package com.tp2.jms.meetingapp.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.tp2.jms.meetingapp.Db.ProfileAdapter;
import com.tp2.jms.meetingapp.Model.Group;
import com.tp2.jms.meetingapp.Model.Profile;
import com.tp2.jms.meetingapp.R;

public class GroupCreationActivity extends AppCompatActivity {

    private EditText groupenametext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        groupenametext = (EditText) findViewById(R.id.etxt_grp_nam);
        Button addbutton = (Button) findViewById(R.id.but_add_mem);


        addbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // validation input field
                String strname = groupenametext.getText().toString();

                if (strname.equals("")) {
                    Toast.makeText(GroupCreationActivity.this, "Groupe name is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    Firebase rootRef = new Firebase("https://fiery-fire-6445.firebaseio.com/groups");
                    ProfileAdapter pa = new ProfileAdapter(GroupCreationActivity.this);
                    Profile profile = pa.getProfile(1);
                    final Group group = new Group(strname,profile);
                    rootRef.child(group.getGroupname()).child("members").setValue(group.getGroup());
                    Toast.makeText(GroupCreationActivity.this, "Creation du Groupe avec Succes", Toast.LENGTH_SHORT).show();

                    rootRef.child(group.getGroupname()).child("invitations").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(GroupCreationActivity.this)
                                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                                .setContentTitle("Meeting App - New invititation")
                                                .setContentText("You have a new invitation of "+dataSnapshot.child("name").getValue().toString()+" for the group "+group.getGroupname());
                               Intent resultIntent = new Intent(GroupCreationActivity.this, MenuActivity.class);

                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(GroupCreationActivity.this);
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
                            Toast.makeText(GroupCreationActivity.this, "You have new invitations", Toast.LENGTH_LONG).show();

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


                    finish();
                }

            }


        });


    }

}
