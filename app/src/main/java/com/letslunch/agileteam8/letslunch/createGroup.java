package com.letslunch.agileteam8.letslunch;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
            // Create a group
            this.createGroup();
        }

    }

    // This method implements the Firebase logic for creating a group
    private void createGroup()
    {
        // Getting information about a group
        String groupName    = this.editTextGroupName.getText().toString().trim();
        String location     = this.editTextMeetingLocation.getText().toString().trim();
        String time         = this.editTextLunchTime.getText().toString().trim();

        if(this.isAllInfoAvailable(groupName,location,time))
        {
            // Displaying message and showing the progress dialog
            myProgressDialog.setMessage("Creating group ...");
            myProgressDialog.show();

            // Firebase logic for creating group
//            this.databaseReference.child("Group").child(restaurantID).setValue(myRestaurant).addOnCompleteListener(this, new OnCompleteListener<Void>()
//            {
//                @Override
//                public void onComplete(@NonNull Task<Void> task)
//                {
//                    // Stop the progress dialog
//                    //progressDialog.dismiss();
//
//                    // Determine if task was completed successfully
//                    if(task.isSuccessful())
//                    {
//                        // Notifying the user that registration was successful
//                        Toast.makeText(addRestaurant.this, "Success!", Toast.LENGTH_SHORT).show();
//
//                        // Clearing textboxes
//                        addRestaurant.this.clearTextBoxes();
//                    }
//                    else
//                    {
//                        // Notifying the user that saving was NOT successful
//                        Toast.makeText(addRestaurant.this, "Unable to Save Restaurant. Try Again", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });


        }


    }

    // The purpose of this file is to verify that all information was provided by the user
    private Boolean isAllInfoAvailable(String groupName, String location, String time)
    {
        if (TextUtils.isEmpty(groupName))
        {
            return false;
        }
        else if (TextUtils.isEmpty(location))
        {
            return false;
        }
        else if(TextUtils.isEmpty(time))
        {
            return false;
        }

        return true;
    }




} // End of class
