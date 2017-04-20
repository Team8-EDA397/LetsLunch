package com.letslunch.agileteam8.letslunch;// The purpose of this class is to contain the information useful information about the user. This class is used
// for Firebase purposes


public class aUser
{
    private String name;

    // Default constructor
    public aUser()
    {

    }

    public aUser(String aName)
    {
        this.name = aName;
    }

    // Getter
    public String getName()
    {
        return name;
    }

} // End of class
