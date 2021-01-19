package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * GeofenceBroadcastReceiver extended class of BroadcastReceiver
 * retrieves intents if geofence has been triggered and manages function calls
 * like LevelUpUser, or GeocacheWasFound from data basehelper
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    /**
     * @value TAG to identify Log messages
     * @value TextToSpeech looks up if TTS is enabled or not
     * @value geocacheMap object to call functions out of the "Main activity"
     */

    String TAG = "GeofenceBroadcastReceiver";
    private boolean TextToSpeech = false;
    GeocacheMap geocacheMap;


    /**
     * onReceive is been called, when pendingintent goes through from GeocacheMap
     * @param context which activity is being used to process
     * @param intent to communicate with the background service BroadcastReceiver
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle received information
        onHandleIntent(context,intent);
        // send back response as action "UPDATE_UI" to
        // communicate GeocacheMap that something changed
        context.sendBroadcast(new Intent("UPDATE_UI"));
    }


    /**
     * Handler that processes incoming intents that has been called by triggering geofences
     * geofences can only be triggered by other users not the same that added them in the first place
     * @param intent for communication passes through information like username that triggered the geofences
     */

    void onHandleIntent(Context context ,Intent intent) {
        // retrieve the GeofencingEvent from the passed Intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        // get the Geofences that triggered it
        List<Geofence> fences = geofencingEvent.getTriggeringGeofences();


        // retrieve intent with extra string to know which user triggered the geofence
        String UserName;
        UserName = intent.getStringExtra("EXTRA_USER_NAME");

        // get shared preference to determine if TextToSpeech is on
        SharedPreferences pref = context.getSharedPreferences("sharedPrefs", context.MODE_PRIVATE);
        TextToSpeech = pref.getBoolean("switch_speech", false);
        Log.d("BROADCASTPREF", TextToSpeech + "" );


        // checking there is only one fence retrieve the request Id
        if (fences == null || fences.size() != 1) {
            return;
        }
        String fenceID = fences.get(0).getRequestId();

        // Initialize objects to retrieve list of the type GeocacheModel and UserModel
        geocacheMap = new GeocacheMap();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        GeocacheModel geocacheModel;
        UserModel userModel;
        // GeocacheModel list
        List<GeocacheModel> list = dataBaseHelper.selectAll();
        // UserModel list
        List<UserModel> userlist = dataBaseHelper.selectAllUserModel();

        if(list != null) {
            // go through list
            for (int i = 0; i < list.size(); i++) {
                // if geofence id and list id as well as the entry to username and given username from intent are equal proceed
                if(list.get(i).get_id() == Integer.parseInt(fenceID) && !list.get(i).getUserName().equals(UserName))
                {
                    // create geocachemodel and insert it into GeocacheWasFound the Geocache Table will now be updated
                    geocacheModel = new GeocacheModel(list.get(i).get_id(),
                            list.get(i).getName() , list.get(i).getlatitude(),
                            list.get(i).getlongitude(), true, list.get(i).getUserName());

                    dataBaseHelper.GeocacheWasFound(geocacheModel, UserName);


                    // TextToSpeec is on?
                    if(TextToSpeech)
                    {
                        // create new intent and start service for tts
                        Intent speechIntent = new Intent();
                        speechIntent.setClass(context, TTS.class);
                        speechIntent.putExtra("MESSAGE", list.get(i).getName());
                        context.startService(speechIntent);
                    }
                    else
                    {
                        Toast.makeText(context, list.get(i).getName() , Toast.LENGTH_SHORT).show();
                    }

                    // remove triggered geofence from list
                    if(fences.get(0) != null)
                    {
                        fences.remove(0);
                    }


                    // if userlist is given
                    if(userlist!=null)
                    {
                        // go through list
                        for(int j = 0; j < userlist.size();j++)
                        {
                            if(userlist.get(j).getName().equals(UserName))
                            {
                                // create usermodel with list values
                                userModel = new UserModel(userlist.get(j).getid(),userlist.get(j).getName(),userlist.get(j).getPassword(),userlist.get(j).getLevel(),userlist.get(j).getExperience());
                                // give experience or level up the user that has triggered the geofence
                                dataBaseHelper.LevelUpUser(userModel);
                            }
                            else {
                                Log.d(TAG,"ERROR NO VALID ENTRY");
                            }
                        }
                    }
                }
                else{
                    Log.d(TAG,"ERROR NO VALID ENTRY");
                }


            }
        }

    }

}

