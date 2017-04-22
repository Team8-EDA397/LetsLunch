package com.letslunch.agileteam8.letslunch;

/**
 * Created by pedrogomezlopez on 20/04/2017.
 */

public class Group
{
    private String name;
    private String location;
    private String time;
    private String id;

    // Default constructor
    public Group()
    {

    }

    public Group(String aName, String aLocation, String aTime, String anID)
    {
        this.name       = aName;
        this.location   = aLocation;
        this.time       = aTime;
        this.id         = anID;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getID() {
        return id;
    }

} // End of class