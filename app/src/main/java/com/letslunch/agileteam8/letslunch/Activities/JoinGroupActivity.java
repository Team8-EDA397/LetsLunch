package com.letslunch.agileteam8.letslunch.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.letslunch.agileteam8.letslunch.R;
import com.letslunch.agileteam8.letslunch.Utils.DBHandler;


public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener
{
    // Global variables
    private String groupCode = null;

    // Local widegts
    private Button buttonJoinGroup;
    private EditText editTextGroupCode;

    // Firebase objects
    private DBHandler database;
    private DatabaseReference databaseReference;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        // Instantiating widget
        buttonJoinGroup = (Button) findViewById(R.id.buttonJoiningGroup);
        editTextGroupCode = (EditText) findViewById(R.id.editTextGroupCode);

        // Instantiating Firebase objects
        database = DBHandler.getInstance();
        database.setActivity(this);
        this.databaseReference = database.databaseReference;

        // Setting up listeners
        buttonJoinGroup.setOnClickListener(this);
    }

    // Taking appropriate action when the appropriate button is clicked.
    @Override public void onClick(View v) {
        if(v == buttonJoinGroup) {
            // Getting information provided by the user
            groupCode = editTextGroupCode.getText().toString().trim();
            // Checking all informaiton was provided
            if(this.isAllInformationProvided(this.groupCode)) {
                // Performing actions for joining a group.
                //database.addCurrentUserToGroup();
                database.joinGroup(groupCode);
            }
        }
    }

    // The purpose of this functino is to ensure that al information has been provided
    private Boolean isAllInformationProvided(String groupCode) {
        if(TextUtils.isEmpty(groupCode)) {
            // Let user know
            Toast.makeText(this, "Please enter a group code", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


} // End of class
