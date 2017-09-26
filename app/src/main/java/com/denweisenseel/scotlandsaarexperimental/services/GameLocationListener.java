package com.denweisenseel.scotlandsaarexperimental.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by denwe on 23.09.2017.
 */

public class GameLocationListener implements LocationListener {

    private Location targetLocation;
    private GPSCallbackInterface gpsCallbackInterface;

    public GameLocationListener(GPSCallbackInterface gpsCallbackInterface) {
        this.gpsCallbackInterface = gpsCallbackInterface;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(targetLocation != null) {
            if(location.distanceTo(targetLocation) < 20) {
                gpsCallbackInterface.updatePosition(location);
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

    public interface GPSCallbackInterface {
        void gpsDeactivated(String s);
        void updatePosition(Location location);
    }
}
