package com.example.jms.touristapp.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.example.jms.touristapp.Database.SqlLiteDbHelper;
import com.example.jms.touristapp.Model.POI;
import com.example.jms.touristapp.R;
import java.util.ArrayList;

/**
 * This class is the menu for an admin user after authentification
 */
public class AdminMenuActivity extends AppCompatActivity {
    //Commands variable
    private final String ADD_COMMAND = "add";
    private final String DELETE_COMMAND = "delete";
    private final String EDIT_COMMAND = "edit";

    private POI poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        //Get buttons
        Button createbutton = (Button) findViewById(R.id.create_poi_button);
        Button editbutton = (Button) findViewById(R.id.edit_poi_button);
        Button deletebutton = (Button) findViewById(R.id.delete_poi_button);

        poi = null;

        //Set eventlistener on buttons
        createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenuActivity.this, EditPOIActivity.class);
                intent.putExtra("command",ADD_COMMAND);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPOIDialog(EDIT_COMMAND);
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPOIDialog(DELETE_COMMAND);
            }
        });
    }

    /**
     *  Show a dialog box with all POI on the database and the admin have to choose one
     * @param command Command to be done
     */
    public void selectPOIDialog(final String command){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.admin_menu_selection_poi);
        b.setCancelable(false);
        // Get all POIs
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(this);
        final ArrayList<POI> poilist =dbHelper.getAllPOIfromDatabase();
        String[] names = new String[poilist.size()];
        int i =0;
        for(POI poi:poilist){
            names[i]=poi.getName();
            i++;
        }
        //Init listener for each POI lines
        b.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                poi = poilist.get(which);
                Intent intent = new Intent(AdminMenuActivity.this, EditPOIActivity.class);
                intent.putExtra("command", command);
                intent.putExtra("poi", poi);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        b.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
