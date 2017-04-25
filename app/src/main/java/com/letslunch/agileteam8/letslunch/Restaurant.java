package com.letslunch.agileteam8.letslunch;

/**
 * Created by miriam on 4/25/2017.
 */

public class Restaurant {
    private String name;
    private long latitude;
    private long longitude;
    private String id;

    public Restaurant(String resId, String resName, long resLatitude, long resLongitude){
        this.latitude = resLatitude;
        this.longitude = resLongitude;
        this.name = resName;
        this.id = resId;
    }

    public String getName() {
        return name;
    }

    public long getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public long getLatitude() {
        return latitude;
    }

}
