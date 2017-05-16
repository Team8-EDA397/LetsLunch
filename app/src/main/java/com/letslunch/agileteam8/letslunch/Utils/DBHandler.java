package com.letslunch.agileteam8.letslunch.Utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.letslunch.agileteam8.letslunch.Activities.MainActivity;

import com.letslunch.agileteam8.letslunch.Group;
import com.letslunch.agileteam8.letslunch.Restaurant;
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

    String prevSelection;
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

        //Toast.makeText(activity, "Inside the DBHandler, activity set", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(activity, "Inside the DBHandler, checking user", Toast.LENGTH_SHORT).show();

        if(firebaseAuth.getCurrentUser() != null)
        {
           currentUser = firebaseAuth.getCurrentUser();
            return true;
        }

        //Toast.makeText(activity, "Inside the DBHandler, user was null ", Toast.LENGTH_SHORT).show();

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
     * @param group
     */
    public void addCurrentUserToGroup(Group group)
    {
        // A firebase user object
        currentGroupID = group.getID();

        // Create User object
        User myUser = new User(currentUser.getDisplayName());

        //Firebase logic for creating group
        this.databaseReference.child("GroupsAndTheirMembers").child(group.getID()).child(currentUser.getUid()).setValue(myUser).addOnCompleteListener(activity, new OnCompleteListener<Void>()
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
    /**
     * Adding the currentuser to aGroup
     * @param groupCode
     */
    public void addCurrentUserToGroup(String groupCode)
    {
        // A firebase user object

        // Create User object
        User myUser = new User(currentUser.getDisplayName());
        currentGroupID = groupCode;

        //Firebase logic for creating group
        this.databaseReference.child("GroupsAndTheirMembers").child(groupCode).child(currentUser.getUid()).setValue(myUser).addOnCompleteListener(activity, new OnCompleteListener<Void>()
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

    // The purpose of this function is to perform the necessary logic for joining a group
    public void joinGroup(final String groupCode)
    {
        // This is a value listener used for reading values from the database.
        ValueEventListener groupListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) // No problem occurred
            {
                if(dataSnapshot.hasChild(groupCode)) // The provided code is valid
                {
                    for (DataSnapshot data : dataSnapshot.getChildren())
                    {
                        Group myGroup = data.getValue(Group.class);
                        if (myGroup.getID().equals(groupCode))
                        {
                            // Set group to user
                            addingGroupToCurrentUser(myGroup);

                            // Join the user to group
                            addCurrentUserToGroup(groupCode);
                            currentGroupID = groupCode;

                            // finish the current activity
                            activity.finish();

                            // Exit
                            return;
                        }
                    }

                }
                else
                {
                    Toast.makeText(activity, "Invalid Code: No group found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) // Problem occurred extracting values
            {
                Toast.makeText(activity, "Unable to add you to the group. Try later.", Toast.LENGTH_SHORT).show();
            }
        };


        databaseReference.child("Groups-Info").addListenerForSingleValueEvent(groupListener);

    }



    // The purpose of this function is to assign to a given owner the groups he belongs to
    public void addingGroupToCurrentUser(final Group createdGroup)
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
                currentGroupID = createdGroup.getID();
            }
        });

    }

    public void saveRestaurants(String name, double latitude, double longitude, Marker m) {

        String createdRestaurantID  = databaseReference.push().getKey();
        m.setTag(createdRestaurantID);

        Restaurant currentRestaurant = new Restaurant(createdRestaurantID, name, latitude, longitude);
        databaseReference.child("GroupsAndTheirRestaurants").child(currentGroupID).child(currentRestaurant.getId()).setValue(currentRestaurant).addOnCompleteListener(activity, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(activity, "Unable to save restaurant. Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void joinRestaurant(String restId, String prevSelection) {

        //adding default disp name because node value cant be null
        String dispName = firebaseAuth.getCurrentUser().getDisplayName()==null? "missing-disp-name": firebaseAuth.getCurrentUser().getDisplayName();

        String userId = firebaseAuth.getCurrentUser().getUid();
        this.prevSelection = prevSelection;
        //
        if(this.prevSelection==null){
            // Add the user to the restaurant
            databaseReference.child("RestaurantsAndTheirUsers").child(restId).child(userId).setValue(dispName);
            // Add the restaurant to the user
            databaseReference.child("UsersAndTheirRestaurants").child(currentGroupID).child(userId).setValue(restId);

            //Instantiate a listener incase user changes selection
            databaseReference.child("UsersAndTheirRestaurants").child(currentGroupID).child(getUser()).addListenerForSingleValueEvent(getSelectionListener());

        }else{
            if(!prevSelection.equalsIgnoreCase(restId)) {
                //remove old restaurant selection
                databaseReference.child("RestaurantsAndTheirUsers").child(prevSelection).child(userId).setValue(null);
                //Add user to a restaurant
                databaseReference.child("RestaurantsAndTheirUsers").child(restId).child(userId).setValue(dispName);
                // Add the restaurant to the user
                databaseReference.child("UsersAndTheirRestaurants").child(currentGroupID).child(userId).setValue(restId);

            }
        }
    }


    public ValueEventListener getSelectionListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    prevSelection =(String)dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }



}
