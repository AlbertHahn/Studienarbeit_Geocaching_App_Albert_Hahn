package com.example.studienarbeit_geocaching_app_albert_hahn;


import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

/**
 * Helper class that extends ContextWrapper and delegates all of its calls to another Context
 * bridge to communicate with BroadcastReceiver
 */

public class GeofenceHelper extends ContextWrapper {

    /**
     * @value REQUEST_CODE_PENDING_INTENT code of respondens
     * @value pendingIntent is being handled by GeofenceBroadcastReceiver
     */
    private static final int REQUEST_CODE_PENDING_INTENT = 2001;
    PendingIntent pendingIntent;

    /**
     * constructer
     * @param base returns back activity context
     */

    public GeofenceHelper(Context base) {
        super(base);
    }

    /**
     * function to process geofences and add them to a geofence object
     * @param geofence a geofence with specific attributes in our case read out from a list
     * @return request from builder
     */

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
    }

    /**
     * function to build a geofence with attributes:
     * @param ID unique number
     * @param latLng location data
     * @param radius location data
     * @param transitionTypes how it behaves on trigger
     * @return geofence object
     */

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    /**
     * function to get a pending intent with username to determine, which user
     * starts to communicate with broadcast receiver
     * @param Username who is calling the broadcast receiver
     * @return gives back the pending intent with new gained information
     */

    public PendingIntent getPendingIntent(String Username) {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.putExtra("EXTRA_USER_NAME", Username);
        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_PENDING_INTENT, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    /**
     * error handling function
     * @param e status code on exception
     * @return error message
     */

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
}
