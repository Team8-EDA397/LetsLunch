package com.letslunch.agileteam8.letslunch.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.letslunch.agileteam8.letslunch.Group;
import com.letslunch.agileteam8.letslunch.R;
import com.letslunch.agileteam8.letslunch.User;
import com.letslunch.agileteam8.letslunch.Utils.DBHandler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener
{
    // Widget variables
    private EditText editTextGroupName;
    private EditText editTextMeetingLocation;
    private EditText editTextLunchTime;
    private Button buttonCreateGroup;
    private ProgressDialog myProgressDialog;

    // Firebase variables
    private DBHandler database;

    private Group createdGroup;
    private static final String GROUP_NODE = "Groups-Info";
    private static final String GROUP_USERS_NODE = "GroupsAndTheirMembers";
    private static final String USERS_GROUPS_NODE = "UserAndTheirGroups";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Instantiating widgets
        editTextGroupName       = (EditText) findViewById(R.id.editTextGroupName);
        editTextMeetingLocation = (EditText) findViewById(R.id.editTextSetLocation);
        editTextLunchTime       = (EditText) findViewById(R.id.editTextSetTime);
        buttonCreateGroup       = (Button) findViewById(R.id.buttonGroupCreation);
        myProgressDialog        = new ProgressDialog(this);

        // Initializing FirebaseAuth object
        database = DBHandler.getInstance();
        database.setActivity(this);

        // Setting up listeners
        buttonCreateGroup.setOnClickListener(this);

    }

    // Actions to perform when the appropriate button is clicked
    @Override
    public void onClick(View v)
    {
        if (v == this.buttonCreateGroup)
        {
            // Getting information about a group
            String groupName    = this.editTextGroupName.getText().toString().trim();
            String location     = this.editTextMeetingLocation.getText().toString().trim();
            String time         = this.editTextLunchTime.getText().toString().trim();


            if (this.isAllInfoAvailable(groupName,location,time))
            {
                // Created a group ID
                String createdGroupID  = database.databaseReference.push().getKey();

                // creating a group object
                Group createdGroup = new Group(groupName, location, time, createdGroupID);
                 String createdGroupID  = generateId(groupName);

                //uncomment below for testing same id
                //String createdGroupID = "testfail-1895";

                // creating a group object
                createdGroup = new Group(groupName,location,time,createdGroupID);

                // try to create group if id is unique
                tryCreateGroup(createdGroup);

            }
        }
    }

    private String generateId(String groupName){
        int MAX_GROUP_CODE = 9999;
        int MIN_GROUP_CODE = 1000;
        int MAX_GROUP_NAME_SIZE = 10;
        String newGroupName="";

        //peform null check remove aschii control characters
        if(groupName!=null){
            newGroupName= groupName.replaceAll("[\u0000-\u001f]", "");
        }


                // Assign the creator of the group to the newly created group
                database.addingCurrentUserToGroup(createdGroup);

                // Adding the group to his creator
                database.addingGroupToCurrentUser(createdGroup);

        Set<Character> illegalChar = new HashSet<Character>(Arrays.asList('.', '$', '#', '[',']', '/',' '));

        //String Buffer for the result
        StringBuffer result = new StringBuffer();
        for(char c : newGroupName.toCharArray()){
            if (result.length()<= MAX_GROUP_NAME_SIZE && !illegalChar.contains(c)){
                result.append(c);

            }
        }

        //merge checked string with random group code
        return result.toString()+"-"+String.valueOf(ThreadLocalRandom.current().nextInt(MIN_GROUP_CODE, MAX_GROUP_CODE + 1));
    }


    // Checks if /Group-Info/<groupId> has any data if not it creates the group
    private void tryCreateGroup(final Group myGroup){
        myProgressDialog.setMessage("Creating group  with id   "+myGroup.getID() );
        myProgressDialog.show();
        DatabaseReference newGroupRef = this.databaseReference.child(GROUP_NODE).child(myGroup.getID()).getRef();
        newGroupRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(myGroup);
                    return Transaction.success(currentData);
                } else {
                    return Transaction.abort();
                }
            }

        //Firebase logic for creating group
        database.databaseReference.child("Groups-Info").child(myGroup.getID()).setValue(myGroup).addOnCompleteListener(this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    // Stop the progress dialog
                    //myProgressDialog.dismiss();

                    // Determine if task was completed successfully
                    if(task.isSuccessful())
                    {
                        // Move back to home screen
                        finish();

                    }
                    else
                    {
                        // Notifying the user that saving was NOT successful
                        Toast.makeText(CreateGroupActivity.this, "Unable to create group. Try Again", Toast.LENGTH_SHORT).show();
                    }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError!=null) {
                    Log.e("CreateGroup_DB_ERROR", databaseError.getMessage());
                }
                //if transaction complemented successfully b will be true
                if (b){
                    myProgressDialog.dismiss();
                    Toast.makeText(createGroup.this, "New group was created, now adding you to group...", Toast.LENGTH_LONG).show();

                    //join creator to group
                    addingGroupCreatorToHisGroup(createdGroup);
                }else{
                    myProgressDialog.dismiss();
                    Toast.makeText(createGroup.this, "Failed to create group. Please try changing the group name", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


        // The purpose of this file is to verify that all information was provided by the user
    private Boolean isAllInfoAvailable(String groupName, String location, String time)
    {
        if (TextUtils.isEmpty(groupName))
        {
            Toast.makeText(this, "Please enter a group Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(location))
        {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(time))
        {
            Toast.makeText(this, "Please enter a time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

} // End of class
