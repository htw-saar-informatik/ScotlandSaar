package com.denweisenseel.com.backend.data;

import com.google.appengine.repackaged.com.google.gson.JsonObject;

/**
 * Created by denwe on 23.07.2017.
 */

public class Geolocation {

    double latitude;
    double longitude;

    public Geolocation(double lat, double lon) {
        double latitude = lat;
        double longitude = lon;
    }

    public Geolocation() {

    }

    public double distanceBetweenGeolocationInMetres(Geolocation location) {
        double earthRadiusKm = 6371e3;

        double lat1 = latitude;
        double lat2 = location.getLatitude();

        double lon1 = longitude;
        double lon2 = location.getLongitude();

        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (earthRadiusKm * c) / 1000;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.addProperty("Latitude", latitude);
        data.addProperty("Longitude", longitude);
        return data;
    }

    public String toString() {
        return "[Location:"+latitude+ "," + longitude + "]";
    }

}
