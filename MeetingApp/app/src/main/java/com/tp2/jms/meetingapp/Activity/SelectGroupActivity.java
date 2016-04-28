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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tp2.jms.meetingapp.Db.ProfileAdapter;
import com.tp2.jms.meetingapp.Model.Profile;
import com.tp2.jms.meetingapp.R;

import java.util.ArrayList;

public class SelectGroupActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView listView = (ListView) findViewById(R.id.listView);
        final Firebase ref = new Firebase("https://fiery-fire-6445.firebaseio.com/groups");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> groupname = new ArrayList<String>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    boolean isMember = false;
                    for (DataSnapshot d : data.child("members").getChildren()) {
                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wInfo = wifiManager.getConnectionInfo();
                        String macAddress = wInfo.getMacAddress();
                        if (macAddress.equals(d.child("mac").getValue().toString())) {
                            isMember = true;
                            break;
                        }
                    }
                    if (isMember) {
                        groupname.add(data.getKey());
                    }
                }

                adapter = new ArrayAdapter<String>(SelectGroupActivity.this, R.layout.simple_size_list, groupname);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(SelectGroupActivity.this, GroupMenuActivity.class);
                        intent.putExtra("groupname",groupname.get(position));
                        finish();
                        startActivity(intent);
                    }
                });


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(SelectGroupActivity.this, "Failed to read", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
