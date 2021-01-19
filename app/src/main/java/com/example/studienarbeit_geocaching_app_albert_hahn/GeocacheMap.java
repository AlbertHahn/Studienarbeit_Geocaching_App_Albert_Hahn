package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.navigation.NavigationView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.text.InputType;
import android.util.Log;

import android.view.MenuItem;

import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * The actual MainActivity class, almost everything Geocaching and Map related is here implemented
 * as well as the toolbar which maintains all the functions that are relevant for as above mentioned implementation
 */

public class GeocacheMap extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {


    /**
     * Permission related variables
     */
    private static final String TAG = "GeocacheMap";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10001;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private static final int WRITE_EXTERNAL_STORAGE_ACCESS_REQUEST_CODE = 10003;
    public boolean mLocationPermissionGranted = false;
    public boolean mBackgroundPermissionGranted = false;
    public boolean mWritePermissionGranted = false;

    /**
     * Objects from different classes like helper classes etc.
     */

    DataBaseHelper dataBaseHelper;
    LocationManager locationManager;
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    // Initialized list for geofence functions
    List<Geofence> geofenceList = new ArrayList<>();

    /**
     * View elements, buttons, toolbar etc.
     */
    private TextView mCurrentLevel, mUserName, mGeofenceRadiusSliderText;
    private DrawerLayout mdrawerLayout;
    private SwitchCompat mGeofencingSwitch, mSpeechSwitch;
    private SeekBar mGeofenceRadiusSlider;
    private ProgressBar mExperienceBar;
    private ActionBarDrawerToggle toggle;

    // Radius for geofences default value 50
    private int GEOFENCE_RADIUS = 50;


    /**
     * Variables for shared preferences
     */
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SWITCH_SPEECH = "switch_speech";
    public static final String SWITCH_GEOFENCING = "switch_geofencing";
    /**
     * View Elements, buttons and Switch for shared preferences
     */

    public boolean mTextToSpeech;
    public boolean mGeofencingSwitched;
    private String mDialogText;


    /**
     * @param mMap Definition of the google maps map
     * @poram mDrawableName String array with all drawable geocaches
     */

    private GoogleMap mMap;
    String[] mDrawableName = {
            "smartphone", "grill", "mask", "baseball", "basketball",
            "bell", "bike", "cake", "coffee", "controller",
            "cricketstick", "football", "golfball", "iceskates", "moped",
            "piano", "pokeball", "sanitizer", "tools", "trophy"
    };

    /**
     * Object initialization for the broadcastreceiver, if callback from GeofenceBroadcastReiver class
     * the UI will be updated with new information for the toolbar and map like the increased experience,
     * level up or redrawing of the map, when a geocache was found
     */


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String UserName = getIntent().getStringExtra("EXTRA_USER_NAME");
            updateUI(UserName);
        }
    };

    /**
     * If app is onDestroy unregister Receiver
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);

        /**
         * @param UserName acquire the username from login, to gain experience
         *                 level, and geocaches in your list
         */

        final String UserName = getIntent().getStringExtra("EXTRA_USER_NAME");

        // checkpermissions on start!
        checkPermission();
        checkPermissionGeofence();

        Log.d(TAG, "LOCATION:" + mLocationPermissionGranted + "/n" + "BACKGROUND:" + mBackgroundPermissionGranted);

        // get current location if permission is granted
        getLocation();

        // Initialize helper Objects
        dataBaseHelper = new DataBaseHelper(GeocacheMap.this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        // clean up old geofences if possible
        geofencingClient.removeGeofences(geofencePendingIntent);

        // Register receiver on an Intentfilter to gain knowledge if geofence was triggered
        registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_UI"));

        /**
         * Declaration of all necessary view elements, toolbar functions and onclicklisteners
         */

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        mdrawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(GeocacheMap.this);

        // Menu items
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.miItem4);
        mGeofencingSwitch = menuItem.getActionView().findViewById(R.id.GeofencingSwitch);

        // Button to turn on/off geofencing
        mGeofencingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Generate list with geofences and insert username that is currently using the app
                    checkPermissionGeofence();
                    if(mBackgroundPermissionGranted==true)
                    {
                        addGeofenceList(UserName);
                        saveData();
                    }else{
                        isChecked=false;
                    }
                } else {
                    // Set geofencing off and remove all geofences and
                    // cancel pending intents to the broadcast receiver
                    geofencingClient.removeGeofences(geofencePendingIntent);
                    if (geofencePendingIntent != null) {
                        geofencePendingIntent.cancel();
                    }
                    saveData();
                }

            }
        });


        // Menu item for text to speech functionality
        MenuItem menuItemSpeech = navigationView.getMenu().findItem(R.id.miTextToSpeech);
        mSpeechSwitch = menuItemSpeech.getActionView().findViewById(R.id.GeofencingSwitch);

        // Text to speech function
        mSpeechSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // save this option
                    saveData();
                } else {
                    // save this option
                    saveData();
                }
            }
        });


        // Menu item for geofence radius slider
        MenuItem menuItemSlider = navigationView.getMenu().findItem(R.id.miSlider);
        mGeofenceRadiusSlider = menuItemSlider.getActionView().findViewById(R.id.GeofenceRadiusSlider);
        mGeofenceRadiusSliderText = menuItemSlider.getActionView().findViewById(R.id.GeofenceRadiusSliderText);

        // Listener to change radius
        mGeofenceRadiusSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set the slider progress as you wish from 1-100
                GEOFENCE_RADIUS = progress + 1;
                mGeofenceRadiusSliderText.setText(String.valueOf(progress));
                // Redraw map
                updateMap();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // toggle for the Actiontoolbar
        toggle = new ActionBarDrawerToggle(GeocacheMap.this, mdrawerLayout, toolbar, R.string.open, R.string.close);
        mdrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Header of the Toolbar and view elements as well as functions
        mCurrentLevel = navigationView.getHeaderView(0).findViewById(R.id.CurrentLevel);
        mUserName = navigationView.getHeaderView(0).findViewById(R.id.UserNameProfile);
        showLevel(UserName);

        mExperienceBar = navigationView.getHeaderView(0).findViewById(R.id.ExperienceBar);
        showExperience(UserName);

        // Load shared preferences and update UI
        loadData();
        updateViews();
    }

    /**
     * used for clearing and redrawing the map off geofences and map makers,
     * if for example geocache was found and the map needs to be cleared and redrawn
     */

    public void updateMap() {
        // initialize map
        initMap();
        // clear map if not null
        if (mMap != null) {
            mMap.clear();
        }
        // cancel all geofence pendings to broadcast receiver
        if (geofencePendingIntent != null) {
            geofencePendingIntent.cancel();
        }
        // remove all geofences
        geofencingClient.removeGeofences(geofencePendingIntent);
        // redraw google markers to map
        addItems();
    }

    /**
     * used for clearing and redrawing the map off geofences and map makers, as well as updating
     * the nav header with informations dependent for the user, that is currently logged in.
     * UI elements like textviews and progressbar will be refreshed
     * @param UserName to display the right information that was given
     */

    public void updateUI(String UserName) {
        // clear map if not null
        if (mMap != null) {
            mMap.clear();
        }
        // cancel all geofence pendings to broadcast receiver
        if (geofencePendingIntent != null) {
            geofencePendingIntent.cancel();
        }
        // remove all geofences
        geofencingClient.removeGeofences(geofencePendingIntent);
        // redraw google markers to map
        addItems();
        // add geofences with username
        addGeofenceList(UserName);
        // update UI
        showExperience(UserName);
        showLevel(UserName);
    }

    /**
     * Update the experience bar that can be found in the nav header
     * @param UserName to look up, which experience progress should show up
     */

    private void showExperience(String UserName) {
        List<UserModel> userlist = dataBaseHelper.selectAllUserModel();
        boolean foundUserName = false;

        if (userlist != null) {
            // search through list
            for (int i = 0; i < userlist.size(); i++) {
                // if username equals list name set progress
                if (userlist.get(i).getName().equals(UserName)) {
                    mExperienceBar.setProgress(userlist.get(i).getExperience());
                    foundUserName = true;
                }
            }
            // if not progress equals 0
            if (!foundUserName) {
                mExperienceBar.setProgress(0);
            }
        }
    }

    /**
     * Update the level and username UI
     * @param UserName to look up, which level and username should show up
     */

    private void showLevel(String UserName) {
        List<UserModel> userlist = dataBaseHelper.selectAllUserModel();
        boolean foundUserName = false;

        if (userlist != null) {
            // search through list
            for (int i = 0; i < userlist.size(); i++) {
                // if username equals list name set level and name to ui
                if (userlist.get(i).getName().equals(UserName)) {
                    mCurrentLevel.setText("LEVEL " + userlist.get(i).getLevel());
                    mUserName.setText(UserName);
                    foundUserName = true;
                }

            }
            // if not Error as set text
            if (!foundUserName) {
                mCurrentLevel.setText("USERNAME NOT FOUND");
                mUserName.setText("USERNAME NOT FOUND");
            }
        }
    }

    /**
     * Data functions for shared preferences
     * {@link #saveData()} saves the preferences
     * {@link #loadData()} loads the preferences
     * {@link #updateViews()} updates the view elements
     */
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH_GEOFENCING, mGeofencingSwitch.isChecked());
        editor.putBoolean(SWITCH_SPEECH, mSpeechSwitch.isChecked());
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mTextToSpeech = sharedPreferences.getBoolean(SWITCH_SPEECH, false);
        mGeofencingSwitched = sharedPreferences.getBoolean(SWITCH_GEOFENCING, false);
    }

    public void updateViews() {
        mSpeechSwitch.setChecked(mTextToSpeech);
        mGeofencingSwitch.setChecked(mGeofencingSwitched);
    }

    /**
     * Adds geofences to the map with the given coordinates of geocache that hasn't been found yet
     * main usage of the geofence helper class happens here
     * @param UserName to setup geofences for given user
     */


    private void addGeofenceList(String UserName) {
        if (mGeofencingSwitch.isChecked()) {
            // Check if its allowed
            checkPermission();
            checkPermissionGeofence();
            if (mLocationPermissionGranted && mBackgroundPermissionGranted) {
                // Get list with database helper
                List<GeocacheModel> list = dataBaseHelper.selectAll();
                // Clear old geofenceList
                geofenceList.clear();


                if (list != null) {
                    // Search through list
                    for (int i = 0; i < list.size(); i++) {

                        // If geocache wasn't found
                        if (!list.get(i).found()) {

                            // Build a geofence with list values
                            final Geofence geofence = geofenceHelper.getGeofence(Integer.toString(i + 1), new LatLng(list.get(i).getlatitude(), list.get(i).getlongitude()),
                                    GEOFENCE_RADIUS, Geofence.GEOFENCE_TRANSITION_ENTER);
                            // build request to Broadcast receiver
                            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
                            // build username with the intent to geofence helper
                            PendingIntent pendingIntent = geofenceHelper.getPendingIntent(UserName);
                            // add the geofence with request and intent to the client
                            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: Geofence Added...");
                                            geofenceList.add(geofence);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            String errorMessage = geofenceHelper.getErrorString(e);
                                            Log.d(TAG, "onFailure: " + errorMessage);
                                        }
                                    });
                        }
                    }
                }
            } else {
                // No permissions for geofencing
                Toast.makeText(GeocacheMap.this, "PERMISSION REQUIRED FOR GEOFENCE FUNCTIONS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Function for drawing markers, circles and adding icons onto the map
     * circle can be changed through the slider
     */

    private void addItems() {

        // Gets list of all available Geocaches
        List<GeocacheModel> list = dataBaseHelper.selectAll();


        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                // If geocache wasn't already found by someone
                if (!list.get(i).found()) {
                    boolean found = false;
                    // Get coordinates from list
                    LatLng latLng = new LatLng(list.get(i).getlatitude(), list.get(i).getlongitude());

                    // Draw Circles
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(latLng);
                    circleOptions.radius(GEOFENCE_RADIUS);
                    circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                    circleOptions.fillColor(Color.argb(64, 255, 0, 0));
                    circleOptions.strokeWidth(4);
                    mMap.addCircle(circleOptions);


                    // go through string array with icon names
                    for (int j = 0; j < mDrawableName.length; j++) {
                        // search through list and look if name exists as an icon
                        if (list.get(i).getName().toLowerCase().equals(mDrawableName[j].toLowerCase())) {
                            // get R.drawable id and match it with the given list and array strings
                            Log.d(TAG, list.get(i).getName().toLowerCase() + " /" + mDrawableName[j].toLowerCase());
                            int resID = getResources().getIdentifier(mDrawableName[j], "drawable", getPackageName());
                            // add icon to map
                            mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(bitmapDescriptorFromVector(this, resID))
                                    .title(mDrawableName[j]));
                            found = true;
                        }
                    }

                    // if drawablename couldn't be found in string array just add an icon amu_bubble_mask
                    if (!found) {
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(bitmapDescriptorFromVector(this, R.drawable.amu_bubble_mask))
                                .title(list.get(i).getName()));
                    }

                }
            }
        }
    }

    /**
     * BitmapDescriptor function to convert drawables into Bitmaps, takes a vector image an converts it to a bitmap
     * This function has been implemented due to an error by compiling the bitmaps raw
     * @param context given context
     * @param vectorResId resource id of the drawable item
     * @return the passed vector image as bitmap
     */

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * A menu selection for the toolbar with lists, logout and a save function
     * @param item which function has been chosen
     * @return state value
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.miHideCacheQR:
                // start barcode scanner app
                if (getLocation()) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                    scanIntegrator.initiateScan();
                }
                return true;
            case R.id.miFoundCaches:
                // start class activity for a list with already found geocaches
                Intent intentfound = new Intent(GeocacheMap.this, GeocacheList.class);
                intentfound.putExtra("EXTRA_USER_NAME", mUserName.getText().toString());
                startActivity(intentfound);
                return true;
            case R.id.miRemainingCaches:
                // start class activity for a list with remaining geocaches
                Intent y = new Intent(GeocacheMap.this, GeocacheListRemaining.class);
                startActivity(y);
                return true;
            case R.id.miLogout:
                // Back to login screen
                Intent mainintent = new Intent(GeocacheMap.this, LoginScreen.class);
                startActivity(mainintent);
                return true;
            case R.id.miFastSelect:
                // starts fragment to fast select items and place them as geocache to the map
                Bundle bundle = new Bundle();
                // put information that will be needed for the fragment in a bundle and pass it through
                bundle.putStringArray("DrawablesNames", mDrawableName);
                bundle.putString("EXTRA_USER_NAME", mUserName.getText().toString());
                PlaceItemFragment placeItemFragment = new PlaceItemFragment();
                placeItemFragment.setArguments(bundle);
                // start fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, placeItemFragment).addToBackStack("PlaceItem").commit();
                // close toolbar
                mdrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.miSaveGPX:

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert_New);
                builder.setTitle("Choose a filename, please! (The file will be saved in your Downloads directory)");
                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setTextColor(R.style.Theme_AppCompat_DayNight_DarkActionBar);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDialogText = input.getText().toString();
                        if (mDialogText == null) {
                            dialog.cancel();
                        }
                        checkWritePermission();
                        XmlHelper xmlHelper = new XmlHelper();
                        boolean succes = xmlHelper.createGPX(mDialogText, dataBaseHelper.selectAll());
                        if (!succes) {
                            Toast.makeText(GeocacheMap.this, "FAILED", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;

        }
        mdrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Actions if back button is pressed closes the toolbar
     * or looks if there is a fragment on top of the activity
     * and pops it back from its stack
     */

    @Override
    public void onBackPressed() {
        // Toolbar actions
        if (mdrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mdrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Fragment on top or not?
            int count = getSupportFragmentManager().getBackStackEntryCount();

            // If no go back else close fragment
            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    /**
     * function to get current location data
     * if gps is not enabled ask the user to activate it
     */


    public boolean getLocation() {
        // check permission
        checkPermission();
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            if (mLocationPermissionGranted) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // if gps is deactivated tell user to activate it
                    NoGpsDialog();
                    // still not convinced deny return value
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        return false;
                    }
                } else {
                    // Get current location from location manager
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, (LocationListener) GeocacheMap.this);
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Simple alert message dialog to ask the user to enable GPS
     */

    private void NoGpsDialog() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert_New);
        builder.setMessage("Your GPS is not enabled, this function needs to know your location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        builder.show();
        //final AlertDialog alert = builder.create();
        //alert.show();
    }


    /**
     * onProvider methods, if anything changes
     * @param provider which provider should be processed
     */

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
    }


    /**
     * Updates if location of the user has changed
     * @param location current location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
    }


    /**
     * onMapReady callback to initialize google maps and it's functions
     * @param googleMap declares map that will be displayed as fragment
     */

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if(mLocationPermissionGranted){
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        addItems();
    }


    /**
     * initialize map and get map onto fragment
     * sync. with changes and callback to onMapReady
     */

    public void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Code Source - https://developer.android.com/training/permissions/requesting.html#java
     * Standard permission check to get access to FINE and COARSE location
     */

    public void checkPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                // if true set bool for it to true
                mLocationPermissionGranted = true;
                initMap();
            }else{
                // request the needed permission
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            // request the needed permission
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    /**
     * Standard permission check for WRITE_EXTERNAL_STORAGE
     */

    public void checkWritePermission(){

        if (ContextCompat.checkSelfPermission(
                GeocacheMap.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            mWritePermissionGranted = true;

        }else {
            ActivityCompat.requestPermissions(GeocacheMap.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_ACCESS_REQUEST_CODE);
        }
    }

    /**
     * Standard permission check for ACCESS_BACKGROUND_LOCATION only need for API 29 and higher
     * so below these API levels bool is always true
     */


    public void checkPermissionGeofence() {

        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mBackgroundPermissionGranted = true;
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            mBackgroundPermissionGranted =true;
        }


    }


    /**
     * https://developer.android.com/training/permissions/requesting.html#java
     * @param requestCode which request for permissions has been send
     * @param permissions which perimssion should be granted or isn't granted
     * @param grantResults size of granted result
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }  else {
                    Toast.makeText(this, "Location access is necessary for the APP to work properly", Toast.LENGTH_SHORT).show();
                }
            case BACKGROUND_LOCATION_ACCESS_REQUEST_CODE:

                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mBackgroundPermissionGranted = true;
                        Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
                    } else {
                        mBackgroundPermissionGranted = false;
                        Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
                    }


            case WRITE_EXTERNAL_STORAGE_ACCESS_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mWritePermissionGranted = true;
                    Toast.makeText(this, "Write permission true", Toast.LENGTH_SHORT).show();
                } else {
                    mWritePermissionGranted = false;
                }

            default:
                return;

        }

    }



    /**
     * ZXING integration to be activated if intent to Barcode scanner was sent
     * @param requestCode on which code should the activity act
     * @param resultCode result of an barcode
     */

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // if permission is granted scan QR code
        checkPermission();
        if(mLocationPermissionGranted) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        // Initilize datahelper objects
        dataBaseHelper = new DataBaseHelper(GeocacheMap.this);
        String provider = LocationManager.GPS_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);

            if (scanningResult != null) {

                // Initialize geocachemodel to insert new db entries
                GeocacheModel geocacheModel;
                // Get scanContent from qr code and put it into a string
                String scanContent = scanningResult.getContents();

                if (scanContent != null) {

                    // New geocachemodel with scan content and location data
                    geocacheModel = new GeocacheModel(-1, scanContent, location.getLatitude(), location.getLongitude(), false, mUserName.getText().toString());
                    // Insert into data table
                    dataBaseHelper.AddOne(geocacheModel);

                    Toast.makeText(GeocacheMap.this, "ADDED", Toast.LENGTH_SHORT).show();

                    // if map available clear it and redraw marker
                    if (mMap != null) {
                        mMap.clear();
                        addItems();
                    }
                }

            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            Toast.makeText(GeocacheMap.this,"no permission to do this", Toast.LENGTH_SHORT).show();
        }
    }


}




