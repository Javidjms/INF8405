package com.tp1.jms.flowfree.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.tp1.jms.flowfree.Db.LevelAdapter;
import com.tp1.jms.flowfree.R;
import com.tp1.jms.flowfree.View.LevelGridAdapter;

import java.util.ArrayList;

/**
 * This class defines the Activity where the user will choose the level of the game with the size chosen in the previous activity
 */
public class SelectLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_level);
        //Set the custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView backbutton = (ImageView) findViewById(R.id.toolbar_back_button);
        backbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
        //Get the size parameter from the previous activity
        Intent intent= getIntent();
        Bundle extras= intent.getExtras();
        final int selectedSize = (int) extras.get("size");

        LevelAdapter leveladapter = new LevelAdapter(this);
        final ArrayList<Integer> levels = leveladapter.getLevelNumbers(selectedSize); // get all possible levels for the grid size chosen befors
        ArrayList<Integer> unlockedlevels =leveladapter.getUnlockedLevelNumbers(selectedSize); // get all unlocked levels for the grid size chosen befors
        ArrayList<Integer> succeedlevels =leveladapter.getSucceedLevelNumbers(selectedSize); // get all succeed levels for the grid size chosen befors
        //Check parameters "unlocked","succeed" and "highscore" for each levels with the desired size
        final ArrayList<Boolean> unlocked = new ArrayList<Boolean>();
        ArrayList<Boolean> succeed = new ArrayList<Boolean>();
        ArrayList<Boolean> perfectscore = new ArrayList<Boolean>();
        for(int l:levels){
            unlocked.add(unlockedlevels.contains(l));
            succeed.add(succeedlevels.contains(l));
            perfectscore.add(leveladapter.getLevelHighscore(selectedSize, l) == leveladapter.getNbLevelFlows(selectedSize, l));
        }
       //Get the custom levelgrid adapter and view
        LevelGridAdapter levelgridadapter = new LevelGridAdapter(SelectLevelActivity.this,levels,unlocked,succeed,perfectscore);
        GridView gridview =(GridView)findViewById(R.id.level_gridview);
        // Configure the custom levelgrid view
        gridview.setAdapter(levelgridadapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (unlocked.get(position)) {
                    Intent intent = new Intent(SelectLevelActivity.this, GameActivity.class);
                    intent.putExtra("size", selectedSize);
                    intent.putExtra("level", levels.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Toast.makeText(SelectLevelActivity.this, R.string.level_locked_text, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
