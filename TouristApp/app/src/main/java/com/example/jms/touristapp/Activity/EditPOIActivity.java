package com.example.jms.touristapp.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.jms.touristapp.Database.SqlLiteDbHelper;
import com.example.jms.touristapp.Interface.RequiredConnectionActivityImpl;
import com.example.jms.touristapp.Model.POI;
import com.example.jms.touristapp.R;
import com.example.jms.touristapp.Util.BatteryLevelMonitor;
import com.example.jms.touristapp.Util.CheckConnectivityStatus;
import com.example.jms.touristapp.Util.DropBoxUtil;
import com.example.jms.touristapp.Util.UploadDBDropBox;

import java.util.Arrays;

/**
 * This activity allows admin to edit a poi
 */
public class EditPOIActivity extends AppCompatActivity implements RequiredConnectionActivityImpl {

    //Commands variable
    private final String ADD_COMMAND = "add";
    private final String DELETE_COMMAND = "delete";
    private final String EDIT_COMMAND = "edit";

    private DropboxAPI<AndroidAuthSession> mdbApi; // Dropbox API for uploading the updated db
    // inputs variable
    private EditText nametext;
    private EditText emailtext;
    private EditText phonetext;
    private EditText descriptiontext;
    private EditText longitudetext;
    private EditText latitudetext;
    private EditText addresstext;
    private EditText websitetext;
    private TextView titletext;
    private EditText imagelinktext;
    private Button donebutton;
    private Spinner spinner;
    private POI poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_poi);

        //BatteryLevelMonitor initialization and level save point
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(BatteryLevelMonitor.getInstance(), intentFilter);
        BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
        b.saveCurrentBatteryLevel();

        //  Get the view text
        nametext = (EditText) findViewById(R.id.name_text);
        emailtext = (EditText) findViewById(R.id.email_text);
        phonetext = (EditText) findViewById(R.id.phone_text);
        descriptiontext = (EditText) findViewById(R.id.description_text);
        longitudetext = (EditText) findViewById(R.id.longitude_text);
        latitudetext = (EditText) findViewById(R.id.latitude_text);
        addresstext = (EditText) findViewById(R.id.address_text);
        websitetext = (EditText) findViewById(R.id.website_text);
        imagelinktext = (EditText) findViewById(R.id.photo_text);
        titletext = (TextView) findViewById(R.id.title_command_text);
        //Get buttons
        donebutton = (Button) findViewById(R.id.done_button);
        Button cancelbutton = (Button) findViewById(R.id.cancel_button);
        spinner = (Spinner)findViewById(R.id.category_spinner);
        String[] category = getResources().getStringArray(R.array.category);
        //Set the adpater for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditPOIActivity.this, R.layout.support_simple_spinner_dropdown_item, category);
        spinner.setAdapter(adapter);
        //Set the cancel button
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Init done button
        initDoneButton();
        // Check internet connectivity
        int codeConnection= CheckConnectivityStatus.CODE_INTERNET_REQUIRED;
        CheckConnectivityStatus.run(this, codeConnection);

    }

    /**
     *  Check input validation before updating the poi
     * @return boolean of the check validataion
     */
    public  boolean checkInputValidation() {
        boolean isValid = false;
        // Get all string from important input
        String strname = nametext.getText().toString();
        String strdescription = descriptiontext.getText().toString();
        String strimglink = imagelinktext.getText().toString();
        String strwebsite = websitetext.getText().toString();
        double latitude = Double.parseDouble(latitudetext.getText().toString());
        double longitude = Double.parseDouble(longitudetext.getText().toString());
        String stremail = emailtext.getText().toString();

        if (strname.equals("")) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_empty_name, Toast.LENGTH_SHORT).show();
        } else if (strdescription.equals("")) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_empty_description, Toast.LENGTH_SHORT).show();
        } else if (strimglink.equals("")) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_empty_image_link, Toast.LENGTH_SHORT).show();
        }else if (!strimglink.contains("http") && !strimglink.contains("poi")) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_unvalid_image_link, Toast.LENGTH_SHORT).show();
        } else if (!strwebsite.equals("") &&  !strwebsite.contains("http")) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_unvalide_website_link, Toast.LENGTH_SHORT).show();
        }else if (latitude == 0) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_empty_latitude, Toast.LENGTH_SHORT).show();
        }else if (longitude == 0) {
            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_empty_longitude, Toast.LENGTH_SHORT).show();
        }  else {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (!stremail.equals("") && !stremail.matches(emailPattern)) {
                Toast.makeText(EditPOIActivity.this, R.string.edit_poi_unvalid_mail, Toast.LENGTH_SHORT).show();
            }
            else{
                isValid = true;
            }
        }
        return isValid;
    }

    /**
     * Init Done button  depends on command value
     */
    public void initDoneButton() {
        Intent intent = getIntent();
        Bundle extras =intent.getExtras();
        String command = extras.getString("command");
        switch(command){
            case ADD_COMMAND: //
                titletext.setText(R.string.edit_poi_create_command);
                donebutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // validation input field
                        if(checkInputValidation()){
                            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_safe_insertion, Toast.LENGTH_SHORT).show();
                            insertnewPOI(); // Insert in the local database
                            uploadDB(); // Upload the updated database in dropbox
                        }
                    }

                });
                break;
            case EDIT_COMMAND:
                titletext.setText(R.string.edit_poi_edit_command);
                poi = (POI) extras.get("poi");
                fillPOIInput(); // Fill all input from poi informations
                donebutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // validation input field
                        if (checkInputValidation()) {
                            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_safe_update, Toast.LENGTH_SHORT).show();
                            updatePOI(); // Update in the local database
                            uploadDB();// Upload the updated database in dropbox
                        }
                    }

                });
                break;

            case DELETE_COMMAND:
                titletext.setText(R.string.edit_poi_delete_command);
                poi = (POI) extras.get("poi");
                fillPOIInput();// Fill all input from poi informations
                lockPOIInput();// Lock all input (only Reading no Writing)
                donebutton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // validation input field
                        if (checkInputValidation()) {
                            Toast.makeText(EditPOIActivity.this, R.string.edit_poi_safe_delete, Toast.LENGTH_SHORT).show();
                            deletePOI();// Delete in the local database
                            uploadDB();// Upload the updated database in dropbox
                        }
                    }

                });
                break;

            default:
                break;
        }

    }

    /**
     * Upload the database in dropbox
     */
    public void uploadDB() {
        DropBoxUtil dbutil = new DropBoxUtil(); // Dropbox Util (API)
        mdbApi = dbutil.getDBApi();
        mdbApi.getSession().startOAuth2Authentication(EditPOIActivity.this); // Start authentification

    }

    /**
     * Insert a new poi in the database
     */
    public void insertnewPOI() {

        POI poi =getCurrentPOI();
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(EditPOIActivity.this);
        dbHelper.insertPOI(poi);

    }

    /**
     * Delete a poi in the database
     */
    public void deletePOI() {
        POI poi =getCurrentPOI();
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(EditPOIActivity.this);
        dbHelper.deletePOI(poi);
    }

    /**
     * Update a poi in the database
     */
    public void updatePOI() {
        POI poi =getCurrentPOI();
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(EditPOIActivity.this);
        dbHelper.updatePOI(poi);
    }

    /**
     * Fill all input with the selected poi existing informations
     */
    public void fillPOIInput() {
        nametext.setText(poi.getName());
        descriptiontext.setText(poi.getDescription());
        imagelinktext.setText(poi.getImage());
        websitetext.setText(poi.getLink());
        latitudetext.setText(Double.toString(poi.getLatitude()));
        longitudetext.setText(Double.toString(poi.getLongitude()));
        addresstext.setText(poi.getAddress());
        phonetext.setText(poi.getPhone());
        emailtext.setText(poi.getEmail());
        String[] category = getResources().getStringArray(R.array.category);
        int itemId = Arrays.binarySearch(category, poi.getIntro());
        spinner.setSelection(itemId);
    }

    /**
     * Lock all input (Only Reading)
     */
    public void lockPOIInput() {
        nametext.setKeyListener(null);
        descriptiontext.setKeyListener(null);
        imagelinktext.setKeyListener(null);
        websitetext.setKeyListener(null);
        latitudetext.setKeyListener(null);
        longitudetext.setKeyListener(null);
        addresstext.setKeyListener(null);
        phonetext.setKeyListener(null);
        emailtext.setKeyListener(null);
        spinner.setEnabled(false);
    }


    /**
     * Get the current poi created by taken all input informations
     * @return current poi
     */
    private POI getCurrentPOI() {
        String strname = nametext.getText().toString();
        String strdescription = descriptiontext.getText().toString();
        String strimglink = imagelinktext.getText().toString();
        String strwebsite = websitetext.getText().toString();
        double latitude = Double.parseDouble(latitudetext.getText().toString());
        double longitude = Double.parseDouble(longitudetext.getText().toString());
        String straddress = addresstext.getText().toString();
        String strphone = phonetext.getText().toString();
        String stremail = emailtext.getText().toString();
        String strcat = spinner.getSelectedItem().toString();
        int id = (this.poi!= null? this.poi.getId():-1 );
        POI poi = new POI(id,strname,strcat,strdescription,strimglink,strwebsite,
                latitude,longitude,straddress,strphone,stremail);

        return poi;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Dropbox Authenfication Success
        if(mdbApi!=null && mdbApi.getSession().authenticationSuccessful()){
            try{
                mdbApi.getSession().finishAuthentication();
                String accessToken = mdbApi.getSession().getOAuth2AccessToken();
                // Start to upload the database
                UploadDBDropBox up = new UploadDBDropBox(this,mdbApi);
                up.execute();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                // BatteryLevel notification
                BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
                Toast.makeText(this, String.format(getString(R.string.battery_level_upload_usage), String.valueOf(b.getLastBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
                Toast.makeText(this, String.format(getString(R.string.battery_total_usage), String.valueOf(b.getTotalBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
            }
            catch (IllegalStateException e){
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        //BatteryLevelMonitor unregistration when activity or app exit
        this.unregisterReceiver(BatteryLevelMonitor.getInstance());
        super.onDestroy();
    }


    @Override
    public void onInternetConnected() {

    }

    @Override
    public void onLocationConnected() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
