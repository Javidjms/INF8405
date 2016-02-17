package com.tp1.jms.flowfree.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.tp1.jms.flowfree.Db.LevelAdapter;
import com.tp1.jms.flowfree.R;

import java.util.ArrayList;

/**
 * This class defines the Activity where the user will choose the size of the grid
 */
public class SelectSizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_size);
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

        LevelAdapter leveladapter = new LevelAdapter(this);
        final ArrayList<Integer> sizes = leveladapter.getLevelSizes(); // Get all possible sizes of grid
        ListView listeview = (ListView) findViewById(R.id.size_listview); // Get the listeview
        ArrayList<String> sizestext = new ArrayList<String>(); //Set text for each element of the listview
        for(int i:sizes){
            String str = String.format(getString(R.string.select_size_text), i);
            sizestext.add(str);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_size_list, sizestext);
        //Configure the listview
        listeview.setAdapter(adapter);
        listeview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectSizeActivity.this, SelectLevelActivity.class);
                intent.putExtra("size", sizes.get(position));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
