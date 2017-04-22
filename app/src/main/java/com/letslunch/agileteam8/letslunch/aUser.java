package com.letslunch.agileteam8.letslunch;

// The purpose of this class is to contain the information useful information about the user. This class is used
// for Firebase purposes


public class aUser
{
    private String name;
    private int eatingStatus;

    /* eatingStatus values:

    -1  = Buying food
    0   = has not responded
    1   = Bringing his lunch

     */

    // Default constructor
    public aUser()
    {

    }

    public aUser(String aName)
    {
        this.name = aName;
        this.eatingStatus = 0;
    }

    // Getter
    public String getName()
    {
        return name;
    }

    public int getEatingStatus()
    {
        return eatingStatus;
    }
} // End of class
