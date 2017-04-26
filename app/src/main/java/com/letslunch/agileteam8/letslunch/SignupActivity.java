package com.letslunch.agileteam8.letslunch;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.text.TextUtils;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener
{
    // Local Variables
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText edittextPassword;
    private Button buttonCreateAccount;
    private TextView textViewLoginLink;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initializing the FirebaseAuth Object
        firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initializing the widgets
        editTextName        = (EditText) findViewById(R.id.input_name);
        editTextEmail       = (EditText) findViewById(R.id.input_email);
        edittextPassword    = (EditText) findViewById(R.id.input_password);
        buttonCreateAccount = (Button)   findViewById(R.id.btn_signup);
        textViewLoginLink   = (TextView) findViewById(R.id.link_login);
        progressDialog      =  new ProgressDialog(this);

        // Setting listeners
        buttonCreateAccount.setOnClickListener(this);
        textViewLoginLink.setOnClickListener(this);

    }

    // Action to perform when clicking the "Create Account" Button or the "link to Login" TextView
    @Override
    public void onClick(View v)
    {
        if (v == buttonCreateAccount)
        {
            // Create the Account
            this.createAccount();

        }
        else if (v == textViewLoginLink)
        {
            // Close current Activity
            finish();

            // Jump to Main Activity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    // The purpose of this function is to perform the logic of creating an account
    private void createAccount()
    {
        // Getting user input
        String name     = this.editTextName.getText().toString().trim();
        String email    = this.editTextEmail.getText().toString().trim();
        String password = this.edittextPassword.getText().toString().trim();

        // Validating that all information was provided
        if (this.isAllInfoProvided(name,email,password))
        {
            // Displaying message and showing the progress dialog
            progressDialog.setMessage("Creating Account ...");
            progressDialog.show();

            // Firebase code for creating user
            firebaseAuth.createUserWithEmailAndPassword(email,password)
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
                                // Set user display name in firebase
                                setUserDisplayName(editTextName.getText().toString().trim());

                                // Finish current activity
                                finish();

                                // Jump to Selection activity
                                startActivity(new Intent(getApplicationContext(), homePage.class));
                            }
                            else
                            {
                                // Notifying the user that registration was NOT successful
                                Toast.makeText(SignupActivity.this, "Unable to Create Account. Try Again", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    // Validating that information was provided to all input fields
    private boolean isAllInfoProvided(String name, String email, String password)
    {
        if (TextUtils.isEmpty(name))
        {
            // Notify user the "Name" field is empty
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();

            return false;
        }
        else if (TextUtils.isEmpty(email))
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

    // The purpose of this function is to set the display name of the user
    private void setUserDisplayName(String name)
    {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
    }

} // End of Signup Activity