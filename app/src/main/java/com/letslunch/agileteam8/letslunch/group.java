package com.letslunch.agileteam8.letslunch;


public class group
{
    private String name;
    private String location;
    private String time;
    private String ID;

    // Default constructor
    public group()
    {

    }

    public group(String aName, String aLocation, String aTime, String anID)
    {
        this.name       = aName;
        this.location   = aLocation;
        this.time       = aTime;
        this.ID         = anID;
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
        return ID;
    }
} // End of class
