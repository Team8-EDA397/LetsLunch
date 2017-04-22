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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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

import static com.letslunch.agileteam8.letslunch.R.id.icon_group;
import static com.letslunch.agileteam8.letslunch.R.id.parent;

public class homePage extends AppCompatActivity implements View.OnClickListener
{
    // Widgets variables
    private Button buttonLogOut;

    // Local variables
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

        // Instantiation local variables
        firebaseAuth    = FirebaseAuth.getInstance();

        // Instantiating widgets
        buttonLogOut    = (Button) findViewById(R.id.logoutButton);

        // Setting listeners
        buttonLogOut.setOnClickListener(this);

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

        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                AlertDialog.Builder alert = new AlertDialog.Builder(homePage.this);
                alert.setTitle("Lets Lunch ");
                alert.setIcon(R.drawable.splash_img);
                alert.setMessage("Group: "+groupList.get(position).getName()+
                        "\n"+"Id: "+groupList.get(position).getID()+
                        "\n"+"Location: "+groupList.get(position).getLocation()+
                        "\n"+"Time: "+groupList.get(position).getTime()
                );

                alert.setNegativeButton(R.string.lunch_box,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.setPositiveButton(R.string.restaurant,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alert.setNeutralButton(R.string.neutral,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // Jump to homePage Activity
                                //startActivity(new Intent(getApplicationContext(), Splashscreen.class));
                            }
                        });

                alert.show();

            }});

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
        if (v == buttonLogOut)
        {
            // Sign the user out
            this.signUserOut();
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
