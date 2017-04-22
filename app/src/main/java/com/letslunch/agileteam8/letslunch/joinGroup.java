package com.letslunch.agileteam8.letslunch;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class joinGroup extends AppCompatActivity implements View.OnClickListener
{
    // Global variables
    private String groupCode = null;

    // Local widegts
    private Button buttonJoinGroup;
    private EditText editTextGroupCode;

    // Firebase objects
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        // Instantiating widget
        buttonJoinGroup     = (Button)findViewById(R.id.buttonJoiningGroup);
        editTextGroupCode   = (EditText)findViewById(R.id.editTextGroupCode);

        // Instantiating Firebase objects
        this.firebaseAuth       = FirebaseAuth.getInstance();
        this.databaseReference  = FirebaseDatabase.getInstance().getReference();

        // Setting up listeners
        buttonJoinGroup.setOnClickListener(this);
    }

    // Taking appropriate action when the appropriate button is clicked.
    @Override
    public void onClick(View v)
    {
        if (v == buttonJoinGroup)
        {
            // Getting information provided by the user
            this.groupCode = this.editTextGroupCode.getText().toString().trim();

            // Checking all informaiton was provided
            if (this.isAllInformationProvided(this.groupCode))
            {
                // Performing actions for joining a group.
                this.joinAGroup();

            }
        }
    }

    // The purpose of this function is to add the user to the given group
    private void userToGroupAddition()
    {
        // A firebase user object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Create aUser object
        aUser myUser = new aUser(user.getDisplayName());

        //Firebase logic for creating group
        this.databaseReference.child("GroupsAndTheirMembers").child(groupCode).child(user.getUid()).setValue(myUser).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(joinGroup.this, "Unable to add you to the created group. Try Manually.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    // The purpose of this function is to add the group to a given user
    private void groupToUserAddition(Group groupToJoin)
    {
        //Firebase logic for creating group
        this.databaseReference.child("UserAndTheirGroups").child(firebaseAuth.getCurrentUser().getUid()).child(groupToJoin.getID()).setValue(groupToJoin).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(joinGroup.this, "Unable to add assign a group to you.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // The purpose of this function is to perform the necessary logic for joining a group
    private void joinAGroup()
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
                            groupToUserAddition(myGroup);

                            // Join the user to group
                            userToGroupAddition();

                            // finish the current activity
                            finish();

                            // Exit
                            return;
                        }
                    }

                }
                else
                {
                    Toast.makeText(joinGroup.this, "Invalid Code: No group found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) // Problem occurred extracting values
            {
                Toast.makeText(joinGroup.this, "Unable to add you to the group. Try later.", Toast.LENGTH_SHORT).show();
            }
        };


        databaseReference.child("Groups-Info").addListenerForSingleValueEvent(groupListener);

    }

    // The purpose of this functino is to ensure that al information has been provided
    private Boolean isAllInformationProvided(String groupCode)
    {
        if (TextUtils.isEmpty(groupCode))
        {
            // Let user know
            Toast.makeText(this, "Please enter a group code", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }




} // End of class
