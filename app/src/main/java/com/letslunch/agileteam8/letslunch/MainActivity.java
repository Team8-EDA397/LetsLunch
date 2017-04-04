package com.letslunch.agileteam8.letslunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    EditText user;
    EditText pass;
    Button login;
    public static HashSet<String[]> users = new HashSet<>();
    TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        user = (EditText) findViewById(R.id.user);
        pass = (EditText) findViewById(R.id.pass);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(loginOncLickListener);

        _signupLink = (TextView) findViewById(R.id.link_signup);
        _signupLink.setOnClickListener(signUpOncLickListener);

    }

    private View.OnClickListener loginOncLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login:
                    String userS = user.getText().toString();
                    String passS = pass.getText().toString();
                    String credentials = new String(userS+" "+passS);

                    // UNCOMMENT THIS WHEN DEMONSTRATION
                    FileInputStream fIn = null;
                    try {
                        fIn = openFileInput("credentials.txt");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    InputStreamReader isr = new InputStreamReader(fIn);

                    // Prepare a char-Array that will hold the chars we read back in.
                    char[] inputBuffer = new char[SignupActivity.name.length()+SignupActivity.password.length()+1];

                    // Fill the Buffer with data from the file
                    try {
                        isr.read(inputBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Transform the chars to a String
                    String readString = new String(inputBuffer);


                    // Check if we read back the same chars that we had written out
                    if (credentials.equals(readString))  {
                        Intent selectionActivity = new Intent(MainActivity.this, SelectionActivity.class);
                        startActivity(selectionActivity);
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.loginWRONG, Toast.LENGTH_SHORT).show();

                        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(400);
                    }


                    // Manual test fast
                    /*
                    Intent selectionActivity = new Intent(MainActivity.this, SelectionActivity.class);
                    startActivity(selectionActivity);
                    return;
                    */
            }
        }
    };

    private View.OnClickListener signUpOncLickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signUpActivity = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signUpActivity);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
