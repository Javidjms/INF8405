package com.example.jms.touristapp.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.jms.touristapp.Activity.POIDetailsActivity;
import com.example.jms.touristapp.Model.POI;
import com.example.jms.touristapp.R;
import com.example.jms.touristapp.Util.LoadPOIImage;
import java.util.ArrayList;

/**
 * This class is a custom grid view adapter for POIListActivity
 */
public class POIGridAdapter extends BaseAdapter {

    private Activity activity;
    private final ArrayList<POI> pois;

    public POIGridAdapter(Activity activity,ArrayList<POI> pois) {
        this.activity = activity;
        this.pois = pois;
    }

    @Override
    public int getCount() {
        return pois.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.simple_gridview_list, null); //Get the custom grid
        final POI poi = pois.get(position);
        TextView attractiontype = (TextView) grid.findViewById(R.id.attraction_type_text); //Get the text
        TextView poitext = (TextView) grid.findViewById(R.id.poi_text); //Get the text
        ImageView imageview = (ImageView)grid.findViewById(R.id.poi_image);//Get the image
        attractiontype.setText(poi.getIntro());//Set the text for the attraction type
        poitext.setText(poi.getName());//Set the text for the poi name

        String path = poi.getImage();
        // Load poi image
        new LoadPOIImage(activity,imageview,path).execute();
        // Set event listner on poi image
        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,POIDetailsActivity.class);
                intent.putExtra("poi", poi);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        return grid;
    }

}
