package com.letslunch.agileteam8.letslunch;

// The purpose of this class is to contain the information useful information about the user. This class is used
// for Firebase purposes


public class User
{
    private String name;
    private String eatingStatus;

    // Default constructor
    public User() {

    }

    public User(String aName) {
        this.name = aName;
        this.eatingStatus = "No Status";
    }

    public User(String aName, String aStatus) {
        this.name = aName;
        this.eatingStatus = aStatus;
    }

    // Getter
    public String getName() {
        return name;
    }

    public String getEatingStatus() {
        return eatingStatus;
    }
} // End of class
