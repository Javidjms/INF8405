package com.example.jms.touristapp.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jms.touristapp.Config;
import com.example.jms.touristapp.Database.SqlLiteDbHelper;
import com.example.jms.touristapp.Interface.RequiredConnectionActivityImpl;
import com.example.jms.touristapp.Util.BatteryLevelMonitor;
import com.example.jms.touristapp.Util.CheckConnectivityStatus;
import com.example.jms.touristapp.Util.LoadPOIImage;
import com.example.jms.touristapp.Model.POI;
import com.example.jms.touristapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This Activity shows main details about a selected/desired POI
 */
public class POIDetailsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, RequiredConnectionActivityImpl {

    private Location lastLocation;
    private GoogleApiClient mGoogleApiClient;
    private POI poi;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidetails);
        //BatteryLevelMonitor initialization and level save point
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(BatteryLevelMonitor.getInstance(), intentFilter);
        BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
        b.saveCurrentBatteryLevel();
        // Check the connection with the google api client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Get the selected/desired poi
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        poi = (POI) extras.get("poi");
        // Load image on the relativelayout background
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl1);
        InputStream is = null;
        try {
            is = new FileInputStream(getApplicationInfo().dataDir + "/" + poi.getImage());
            Drawable drawable = Drawable.createFromStream(is, null);
            rl.setBackground(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Poi name text
        TextView title = (TextView) findViewById(R.id.poi_name_text);
        title.setText(poi.getName());
        //Poi address text
        TextView adress = (TextView) findViewById(R.id.adress_text);
        adress.setText(getString(R.string.poi_address_text) + poi.getAddress().trim());
        // Poi email text
        TextView email = (TextView) findViewById(R.id.email_text);
        final String stremail = poi.getEmail();
        email.setText(getString(R.string.poi_email_text) + (stremail != null ? stremail.trim() : " "));
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stremail != null) { // Open email writer
                    startSendEmailActivity(stremail.trim());
                }
            }
        });
        // Poi phone text
        TextView phone = (TextView) findViewById(R.id.phone_text);
        final String strphone = poi.getPhone();
        phone.setText(getString(R.string.poi_phone_text) + (strphone != null ? strphone.trim() : " "));
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTelephoneEnabled() && strphone != null) { // If phone then call the number
                    startDialActivity(strphone.trim());
                } else if (strphone != null) {
                    startInsertContactActivity(strphone.trim()); // If not phone(tab) then add into the contacts
                }
            }
        });
        // Poi website text
        TextView website = (TextView) findViewById(R.id.website_text);
        final String url = poi.getLink();
        website.setText(getString(R.string.poi_website_text) + (url != null ? url.trim() : " "));
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url != null) // Open website link with web browser
                    startWebSiteViewActivity(url.trim());
            }
        });

        // Poi distance beetween the user location
        TextView distance = (TextView) findViewById(R.id.distance_text);
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastLocation != null) { // Open Google Direction API
                    LatLng mypos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    LatLng poipos = new LatLng(poi.getLatitude(), poi.getLongitude());
                    startGoogleMapDirectionActivity(mypos, poipos);
                }
            }
        });

        ImageView static_map = (ImageView) findViewById(R.id.static_image);
        static_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Load MapActivity if Internet and GPS Connection etablished
                int codeConnection = CheckConnectivityStatus.CODE_BOTH_REQUIRED;
                CheckConnectivityStatus.run(POIDetailsActivity.this, codeConnection);
            }
        });

        // Poi Description text
        TextView description_text = (TextView) findViewById(R.id.description_text);
        description_text.setText((poi.getDescription()));

        // Poi Like Favorite FAB
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePOIFavoriteStatus(view);

            }
        });
        ChangePOIFavoriteStatus(null);

        // Load image of the poi location with Google Static Map API
        int codeConnection= CheckConnectivityStatus.CODE_INTERNET_REQUIRED;
        CheckConnectivityStatus.run(this, codeConnection);
        // BatteryLevel notification
        Toast.makeText(this, String.format(getString(R.string.battery_loading_detais_usage), String.valueOf(b.getLastBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
        Toast.makeText(this, String.format(getString(R.string.battery_total_usage), String.valueOf(b.getTotalBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
    }

    /**
     *  Change Like/Dislike POI depends on FAB status
     * @param view
     */
    private void ChangePOIFavoriteStatus(View view) {
        //Get poi favorites status
        int id = poi.getId();
        SqlLiteDbHelper sdb = new SqlLiteDbHelper(this);
        boolean favorite = sdb.getPOIFavoriteStatus(id);
        Drawable d;
        String message;
        if(view!=null){ // swap favorite status
            favorite = !favorite;
        }
        // If the poi is liked
        if(favorite){
            d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_favorite_checked, null);
            message = getString(R.string.poi_liked);
        }
        else{ // If the poi is not liked
            d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_favorite_unchecked, null);
            message = getString(R.string.poi_unliked);

        }
        fab.setImageDrawable(d); // swap fab image
        if(view !=null){ // Notification on view
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();;
            sdb.setPOIFavoriteStatus(id,favorite);
        }

    }

    /**
     * Start Activity for sending Email
     * @param email
     */
    private void startSendEmailActivity(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    /**
     * Start Google Direction Map API Intent
     * @param mypos
     * @param poipos
     */
    private void startGoogleMapDirectionActivity(LatLng mypos, LatLng poipos) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                String.format(Config.GOOGLE_MAP_DIRECTION_API_LINK, mypos.latitude, mypos.longitude, poipos.latitude, poipos.longitude)
        ));
        startActivity(intent);
    }

    /**
     * Start Activity for inserting new contact
     * @param phone
     */
    private void startInsertContactActivity(String phone) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        startActivity(intent);
    }

    /**
     * Start Activity for opening web site url
     * @param url
     */
    private void startWebSiteViewActivity(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Get user last location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null) {
            TextView distancetext = (TextView) findViewById(R.id.distance_text);
            Location poilocation = new Location("poi");
            poilocation.setLatitude(poi.getLatitude());
            poilocation.setLongitude(poi.getLongitude());
            int distance = (int) poilocation.distanceTo(lastLocation);
            distancetext.setText(String.format(getString(R.string.poi_distance_text), distance));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Start Activity for dialing
     * @param phone phone number to dial
     */
    private void startDialActivity(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    /**
     * Check if the device allows phone dials
     * @return
     */
    private boolean isTelephoneEnabled() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    @Override
    protected void onDestroy() {
        //BatteryLevelMonitor unregistration when activity or app exit
        this.unregisterReceiver(BatteryLevelMonitor.getInstance());
        super.onDestroy();
    }

    @Override
    public void onInternetConnected() {
        // Load Google Static Map API Image
        ImageView static_map = (ImageView) findViewById(R.id.static_image);
        String static_link = String.format(Config.GOOGLE_MAP_STATIC_API_LINK, poi.getLatitude(), poi.getLongitude());
        new LoadPOIImage(this, static_map, static_link).execute();
        }

    @Override
    public void onLocationConnected() {
        // Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Get user last location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(lastLocation!=null){
            Intent intent = new Intent(POIDetailsActivity.this,MapsActivity.class);
            intent.putExtra("mylatitude",lastLocation.getLatitude());
            intent.putExtra("mylongitude",lastLocation.getLongitude());
            intent.putExtra("poilatitude",poi.getLatitude());
            intent.putExtra("poilongitude",poi.getLongitude());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition( R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
