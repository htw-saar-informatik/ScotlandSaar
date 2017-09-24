package com.denweisenseel.scotlandsaarexperimental.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by denwe on 23.09.2017.
 */

public class GameLocationListener implements LocationListener {

    private Location targetLocation;
    private GPSCallbackInterface gpsCallbackInterface;

    @Override
    public void onLocationChanged(Location location) {
        if(location.distanceTo(targetLocation) < 20) {
            gpsCallbackInterface.updatePosition(location);
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
