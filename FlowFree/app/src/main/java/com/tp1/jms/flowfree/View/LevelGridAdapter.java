package com.tp1.jms.flowfree.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tp1.jms.flowfree.R;

import java.util.ArrayList;

/**
 * Created by JMS on 09/02/2016.
 * This class defines an adapter for the levelgrid which displays all levels on a custom grid with the status of the level
 */
public class LevelGridAdapter extends BaseAdapter {
    private ArrayList<Boolean> succeed;
    private ArrayList<Boolean> highscore;
    private ArrayList<Boolean> unlocked;
    private ArrayList<Integer> levelnumber;
    private Context context;


    public LevelGridAdapter(Context c, ArrayList<Integer> levelnumber, ArrayList<Boolean> unlocked, ArrayList<Boolean> succeed, ArrayList<Boolean> highscore) {
        this.context = c;
        this.levelnumber = levelnumber;
        this.unlocked = unlocked;
        this.succeed = succeed;
        this.highscore = highscore;

    }

    @Override
    public int getCount() {
        return levelnumber.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.simple_level_grid, null); //Get the custom grid
            TextView textview = (TextView) grid.findViewById(R.id.level_grid_text); //Get the text
            ImageView imageview = (ImageView)grid.findViewById(R.id.level_grid_image);//Get the image
            String str = String.format(context.getString(R.string.select_level_text), levelnumber.get(position));
            textview.setText(str);//Set the text for the level

            int imageid;
            //Set the icon of the level depends on the status of the level
            if(!unlocked.get(position)){
                imageid = R.drawable.locked_level;
            }
            else if(succeed.get(position)){
                if(highscore.get(position)){
                    imageid = R.drawable.perfect_level;
                }
                else{
                    imageid = R.drawable.success_level;
                }
            }
            else{
                imageid = R.drawable.play_image;
            }
            imageview.setImageResource(imageid);
        } else {
            grid = convertView;
        }

        return grid;
    }
}
