package com.letslunch.agileteam8.letslunch.Activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.letslunch.agileteam8.letslunch.Group;
import com.letslunch.agileteam8.letslunch.GroupList;
import com.letslunch.agileteam8.letslunch.R;
import com.letslunch.agileteam8.letslunch.User;
import com.letslunch.agileteam8.letslunch.Utils.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener
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
    DBHandler database;
    ListView listViewGroups;
    List<Group> groupList;
    List<User> usersList; // List of groups members for a given group
    String currentUser;

    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Instantiation firebase variable
        database = DBHandler.getInstance();
        database.setActivity(this);
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
        if (!database.isUserAlreadySignedIn())
        {
            // Finish current activity
            finish();

            // Jump to the login activity
            startActivity(new Intent(this, MainActivity.class));
        }


        // Getting information about the current user
        currentUser = database.getUser();

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
                database.setCurrentGroupID(groupID);
                Log.d("GROUP ID SELECTED", groupID);
                // Get all users
                loadUsers(position);
            }
        });
    }

    private void startMap() {
        Intent intent = new Intent(HomePageActivity.this, MapsActivity.class);
        intent.putExtra("GROUP_ID", this.groupID);
        startActivity(intent);
    }

//     @Override
//     protected void onStart() {
//         super.onStart();

//         databaseGroups.child("UserAndTheirGroups").child(currentUser).addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange (DataSnapshot dataSnapshot) {
//                 groupList.clear();
//                 for(DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){

//                     // if (groupsSnapshot.getValue().equals(firebaseAuth.getCurrentUser().getUid())){
//                     //    Group group = new Group(firebaseAuth.getCurrentUser().getUid(),"","","");
//                     //    groupList.add(group);

//                      Group group = groupsSnapshot.getValue(Group.class);
//                      groupList.add(group);
//                 }

//                 GroupList adapter = new GroupList(HomePageActivity.this,groupList);
//                 listViewGroups.setAdapter(adapter);
//             }

//             @Override
//             public void onCancelled(DatabaseError databaseError) {

//             }
//         });
//     }




    // Performing appropriate actions depending on what button is clicked
    @Override
    public void onClick(View v)
    {
        if (v == this.buttonLogOut)
        {
            // Sign the user out
            database.signUserOut();
        }
        else if (v == this.buttonCreateGroup)
        {
            // Move to the CreateGroupActivity activity
            startActivity(new Intent(this, CreateGroupActivity.class));

        }
        else if (v == this.buttonJoinGroup)
        {
            // Move to the JoinGroupActivity activity
            startActivity(new Intent(this, JoinGroupActivity.class));
        }
    }
/*
    // The purpose of this class is to update the eating status of the user for a given group
    private void userResponseToEating(String userStatus)
    {
        // Get a firebase user object
        FirebaseUser user = database.getUser();

        // Get the display name of the user
        String userDisplayName  = user.getDisplayName();

        // Create an User object
        User updatedUser = new User(userDisplayName, userStatus);

        // Update the current user eating status by storing the User object
        database.databaseReference.child("GroupsAndTheirMembers").child(this.groupID).child(user.getUid()).setValue(updatedUser).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(HomePageActivity.this, "Your status has been updated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(HomePageActivity.this, "Unable to let your friends know.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
*/
    public void loadGroup(){
        database.databaseReference.child("UserAndTheirGroups").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){

                    Group group = groupsSnapshot.getValue(Group.class);
                    groupList.add(group);
                }
                GroupList adapter = new GroupList(HomePageActivity.this, groupList);
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
        database.databaseReference.child("GroupsAndTheirMembers").child(this.groupID).addListenerForSingleValueEvent(new ValueEventListener()
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
                    User user = usersSnapshot.getValue(User.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(HomePageActivity.this);
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
                        database.userResponseToEating(userStatus.toString());
                    }
                });
        alert.setPositiveButton(R.string.restaurant,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.EATING_AT_RESTAURANT;
                        database.userResponseToEating(userStatus.toString());
                        dialog.cancel();
                        startMap();
                    }
                });
        alert.setNeutralButton(R.string.neutral,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.NOT_ATTENDING;
                        database.userResponseToEating(userStatus.toString());
                        dialog.cancel();
                    }
                });
        alert.show();
    } // End of createAndShowAlert()

} // End of class
