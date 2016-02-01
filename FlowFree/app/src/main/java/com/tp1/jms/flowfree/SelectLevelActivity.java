package com.tp1.jms.flowfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SelectLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button button1 = (Button) findViewById(R.id.L1_But);
        Button button2 = (Button) findViewById(R.id.L2_But);
        Button button3 = (Button) findViewById(R.id.L3_But);
        setSupportActionBar(toolbar);



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectLevelActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });
    }

}
