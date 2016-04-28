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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tp2.jms.meetingapp.Db.ProfileAdapter;
import com.tp2.jms.meetingapp.Model.Preferences;
import com.tp2.jms.meetingapp.Model.Profile;
import com.tp2.jms.meetingapp.R;

import java.util.ArrayList;

public class ProfileEditionActivity extends AppCompatActivity {

    private EditText nametext;
    private EditText emailtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // view
        nametext = (EditText) findViewById(R.id.txt_Nam);
        emailtext = (EditText) findViewById(R.id.txt_eml);

        //Get buttons
        Button donebutton = (Button) findViewById(R.id.but_don);
        final Spinner spinner1 = (Spinner)findViewById(R.id.spinner1);
        final Spinner spinner2 = (Spinner)findViewById(R.id.spinner2);
        final Spinner spinner3 = (Spinner)findViewById(R.id.spinner3);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProfileEditionActivity.this, R.layout.support_simple_spinner_dropdown_item, Preferences.prefs);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

        final ProfileAdapter profileAdapter = new ProfileAdapter(ProfileEditionActivity.this);
        final Profile myprofile = profileAdapter.getProfile(1);
        nametext.setText(myprofile.getName());
        emailtext.setText(myprofile.getEmail());
        for(int i=0;i<Preferences.prefs.length;i++){
            if(Preferences.prefs[i].equals(myprofile.getPreferences().get(0))){
                spinner1.setSelection(i);
            }
            if(Preferences.prefs[i].equals(myprofile.getPreferences().get(1))){
                spinner2.setSelection(i);
            }
            if(Preferences.prefs[i].equals(myprofile.getPreferences().get(2))){
                spinner3.setSelection(i);
            }
        }


        //Set eventlistener on buttons
        donebutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // validation input field
                String strname = nametext.getText().toString();
                String stremail = emailtext.getText().toString();
                if (strname.equals("")) {
                    Toast.makeText(ProfileEditionActivity.this, "Name is empty", Toast.LENGTH_SHORT).show();
                    //return; // exit if()
                } else if (stremail.equals("")) {
                    Toast.makeText(ProfileEditionActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                    //return; // exit if()
                } else {
                    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                    if (!stremail.matches(emailPattern)) {
                        Toast.makeText(ProfileEditionActivity.this, "Enter a valid email", Toast.LENGTH_SHORT).show();
                    } else {

                        if (spinner1.getSelectedItemPosition() == spinner2.getSelectedItemPosition() ||
                                spinner2.getSelectedItemPosition() == spinner3.getSelectedItemPosition() ||
                                spinner1.getSelectedItemPosition() == spinner3.getSelectedItemPosition()) {

                            Toast.makeText(ProfileEditionActivity.this, "Choose different preferences", Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayList<String> preferences = new ArrayList<String>(3);
                            preferences.add(spinner1.getSelectedItem().toString());
                            preferences.add(spinner2.getSelectedItem().toString());
                            preferences.add(spinner3.getSelectedItem().toString());
                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            WifiInfo wInfo = wifiManager.getConnectionInfo();
                            String macAddress = wInfo.getMacAddress();
                            final Profile profile = new Profile(strname, macAddress,stremail, preferences);
                            profileAdapter.updateProfile(profile);
                            Toast.makeText(ProfileEditionActivity.this, "Choose your options", Toast.LENGTH_SHORT).show();
                            final Firebase ref = new Firebase("https://fiery-fire-6445.firebaseio.com/groups/");

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final ArrayList<String> groupname = new ArrayList<String>();
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        for (DataSnapshot d : data.child("members").getChildren()) {
                                            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                                            WifiInfo wInfo = wifiManager.getConnectionInfo();
                                            String macAddress = wInfo.getMacAddress();
                                            if (macAddress.equals(d.child("mac").getValue().toString())) {
                                                d.getRef().setValue(profile);
                                                Toast.makeText(ProfileEditionActivity.this, profile.getName(), Toast.LENGTH_LONG).show();
                                                Toast.makeText(ProfileEditionActivity.this, d.toString(), Toast.LENGTH_LONG).show();
                                                break;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Toast.makeText(ProfileEditionActivity.this, "Failed to read", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                            finish();
                        }
                    }

                }
            }

        });





    }

}
