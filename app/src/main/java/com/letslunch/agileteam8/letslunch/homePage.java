package com.letslunch.agileteam8.letslunch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

public class homePage extends AppCompatActivity implements View.OnClickListener
{
    // Widgets variables
    private Button buttonLogOut;
    private Button buttonCreateGroup;
    private Button buttonJoinGroup;

    // Firebase variables
    FirebaseAuth firebaseAuth;

    DatabaseReference databaseGroups;

    ListView listViewGroups;

    List<Group> groupList;

    String currentUser;

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

        // Getting the reference of artists node
        databaseGroups = FirebaseDatabase.getInstance().getReference();

        // Getting information about the current user
        currentUser = firebaseAuth.getCurrentUser().getUid();

        listViewGroups = (ListView) findViewById(R.id.listViewGroups);

        groupList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseGroups.child("UserAndTheirGroups").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){

                    // if (groupsSnapshot.getValue().equals(firebaseAuth.getCurrentUser().getUid())){
                    //    Group group = new Group(firebaseAuth.getCurrentUser().getUid(),"","","");
                    //    groupList.add(group);

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

} // End of class
