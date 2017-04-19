package com.letslunch.agileteam8.letslunch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    // Local Variables
    private EditText editTextemail;
    private EditText editTextpassword;
    private Button buttonLogin;
    private TextView textViewcreateAccountLink;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Initializing FirebaseAuth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Initializing Widgets
        editTextemail               = (EditText) findViewById(R.id.user);
        editTextpassword            = (EditText) findViewById(R.id.pass);
        buttonLogin                 = (Button) findViewById(R.id.login);
        textViewcreateAccountLink   = (TextView) findViewById(R.id.link_signup);
        progressDialog              = new ProgressDialog(this);

        // Setting listeners
        buttonLogin.setOnClickListener(this);
        textViewcreateAccountLink.setOnClickListener(this);

        // Sending notification
        sendNotification();

        // Check if a user is already logged in
        if (this.isUserAlreadySignedIn(firebaseAuth))
        {
            // Exit the current activity
            finish();

            // Jump to Selection Activity
            startActivity(new Intent(this, SelectionActivity.class));
        }
    }

    // Performing appropriate actions dependending if "buttonLogin" or "textViewcreateAccountLink" is clicked
    @Override
    public void onClick(View v)
    {
        if (v == buttonLogin)
        {
            // Sign the user
            this.signUser();
        }
        else if (v == textViewcreateAccountLink)
        {
            // Exit the current activity
            finish();

            // Jump to Create Account Activity
            startActivity(new Intent(this, SignupActivity.class));
        }

    }

    // The purpose of this function is to provide the logic for signing a user.
    private void signUser()
    {
        // Getting user credentials
        String email    = this.editTextemail.getText().toString().trim();
        String password = this.editTextpassword.getText().toString().trim();

        // Verifying that all email and password were provided
        if (this.isAllInfoProvided(email,password))
        {
            // Displaying message and showing the progress dialog
            progressDialog.setMessage("Signing User ...");
            progressDialog.show();

            // Firebase logic for Signing in user
            this.firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            // Stop the progress dialog
                            progressDialog.dismiss();

                            // Determine if task was completed successfully
                            if(task.isSuccessful())
                            {
                                finish();

                                // Jump to Selection Activity
                                startActivity(new Intent(getApplicationContext(), SelectionActivity.class));
                            }
                            else
                            {
                                // Notifying the user that registration was NOT successful
                                Toast.makeText(MainActivity.this, "Unable to Sign In. Try Again.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
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

    // Validating that information was provided to all input fields
    private boolean isAllInfoProvided(String email, String password)
    {

        if (TextUtils.isEmpty(email))
        {
            // Notify user the "Email" field is empty
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();

            return false;
        }
        else if (TextUtils.isEmpty(password))
        {
            // Notify user the "Password" field is empty
            Toast.makeText(this, "Please enter a Password", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendNotification()
    {
        Intent intent = new Intent(this, ReminderReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Set the alarm to start at approximately 7:30 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 29);

        //set it to repeat every day (not tested)
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() , AlarmManager.INTERVAL_DAY, pendingIntent);


    }
}
