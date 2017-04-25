package com.letslunch.agileteam8.letslunch;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by miriam on 4/25/2017.
 */

public class Restaurant {
    private String name;
    private double latitude;
    private double longitude;
    private String id;
    private LatLng latLng;

    public Restaurant(String resId, String resName, double resLatitude, double resLongitude, LatLng resLatLng){
        this.latitude = resLatitude;
        this.longitude = resLongitude;
        this.name = resName;
        this.id = resId;
        this.latLng = resLatLng;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public LatLng getLatLng() { return new LatLng(latitude, longitude); }

}
