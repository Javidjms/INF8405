package com.tp2.jms.meetingapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tp2.jms.meetingapp.R;

public class GroupMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle extras= intent.getExtras();
        final String groupname = (String) extras.get("groupname");

        final Button invitationbutton = (Button) findViewById(R.id.invitations_button);
        Button memberlistbutton = (Button) findViewById(R.id.member_list_button);
        Button checkavailbutton = (Button) findViewById(R.id.check_avail_button);
        Button findplacebutton = (Button) findViewById(R.id.find_place_button);


        invitationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMenuActivity.this,InvitationGroupActivity.class);
                intent.putExtra("groupname",groupname);
                startActivity(intent);

            }
        });

        memberlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMenuActivity.this,GroupUserListActivity.class);
                intent.putExtra("groupname",groupname);
                startActivity(intent);

            }
        });

        TextView grouptitle = (TextView) findViewById(R.id.group_title);
        grouptitle.setText(groupname);

        final Firebase ref = new Firebase("https://fiery-fire-6445.firebaseio.com/groups/"+groupname+"/members");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isAdmin = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wInfo = wifiManager.getConnectionInfo();
                    String macAddress = wInfo.getMacAddress();
                    if (macAddress.equals(data.child("mac").getValue().toString())) {
                        isAdmin = true;
                    }
                    break;
                }
                invitationbutton.setEnabled(isAdmin);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

}
