package com.letslunch.agileteam8.letslunch;

/**
 * Created by pedrogomezlopez on 22/04/2017.
 */

// The purpose of this class is to contain the information useful information about the user. This class is used
// for Firebase purposes


public class aUser
{
    private String name;
    private String eatingStatus;


    // Default constructor
    public aUser()
    {

    }

    public aUser(String aName)
    {
        this.name = aName;
        this.eatingStatus = "No Status";
    }

    // Getter
    public String getName()
    {
        return name;
    }

    public String getEatingStatus()
    {
        return eatingStatus;
    }
} // End of class