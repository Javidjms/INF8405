package com.example.jms.touristapp.Util;


import android.content.Context;
import com.example.jms.touristapp.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Arrays;

/**
 * This class permits to get the path of the category image marker stored in asset in order to load it
 */
public class LoadCategoryImage {

    private  String MARKER_PATH = "markers/";
    private  String IMAGE_EXTENSION = ".png";
    private String[] category;

    public LoadCategoryImage(Context context){
        category = context.getResources().getStringArray(R.array.category);
    }

    public BitmapDescriptor get(String intro) {
        int id = Arrays.binarySearch(category, intro)+1;
        String image=String.valueOf(id);
        String path = MARKER_PATH+image+IMAGE_EXTENSION;
        return BitmapDescriptorFactory.fromAsset(path);
    }

}
