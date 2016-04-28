package com.example.jms.touristapp.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.jms.touristapp.Util.BatteryLevelMonitor;
import com.example.jms.touristapp.Util.ConnectionChangeReceiver;
import com.example.jms.touristapp.Util.LoadCategoryImage;
import com.example.jms.touristapp.Model.POI;
import com.example.jms.touristapp.R;
import com.example.jms.touristapp.Database.SqlLiteDbHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

/**
 * This class shows all Pois on the google Map
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,SensorEventListener,LocationListener {

    private GoogleMap mMap;
    // latitude and longitude variables
    private double myLatitude;
    private double myLongitude;
    private double poiLatitude;
    private double poiLongitude;
    // Sensor variables
    private float mDeclination;
    private SensorManager sensorMgr;
    private boolean orientationSensorStatus;

    private ConnectionChangeReceiver cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //BatteryLevelMonitor initialization and level save point
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(BatteryLevelMonitor.getInstance(), intentFilter);
        BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
        b.saveCurrentBatteryLevel();

        // Get parameters from called activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        myLatitude = extras.getDouble("mylatitude");
        myLongitude = extras.getDouble("mylongitude");
        poiLatitude = extras.getDouble("poilatitude");
        poiLongitude = extras.getDouble("poilongitude");

        // Sensor orientation initalization
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        orientationSensorStatus = true;

        // FAB linked to orientation sensor initialization
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchMapOrientationMode(view);
            }
        });

        // Interruption Connection Dection Enabled
        cr = new ConnectionChangeReceiver();

    }

    private void SwitchMapOrientationMode(View view) {
        String message;
        if(orientationSensorStatus){ // If orientation mode is enabled
            message = getString(R.string.map_orientation_enabled);
            // Enable orientation sensor
            Sensor orientation = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (orientation != null) {
                sensorMgr.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        else{// If orientation mode is disabled
            message = getString(R.string.map_orientation_disabled);
            //Disable orientation sensor
            sensorMgr.unregisterListener(this);
        }
        // Change orienation flag status
        orientationSensorStatus = !orientationSensorStatus ;
        // Snackbar notification
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Montreal and move the camera - Just in case of waiting
        LatLng montreal = new LatLng(45, -73);
        mMap.addMarker(new MarkerOptions().position(montreal).title("Marker in Montreal"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(montreal));
        showAllPoi();
        BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
        // Battery Level notification
        Toast.makeText(this, String.format(getString(R.string.battery_usage_map), String.valueOf(b.getLastBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
        Toast.makeText(this, String.format(getString(R.string.battery_total_usage), String.valueOf(b.getTotalBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
    }

    /**
     * Shows all POI on the google map
     */
    public void showAllPoi() {
        mMap.clear(); // Clear the map
        // Loading all POIs from database
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(this) ;
        dbHelper.openDataBase();
        ArrayList<POI> pois = dbHelper.getAllPOIfromDatabase();
        dbHelper.close();
        //Add the marker of a poi on the map
        for(POI poi:pois){
            LatLng poipos = new LatLng(poi.getLatitude(), poi.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(poipos)
                    .title(poi.getName())
                    .icon(new LoadCategoryImage(this.getApplicationContext()).get(poi.getIntro()))
            );
        }

        // Add user position marker and center to the poi position
        LatLng me = new LatLng(myLatitude, myLongitude);
        LatLng poipos = new LatLng(poiLatitude, poiLongitude);
        mMap.addMarker(new MarkerOptions().position(me).title("My position"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(poipos));


    }

    @Override
    public void onLocationChanged(Location location) {
        // Orientation sensor Geomagnetic field value for the declination
        GeomagneticField field = new GeomagneticField(
                (float)location.getLatitude(),
                (float)location.getLongitude(),
                (float)location.getAltitude(),
                System.currentTimeMillis()
        );

        // getDeclination returns degrees
        mDeclination = field.getDeclination();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Orientation Sensor => Rotate map depends on the declination value
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[]  mRotationMatrix = new float[16];

            SensorManager.getRotationMatrixFromVector(
                    mRotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float bearing = (float) (Math.toDegrees(orientation[0]) + mDeclination);
            updateCamera(bearing);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Update the camera angle by rotation parameter bearing
     * @param bearing
     */
    private void updateCamera(float bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }


    @Override
    protected void onResume() {
        // Initalize Orientation Sensor
        Sensor orientation = sensorMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (orientation != null && !orientationSensorStatus) {
            sensorMgr.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
        }
        // Initalize Connectivity Change Receiver BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(cr, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Disable Orientation Sensor
        if(!orientationSensorStatus){
            sensorMgr.unregisterListener(this);
        }
        // Unregistration of the Connectivity Change Receiver BroadcastReceiver
        unregisterReceiver(cr);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //BatteryLevelMonitor unregistration when activity or app exit
        this.unregisterReceiver(BatteryLevelMonitor.getInstance());
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
