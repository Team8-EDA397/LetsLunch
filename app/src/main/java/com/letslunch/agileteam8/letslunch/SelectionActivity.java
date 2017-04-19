package com.letslunch.agileteam8.letslunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SelectionActivity extends AppCompatActivity {

    // Local Variables
    Button buylunch;
    Button lunchbox;
    Button buttonLogOut;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // Firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initializing Widgets
        buylunch = (Button) findViewById(R.id.buttonbl);
        buylunch.setOnClickListener(buylunchOnClickListener);

        lunchbox = (Button) findViewById(R.id.buttonlb);
        lunchbox.setOnClickListener(lunchboxOncLickListener);

        buttonLogOut = (Button) findViewById(R.id.buttonLogout);
        buttonLogOut.setOnClickListener(logOutButtonListener);

        // Checking if the user is signing or not
        if (!this.isUserAlreadySignedIn(firebaseAuth))
        {
            // Finish current activity
            finish();

            // Jump to the login activity
            startActivity(new Intent(this, MainActivity.class));
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

    private View.OnClickListener logOutButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            // Logout from Firebase
            firebaseAuth.signOut();

            // Finishing the current activity
            finish();

            // Jumping to the login activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    };

    private View.OnClickListener buylunchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mapActivity = new Intent(SelectionActivity.this, MapsActivity.class);
            startActivity(mapActivity);
        }
    };

    private View.OnClickListener lunchboxOncLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent mapActivity = new Intent(SelectionActivity.this, MapsActivity.class);
            startActivity(mapActivity);
        }
    };
}
