package com.letslunch.agileteam8.letslunch;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


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

    private Group createdGroup;


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

                String createdGroupID  = "heita-1895";//groupName.trim()+"-"+String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999 + 1));
                        //"heita-1895";
                        //groupName.trim()+"-"+String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999 + 1));
                // this.databaseReference.child("Groups-Info").push().getKey();

                // creating a group object
                createdGroup = new Group(groupName,location,time,createdGroupID);

                // Create a node containing all information of the group
                this.createGroup(createdGroup);

                // Assign the creator of the group to the newly created group

                // Adding the group to his creator

            }
        }
    }

    private void createGroup(final Group myGroup, final Boolean checked){
        final DatabaseReference newGroupRef = this.databaseReference.child("Groups-Info").child(myGroup.getID()).getRef();
        newGroupRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                System.out.println("current data in do "+ currentData);
                if (checked == false) {
                    System.out.println("############# group is unique safe to update " + newGroupRef +" "+currentData);
                    newGroupRef.setValue(myGroup);
                    return Transaction.success(currentData);
                } else {
                    System.out.println("############# group is not unique NOT safe to update " +  newGroupRef +" "+currentData);
                    System.out.println("############# in failed " + newGroupRef);
                    return Transaction.abort();

                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError!=null) {
                    Log.e("CreateGroup_DB_ERROR", databaseError.getMessage());
                }
                //if transaction complemented successfully b will be true
                if (b){
                    System.out.println("############# group was created not adding creator to group");
                    addingGroupCreatorToHisGroup(createdGroup);
                }else{
                    System.out.println("should tell user to try again");

                }

            }
        });

    }

    // This method implements the Firebase logic for creating a group
    private void createGroup(final Group myGroup)
    {
        // Displaying message and showing the progress dialog
        myProgressDialog.setMessage("Creating group  with id   "+myGroup.getID() );
        myProgressDialog.show();
        checkIfGroupExists(myGroup);


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
                //final Boolean result ;
                if(!task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful

                    Toast.makeText(createGroup.this, "Unable to add you to the created group. Try to join .", Toast.LENGTH_SHORT).show();

                }else{
                    addingGroupToOwner(createdGroup);
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
                }else{
                    finish();
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


    // Tests to see if /Group-Info/<groupId> has any data.
    private void checkIfGroupExists(final Group group) {
        DatabaseReference groupRef = databaseReference.child("Groups-Info").child(group.getID()).getRef();
                groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue()!=null) {
                    //user exists, do something
                    System.out.println("############# checkexists was true " + snapshot);
                    createGroup(group, true);
                } else {
                    //user does not exist, do something else
                    System.out.println("############# checkexists was false " + snapshot);
                    createGroup(group, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



} // End of class
