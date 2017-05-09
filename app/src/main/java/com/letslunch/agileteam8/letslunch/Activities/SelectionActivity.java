package com.letslunch.agileteam8.letslunch.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.letslunch.agileteam8.letslunch.R;

public class SelectionActivity extends AppCompatActivity
{

    // Local Variables
    Button buylunch;
    Button lunchbox;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // Firebase Instance
        firebaseAuth = FirebaseAuth.getInstance();

        // Initializing Widgets
        buylunch = (Button) findViewById(R.id.buttonbl);
        buylunch.setOnClickListener(buylunchOnClickListener);

        lunchbox = (Button) findViewById(R.id.buttonlb);
        lunchbox.setOnClickListener(lunchboxOncLickListener);

    }


    private View.OnClickListener buylunchOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent mapActivity = new Intent(SelectionActivity.this, MapsActivity.class);
            startActivity(mapActivity);
        }
    };

    private View.OnClickListener lunchboxOncLickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent mapActivity = new Intent(SelectionActivity.this, MapsActivity.class);
            startActivity(mapActivity);
        }
    };
}
