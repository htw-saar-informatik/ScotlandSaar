package com.denweisenseel.scotlandsaarexperimental.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by denwe on 23.09.2017.
 */

public class GameLocationListener implements LocationListener {

    private static final String TAG = "GameLocationListener";
    private Location targetLocation;
    private GPSCallbackInterface gpsCallbackInterface;

    public GameLocationListener(GPSCallbackInterface gpsCallbackInterface) {
        this.gpsCallbackInterface = gpsCallbackInterface;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Callback initiated, comparing " +location+" to: " +targetLocation);
        if(targetLocation != null) {
            if(location.distanceTo(targetLocation) < 20) {
                Log.i(TAG, "THEYRE CLOSE");
                gpsCallbackInterface.updatePosition(location);
            } else {
                Log.i(TAG, "NOT CLOSE ENOUGH");
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        gpsCallbackInterface.gpsDeactivated(s);
    }

    public void setTargetLocation(LatLng targetLocation) {
        this.targetLocation = new Location("");
        this.targetLocation.setLatitude(targetLocation.latitude);
        this.targetLocation.setLongitude(targetLocation.longitude);
    }

    public interface GPSCallbackInterface {
        void gpsDeactivated(String s);
        void updatePosition(Location location);
    }
}
