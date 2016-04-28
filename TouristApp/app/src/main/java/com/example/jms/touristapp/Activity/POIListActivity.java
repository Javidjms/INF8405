package com.example.jms.touristapp.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.jms.touristapp.Interface.RequiredConnectionActivityImpl;
import com.example.jms.touristapp.Model.POI;
import com.example.jms.touristapp.Util.BatteryLevelMonitor;
import com.example.jms.touristapp.Util.CheckConnectivityStatus;
import com.example.jms.touristapp.View.AboutDialog;
import com.example.jms.touristapp.View.POIGridAdapter;
import com.example.jms.touristapp.R;
import com.example.jms.touristapp.Database.SqlLiteDbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;

/**
 * This activity shows a list of all pois stored in the database into a gridview
 */
public class POIListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener,RequiredConnectionActivityImpl {

    private int CATEGORY_ID; // category id
    private final String CATEGORY_ALL = "ALL";
    private final String CATEGORY_FAVORITES = "Favorites";

    private Location lastLocation;
    private GoogleApiClient mGoogleApiClient;
    // Shaking Sensor constants
    private static float SHAKE_THRESHOLD = 15.0f;
    private static int MIN_TIME_BETWEEN_SHAKES_MILLISECS = 2000;
    // Shaking Sensor variables
    private SensorManager sensorMgr;
    private int SHAKE_COUNT;
    private long LAST_SHAKE_DETECTED_TIME;
    private long LAST_SHAKE_TIME;
    // Searchview
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poilist);
        //Toolbar initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.poi_list_toolbar_title);
        setSupportActionBar(toolbar);
        //BatteryLevelMonitor initialization and level save point
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(BatteryLevelMonitor.getInstance(), intentFilter);
        BatteryLevelMonitor b = BatteryLevelMonitor.getInstance();
        b.saveCurrentBatteryLevel();
        // Connection of the Google Api Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Sidebar (Drawer) Initialization
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setBackgroundColor(ContextCompat.getColor(this,R.color.colorTransparentBlue));
        toggle.syncState();

        // Navigation Sidebar  Initialization
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorTransparentYellow));

        // Open Database to prepare for query
        SqlLiteDbHelper dbHelper = new SqlLiteDbHelper(this);
        dbHelper.openDataBase();

        // Load category and query variable
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String category = extras.getString("category");
        String query = extras.getString("query",null);
        ArrayList<POI> pois ;

        if(query==null) { // If there is no query

            if (category.equals(CATEGORY_ALL)) { // If all spots (all category)
                pois = dbHelper.getAllPOIfromDatabase();
                CATEGORY_ID = 0;
            }else if (category.equals(CATEGORY_FAVORITES)) { // Only favorites pois
                pois = dbHelper.getAllFavoritesPOIfromDatabase();
                CATEGORY_ID = 1;
            } else { // Only the selected category poi
                pois = dbHelper.getPOIfromDatabasebyCategory(category);
                CATEGORY_ID = extras.getInt("category_id");
            }
            navigationView.getMenu().getItem(CATEGORY_ID).setChecked(true);
        }
        else{ // if query (searched category)
            pois = dbHelper.getPOIfromDatabasebyQuery(query);
            CATEGORY_ID = 0;
            navigationView.getMenu().getItem(0).setChecked(true);

        }
        // Close database
        dbHelper.close();

        // Initialize the GridView and PoiGridAdapter
        GridView gridview = (GridView) findViewById(R.id.gridView);
        POIGridAdapter poiGridAdapter = new POIGridAdapter(this, pois);
        gridview.setAdapter(poiGridAdapter);

        // Inialize shaking sensor
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Battery Level notification
        Toast.makeText(this, String.format(getString(R.string.battery_image_loading_usage), String.valueOf(b.getLastBatteryUsage() + "%")), Toast.LENGTH_LONG).show();
        Toast.makeText(this, String.format(getString(R.string.battery_total_usage), String.valueOf(b.getTotalBatteryUsage() + "%")), Toast.LENGTH_LONG).show();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!searchView.isIconified()){
                searchView.setIconified(true);
                searchView.setIconified(true);
                Intent intent =getIntent();
                intent.removeExtra("query");
                intent.putExtra("category", CATEGORY_ALL);
                finish();
                startActivity(intent);
            }
            else{
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.poilist, menu);
        // Initalize the menu options
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        // Initalize the SearchView
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String initialQuery = extras.getString("query", null);
        if(initialQuery!=null){
            searchView.setIconified(false);
            searchView.setQuery(initialQuery, false);
            intent.removeExtra("query");
            //myActionMenuItem.collapseActionView();
        }
        // SearchView Close Button listener
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =getIntent();
                intent.removeExtra("query");
                intent.putExtra("category", CATEGORY_ALL);
                finish();
                startActivity(intent);
            }
        });
        // On query listener for searching poi
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();

                // Reload the activity with query
                Intent intent = getIntent();
                intent.putExtra("query", query.trim());
                intent.putExtra("category", CATEGORY_ALL);
                intent.putExtra("category_id", 0);
                finish();
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.show_position_options) { // If see my position button clicked
            int code = CheckConnectivityStatus.CODE_BOTH_REQUIRED;
            CheckConnectivityStatus.run(this, code);
            return true;
        } else if (id == R.id.about_options) { // If dialog button clicked
            new AboutDialog(POIListActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Launches the map Activity and shows user position
     */
    public void ShowMyPosition() {
        // Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Get the last location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(lastLocation!=null) {
            // Launches Map Activity with latitude and longitude parameters
            Intent intent = new Intent(POIListActivity.this, MapsActivity.class);
            intent.putExtra("mylatitude", lastLocation.getLatitude());
            intent.putExtra("mylongitude", lastLocation.getLongitude());
            intent.putExtra("poilatitude", lastLocation.getLatitude());
            intent.putExtra("poilongitude", lastLocation.getLongitude());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int cat_id =0;
        Intent intent = getIntent();
        String cat=null;

        // Check all button id of the navigation drawer bar for category
        if (id == R.id.nav_all_spots) {
            cat = CATEGORY_ALL;
            cat_id = 0;
        }else if (id == R.id.nav_favorites) {
            cat = CATEGORY_FAVORITES;
            cat_id = 1;
            // Handle the camera action
        }else  {
            String[] category = getResources().getStringArray(R.array.category);
            TypedArray category_drawable = getResources().obtainTypedArray(R.array.category_drawable);

            // Arrays binarySearch() not working on TypedArray
            //CATEGORY_ID = Arrays.binarySearch(category,category_drawable) + 1;
            // So we iterate all categories
            for(int i=0;i<category_drawable.length();i++){
                if(category_drawable.getResourceId(i,-1)==id){
                    cat = category[i];
                    cat_id = i+2; // +2 => Output for categories ALL and FAVORITES
                    break;
                }
            }
        }

        // Reload the activity if the selected category changed
        if(CATEGORY_ID !=cat_id){
            intent.putExtra("category", cat);
            intent.putExtra("category_id",cat_id);
            finish();
            startActivity(intent);
        }
        // Close drawer for safety
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // Get user last location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int MIN_SHAKE_DETECTION = 1;
        int SINGLE_SHAKE_THRESHOLD = 500;
        // Shaking sensor event
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // If multiple sensor shake detected => Real HandShake
            if (SHAKE_COUNT >= MIN_SHAKE_DETECTION && (curTime - LAST_SHAKE_DETECTED_TIME) > MIN_TIME_BETWEEN_SHAKES_MILLISECS) {
                Toast.makeText(this, "Shake", Toast.LENGTH_SHORT).show();
                SHAKE_COUNT = 0;
                LAST_SHAKE_DETECTED_TIME = curTime;
                // Show the MapActivity if there is a WIFI and GPS Connection
                int code = CheckConnectivityStatus.CODE_BOTH_REQUIRED;
                CheckConnectivityStatus.run(this, code);
            }else{
                // If the shake is beetween the threshold value
                if(SHAKE_COUNT == 0 || (curTime - LAST_SHAKE_TIME) < SINGLE_SHAKE_THRESHOLD) {
                    // Calculate acceleration value
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;
                    // If the acceleration value is considered like a single shake
                    if (acceleration > SHAKE_THRESHOLD) {
                        LAST_SHAKE_TIME = curTime;
                        SHAKE_COUNT++;
                    }
                } else {
                    SHAKE_COUNT = 0;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
    protected void onDestroy() {
        //BatteryLevelMonitor unregistration when activity or app exit
        this.unregisterReceiver(BatteryLevelMonitor.getInstance());
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // Initialisation of the accelerometer sensor for shake
        Sensor accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Unregistration of the accelerometer sensor for shake
        sensorMgr.unregisterListener(this);
        super.onPause();
    }


    @Override
    public void onInternetConnected() {

    }

    @Override
    public void onLocationConnected() {
        ShowMyPosition();
    }


}


