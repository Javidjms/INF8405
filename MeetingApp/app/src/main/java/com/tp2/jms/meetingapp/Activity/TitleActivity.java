package com.tp2.jms.meetingapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.tp2.jms.meetingapp.Db.ProfileAdapter;
import com.tp2.jms.meetingapp.R;

public class TitleActivity extends AppCompatActivity {

    private boolean exit = false; // Flag for exiting the main app
    private ProfileAdapter profileadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profileadapter = new ProfileAdapter(this);
        Firebase.setAndroidContext(this);

        //Get buttons
        Button startbutton = (Button) findViewById(R.id.but_crt_usr);
        //Set eventlistener on buttons
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileadapter.isEmpty()) {
                    Toast.makeText(TitleActivity.this, "First Use : Create your own profile", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TitleActivity.this, ProfileCreationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(TitleActivity.this, "Choose your option", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TitleActivity.this, MenuActivity.class);
                    startActivity(intent);
                }

                //  overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });




    }

    @Override
    public void onBackPressed() {
        if(!exit){ // Displays a confirmation Toast message for exiting the main app
            Toast.makeText(this, "Tap again if you want to exit", Toast.LENGTH_SHORT).show();
            exit = true;
        }
        else{
            finish();
        }
    }

}
