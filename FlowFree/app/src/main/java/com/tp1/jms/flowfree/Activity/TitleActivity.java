package com.tp1.jms.flowfree.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tp1.jms.flowfree.Db.LevelAdapter;
import com.tp1.jms.flowfree.R;
import com.tp1.jms.flowfree.View.TutorialBox;

/**
 * This class defines the first Activity with the title and the menu
 */
public class TitleActivity extends AppCompatActivity {

    private boolean exit = false; // Flag for exiting the main app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        //Init the level database (only once for the first time)
        LevelAdapter leveladapter = new LevelAdapter(this);
        leveladapter.initTableLevels();
        //Get buttons
        Button startbutton = (Button) findViewById(R.id.title_start_button);
        Button tutorialbutton = (Button) findViewById(R.id.title_tutorial_button);
        Button aboutbutton = (Button) findViewById(R.id.title_about_button);
        //Set eventlistener on buttons
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, SelectSizeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        tutorialbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorialBox tutorialbox = new TutorialBox(TitleActivity.this);
                tutorialbox.show();

            }
        });
        aboutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TitleActivity.this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        Toast.makeText(this, R.string.welcome_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if(!exit){ // Displays a confirmation Toast message for exiting the main app
            Toast.makeText(this, R.string.exit_confirmation_message, Toast.LENGTH_SHORT).show();
            exit = true;
        }
        else{
            finish();
        }
    }
}
