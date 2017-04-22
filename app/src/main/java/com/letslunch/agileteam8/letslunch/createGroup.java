package com.letslunch.agileteam8.letslunch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class createGroup extends AppCompatActivity implements View.OnClickListener
{
    // Widget variables
    private EditText editTextGroupName;
    private EditText editTextMeetingLocation;
    private EditText editTextLunchTime;
    private Button buttonCreateGroup;
    private ProgressDialog myProgressDialog;

    // Firebase variables
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

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
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();

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
                String createdGroupID  = this.databaseReference.push().getKey();

                // creating a group object
                Group createdGroup = new Group(groupName,location,time,createdGroupID);

                // Create a node containing all information of the group
                this.createGroup(createdGroup);

                // Assign the creator of the group to the newly created group
                this.addingGroupCreatorToHisGroup(createdGroup);

                // Adding the group to his creator
                this.addingGroupToOwner(createdGroup);
            }
        }
    }

    // This method implements the Firebase logic for creating a group
    private void createGroup(Group myGroup)
    {
        // Displaying message and showing the progress dialog
        myProgressDialog.setMessage("Creating group ...");
        myProgressDialog.show();

        //Firebase logic for creating group
        this.databaseReference.child("Groups-Info").child(myGroup.getID()).setValue(myGroup).addOnCompleteListener(this, new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    // Stop the progress dialog
                    myProgressDialog.dismiss();

                    // Determine if task was completed successfully
                    if(task.isSuccessful())
                    {
                        // Move back to home screen
                        finish();

                    }
                    else
                    {
                        // Notifying the user that saving was NOT successful
                        Toast.makeText(createGroup.this, "Unable to create group. Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }

    // Adding the creator of the group to the group he created.
    private void addingGroupCreatorToHisGroup(Group aGroup)
    {
        // A firebase user object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Create aUser object
        aUser myUser = new aUser(user.getDisplayName());

        //Firebase logic for creating group
        this.databaseReference.child("GroupsAndTheirMembers").child(aGroup.getID()).child(user.getUid()).setValue(myUser).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(createGroup.this, "Unable to add you to the created group. Try Manually.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    // The purpose of this function is to assign to a given owner the groups he belongs to
    private void addingGroupToOwner(Group createdGroup)
    {
        //Firebase logic for creating group
        this.databaseReference.child("UserAndTheirGroups").child(firebaseAuth.getCurrentUser().getUid()).child(createdGroup.getID()).setValue(createdGroup).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(createGroup.this, "Unable to add assign a group to you.", Toast.LENGTH_SHORT).show();
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
