package com.tp2.jms.meetingapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tp2.jms.meetingapp.R;

import java.util.ArrayList;

public class GroupUserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        final String groupname = (String) extras.get("groupname");
        final ArrayList<String> username = new ArrayList<String>();
        final ListView listView = (ListView) findViewById(R.id.listView);
        final Firebase ref = new Firebase("https://fiery-fire-6445.firebaseio.com/groups/"+groupname+"/members");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    username.add(data.child("name").getValue().toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupUserListActivity.this, R.layout.simple_size_list, username);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
