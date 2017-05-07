package com.letslunch.agileteam8.letslunch.Utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.letslunch.agileteam8.letslunch.Activities.CreateGroupActivity;
import com.letslunch.agileteam8.letslunch.Activities.HomePageActivity;
import com.letslunch.agileteam8.letslunch.Activities.MainActivity;
import com.letslunch.agileteam8.letslunch.Group;
import com.letslunch.agileteam8.letslunch.User;
/**
 * Created by Carl-Henrik Hult on 2017-05-07.
 */

public class DBHandler
{
    private static DBHandler ourInstance = new DBHandler();
    public FirebaseAuth firebaseAuth;
    public DatabaseReference databaseReference;
    public FirebaseUser currentUser;
    Activity activity;
    private String currentGroupID;
    public static synchronized DBHandler getInstance()
    {
        if(ourInstance == null)
            return ourInstance = new DBHandler();
        else
            return ourInstance;
    }

    /**
     * Init of DBHandler
     */
    private DBHandler()
    {
        this.firebaseAuth       = FirebaseAuth.getInstance();
        this.databaseReference  = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Sets the current activity to the @param activity
     * @param activity is the activity that is using the database
     */
    public void setActivity(Activity activity){
        this.activity = activity;

        Toast.makeText(activity, "Inside the DBHandler, activity set", Toast.LENGTH_SHORT).show();
    }
    //----------------------------------------------------------------------------------------------


    public void setCurrentGroupID(String groupID)
    {
        currentGroupID = groupID;
    }

    /**
     * Checks if there is a user currently signed in.
     * @return returns true if user already is signed in
     */
    public boolean isUserAlreadySignedIn()
    {
        Toast.makeText(activity, "Inside the DBHandler, checking user", Toast.LENGTH_SHORT).show();

        if(firebaseAuth.getCurrentUser() != null)
        {
           currentUser = firebaseAuth.getCurrentUser();
            return true;
        }

        Toast.makeText(activity, "Inside the DBHandler, user was null ", Toast.LENGTH_SHORT).show();

        return false;

    }

    /**
     * Checks if all the needed information was provided to correctly sign in a user.
     * @param email
     * @param password
     * @return
     */
    public boolean signInValid(String email,String password)
    {
        if (TextUtils.isEmpty(email))
        {
            // Notify user the "Email" field is empty
            Toast.makeText(activity, "Please enter an email", Toast.LENGTH_SHORT).show();

            return false;
        }
        else if (TextUtils.isEmpty(password))
        {
            // Notify user the "Password" field is empty
            Toast.makeText(activity, "Please enter a Password", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }


    /**
     * Sets the user eating status on the server
     * @param userStatus
     */
    public void userResponseToEating(String userStatus)
    {

        // Get the display name of the user
        String userDisplayName  = currentUser.getDisplayName();

        // Create an User object
        User updatedUser = new User(userDisplayName, userStatus);

        // Update the current user eating status by storing the User object
        databaseReference.child("GroupsAndTheirMembers").child(currentGroupID).child(currentUser.getUid()).setValue(updatedUser).addOnCompleteListener(activity, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(activity, "Your status has been updated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(activity, "Unable to let your friends know.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Returns the current users UID
     * @return
     */
    public String getUser(){
        return currentUser.getUid();
    }

    // The purpose of this function is to set the display name of the user

    /**
     * Changes the user display name in the database
     * @param name
     */
    public void setUserDisplayName(String name)
    {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
    }

    /**
     * Signs out the user and starts the login activity.
     */
    public void signUserOut()
    {
        // Logout from Firebase
        firebaseAuth.signOut();
        // Finishing the current activity
        activity.finish();

        // Jumping to the login activity
        activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
    }


    // Adding the creator of the group to the group he created.

    /**
     * Adding the current to aGroup
     * @param aGroup
     */
    public void addingCurrentUserToGroup(Group aGroup)
    {
        // A firebase user object

        // Create User object
        User myUser = new User(currentUser.getDisplayName());

        //Firebase logic for creating group
        this.databaseReference.child("GroupsAndTheirMembers").child(aGroup.getID()).child(currentUser.getUid()).setValue(myUser).addOnCompleteListener(activity, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(activity, "Unable to add you to the created group. Try Manually.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    // The purpose of this function is to assign to a given owner the groups he belongs to
    public void addingGroupToCurrentUser(Group createdGroup)
    {
        //Firebase logic for creating group
        this.databaseReference.child("UserAndTheirGroups").child(firebaseAuth.getCurrentUser().getUid()).child(createdGroup.getID()).setValue(createdGroup).addOnCompleteListener(activity, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(activity, "Unable to add assign a group to you.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



}
