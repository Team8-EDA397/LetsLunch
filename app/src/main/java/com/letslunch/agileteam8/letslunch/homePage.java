package com.letslunch.agileteam8.letslunch;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.letslunch.agileteam8.letslunch.R.id.icon_group;
import static com.letslunch.agileteam8.letslunch.R.id.parent;

public class homePage extends AppCompatActivity implements View.OnClickListener
{
    // Creating an Enumeration for the possible eating status of a user.
    private enum eatingStatus
    {
        // Enumeration values
        NOT_ATTENDING, EATING_AT_RESTAURANT, BRING_LUNCH;
    };


    // Widgets variables
    private Button buttonLogOut;
    private Button buttonCreateGroup;
    private Button buttonJoinGroup;

    // Firebase variables
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseGroups;

    ListView listViewGroups;
    List<Group> groupList;
    List<aUser> usersList; // List of groups members for a given group
    String currentUser;

    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Instantiation firebase variable
        firebaseAuth    = FirebaseAuth.getInstance();

        // Instantiating widgets
        buttonLogOut        = (Button) findViewById(R.id.logoutButton);
        buttonCreateGroup   = (Button) findViewById(R.id.buttonCreateGroup);
        buttonJoinGroup     = (Button) findViewById(R.id.buttonJoinGroup);
        listViewGroups = (ListView) findViewById(R.id.listViewGroups);

        // Setting listeners
        buttonLogOut.setOnClickListener(this);
        buttonCreateGroup.setOnClickListener(this);
        buttonJoinGroup.setOnClickListener(this);

        // Checking if the user is sign-in or not
        if (!this.isUserAlreadySignedIn(firebaseAuth))
        {
            // Finish current activity
            finish();

            // Jump to the login activity
            startActivity(new Intent(this, MainActivity.class));
        }

        // Getting the reference of groups node
        databaseGroups = FirebaseDatabase.getInstance().getReference();

        // Getting information about the current user
        currentUser = firebaseAuth.getCurrentUser().getUid();

        // Instantiating variables
        groupList = new ArrayList<>();
        usersList = new ArrayList<>();

        // Loading the groups the user belongs to
        loadGroup();

        // Clicking a row from the table view
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // getting th group ID
                groupID = groupList.get(position).getID();
                Log.d("GROUP ID SELECTED", groupID);
                // Get all users
                loadUsers(position);
            }
        });
    }

/*
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(homePage.this);
                alert.setTitle("Lets Lunch ");
                alert.setIcon(R.drawable.splash_img);
                groupID = groupList.get(position).getID();
                alert.setMessage("Group: "+groupList.get(position).getName()+
                        "\n"+"Id: "+groupList.get(position).getID()+
                        "\n"+"Location: "+groupList.get(position).getLocation()+
                        "\n"+"Time: "+groupList.get(position).getTime()
                );

                alert.setNegativeButton(R.string.lunch_box,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eatingStatus userStatus = eatingStatus.BRING_LUNCH;
                                userResponseToEating(userStatus.toString());
                                dialog.dismiss();
                            }
                        });
                alert.setPositiveButton(R.string.restaurant,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                eatingStatus userStatus = eatingStatus.EATING_AT_RESTAURANT;
                                userResponseToEating(userStatus.toString());
                                dialog.dismiss();
                            }
                        });
                alert.setNeutralButton(R.string.neutral,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                eatingStatus userStatus = eatingStatus.NOT_ATTENDING;
                                userResponseToEating(userStatus.toString());
                                dialog.dismiss();

                            }
                        });

                alert.show();

            }});

    }
    */

    // Performing appropriate actions depending on what button is clicked
    @Override
    public void onClick(View v)
    {
        if (v == this.buttonLogOut)
        {
            // Sign the user out
            this.signUserOut();
        }
        else if (v == this.buttonCreateGroup)
        {
            // Move to the createGroup activity
            startActivity(new Intent(this, createGroup.class));

        }
        else if (v == this.buttonJoinGroup)
        {
            // Move to the joinGroup activity
            startActivity(new Intent(this, joinGroup.class));
        }
    }


    // The purpose of this function is to determine if a user is already logged in to the app.
    private boolean isUserAlreadySignedIn(FirebaseAuth firebaseObject)
    {
        if(firebaseObject.getCurrentUser() != null )
        {
            return true;
        }

        return false;
    }

    // The purpose of this function is to contain the logic necessary for signing out the user
    private void signUserOut()
    {
        // Logout from Firebase
        firebaseAuth.signOut();

        // Finishing the current activity
        finish();

        // Jumping to the login activity
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    // The purpose of this class is to update the eating status of the user for a given group
    private void userResponseToEating(String userStatus)
    {
        // Get a firebase user object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Get the display name of the user
        String userDisplayName  = user.getDisplayName();

        // Create an aUser object
        aUser updatedUser = new aUser(userDisplayName, userStatus);

        // Update the current user eating status by storing the aUser object
        databaseGroups.child("GroupsAndTheirMembers").child(this.groupID).child(user.getUid()).setValue(updatedUser).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(homePage.this, "Your status has been updated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(homePage.this, "Unable to let your friends know.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadGroup(){
        databaseGroups.child("UserAndTheirGroups").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){

                    Group group = groupsSnapshot.getValue(Group.class);
                    groupList.add(group);
                }
                GroupList adapter = new GroupList(homePage.this,groupList);
                listViewGroups.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void loadUsers(final int position)
    {
        Log.d("ID to be used = ", this.groupID);
        databaseGroups.child("GroupsAndTheirMembers").child(this.groupID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Click", "Entre");
                // Clearing the list
                usersList.clear();
                // Obtaining users and placing them on the list of users
                for(DataSnapshot usersSnapshot : dataSnapshot.getChildren())
                {
                    aUser user = usersSnapshot.getValue(aUser.class);
                    usersList.add(user);
                }

                // Print users
                createAndShowAlert(position);

            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
    private String getAllUsers()
    {
        String users = "";
        for(int i = 0 ; i< usersList.size(); i++)
        {
            users = users + usersList.get(i).getName() + " \n";
        }
        return users;
    }
    private void createAndShowAlert(int position)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(homePage.this);
        alert.setTitle("Lets Lunch ");
        alert.setIcon(R.drawable.splash_img);
        alert.setMessage("Group: "+groupList.get(position).getName()+
                "\n"+"Id: "+groupList.get(position).getID()+
                "\n"+"Location: "+groupList.get(position).getLocation()+
                "\n"+"Time: "+groupList.get(position).getTime()+"\n"+
                "\n"+"Users \n"+ getAllUsers()
        );

        alert.setNegativeButton(R.string.lunch_box,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.BRING_LUNCH;
                        dialog.dismiss();
                        userResponseToEating(userStatus.toString());
                    }
                });
        alert.setPositiveButton(R.string.restaurant,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.EATING_AT_RESTAURANT;
                        userResponseToEating(userStatus.toString());
                        dialog.cancel();
                    }
                });
        alert.setNeutralButton(R.string.neutral,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.NOT_ATTENDING;
                        userResponseToEating(userStatus.toString());
                        dialog.cancel();
                    }
                });
        alert.show();
    } // End of createAndShowAlert()

} // End of class
