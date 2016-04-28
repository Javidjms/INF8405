package com.tp2.jms.meetingapp.Activity;

import android.content.Intent;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tp2.jms.meetingapp.Db.ProfileAdapter;
import com.tp2.jms.meetingapp.Model.Preferences;
import com.tp2.jms.meetingapp.Model.Profile;
import com.tp2.jms.meetingapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InvitationGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        final String groupname = (String) extras.get("groupname");
        final ArrayList<String> username = new ArrayList<String>();
        final ListView listView = (ListView) findViewById(R.id.listView);
        final Firebase ref = new Firebase("https://fiery-fire-6445.firebaseio.com/groups/"+groupname);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.child("invitations").getChildren()) {
                    username.add(data.child("name").getValue().toString());
                }
                final ArrayAdapter adapter = new ArrayAdapter<String>(InvitationGroupActivity.this, R.layout.simple_size_list, username);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String uname = username.get(position);
                        username.remove(position);
                        adapter.notifyDataSetChanged();
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Profile profile = null;
                                System.out.println("DATASTRING :" + dataSnapshot.toString());
                                for (DataSnapshot data : dataSnapshot.child("invitations").getChildren()) {
                                    System.out.println("DATACHILD :" + data.toString());
                                    System.out.println(uname);
                                    System.out.println(data.child("name").toString());
                                    if (data.child("name").getValue().toString().equals(uname)) {
                                        System.out.println("ok");
                                        ArrayList<String> pref = new ArrayList<String>();
                                        pref.add(data.child("preferences").child("0").getValue().toString());
                                        pref.add(data.child("preferences").child("1").getValue().toString());
                                        pref.add(data.child("preferences").child("2").getValue().toString());
                                        profile = new Profile(uname,
                                                data.child("mac").getValue().toString(),
                                                data.child("email").getValue().toString(),
                                                pref);
                                        profile.setId(1);
                                        data.getRef().setValue(null);
                                        break;
                                    }

                                }
                                if (profile != null) {
                                    System.out.println("ok2");
                                    final Profile p = profile;
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            long number = dataSnapshot.child("members").getChildrenCount()+1;
                                            ref.child("members").child("" + number).setValue(p);
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });


                                }


                            }


                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    }

                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }

}

