package com.letslunch.agileteam8.letslunch;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
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
    // Creating an Enumeration for the possible eating status of a user.
    private enum eatingStatus
    {
        // Enumeration values
        NOT_ATTENDING, EATING_AT_RESTAURANT, BRING_LUNCH;
    };


    // Widgets variables
    private Button buttonLogOut;
    private Button buttonCreateGroup;
    private Button buttonJoinGroup;


    // Firebase variables
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseGroups;

    ListView listViewGroups;
    List<Group> groupList;
    List<aUser> usersList; // List of groups members for a given group
    String currentUser;

    String groupID;

    int numberOfNotAttending        = 0;
    int numberOfEatingAtRestaurant  = 0;
    int numberOfBringLunch          = 0;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private String phoneNumberToSendSMS = "";

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
        listViewGroups = (ListView) findViewById(R.id.listViewGroups);

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

        // Getting the reference of groups node
        databaseGroups = FirebaseDatabase.getInstance().getReference();

        // Getting information about the current user
        currentUser = firebaseAuth.getCurrentUser().getUid();

        // Instantiating variables
        groupList = new ArrayList<>();
        usersList = new ArrayList<>();

        // Loading the groups the user belongs to
        loadGroup();

        // Clicking a row from the table view
        listViewGroups.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // getting th group ID
                groupID = groupList.get(position).getID();
                Log.d("GROUP ID SELECTED", groupID);
                // Get all users
                loadUsers(position);
            }
        });
    }

    private void startMap() {
        Intent intent = new Intent(homePage.this, MapsActivity.class);
        intent.putExtra("GROUP_ID", this.groupID);
        startActivity(intent);
    }

//     @Override
//     protected void onStart() {
//         super.onStart();

//         databaseGroups.child("UserAndTheirGroups").child(currentUser).addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange (DataSnapshot dataSnapshot) {
//                 groupList.clear();
//                 for(DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){

//                     // if (groupsSnapshot.getValue().equals(firebaseAuth.getCurrentUser().getUid())){
//                     //    Group group = new Group(firebaseAuth.getCurrentUser().getUid(),"","","");
//                     //    groupList.add(group);

//                      Group group = groupsSnapshot.getValue(Group.class);
//                      groupList.add(group);
//                 }

//                 GroupList adapter = new GroupList(homePage.this,groupList);
//                 listViewGroups.setAdapter(adapter);
//             }

//             @Override
//             public void onCancelled(DatabaseError databaseError) {

//             }
//         });
//     }




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

    // The purpose of this class is to update the eating status of the user for a given group
    private void userResponseToEating(String userStatus)
    {
        // Get a firebase user object
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Get the display name of the user
        String userDisplayName  = user.getDisplayName();

        // Create an aUser object
        aUser updatedUser = new aUser(userDisplayName, userStatus);

        // Update the current user eating status by storing the aUser object
        databaseGroups.child("GroupsAndTheirMembers").child(this.groupID).child(user.getUid()).setValue(updatedUser).addOnCompleteListener(this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                // Determine if task was completed successfully
                if(task.isSuccessful())
                {
                    // Notifying the user that saving was NOT successful
                    Toast.makeText(homePage.this, "Your status has been updated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(homePage.this, "Unable to let your friends know.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadGroup(){
        databaseGroups.child("UserAndTheirGroups").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot) {
                groupList.clear();
                for(DataSnapshot groupsSnapshot : dataSnapshot.getChildren()){

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
    private void loadUsers(final int position)
    {
        Log.d("ID to be used = ", this.groupID);
        databaseGroups.child("GroupsAndTheirMembers").child(this.groupID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Click", "Entre");
                // Clearing the list
                usersList.clear();

                resetEatingStatusCount();

                // Obtaining users and placing them on the list of users
                for(DataSnapshot usersSnapshot : dataSnapshot.getChildren())
                {
                    aUser user = usersSnapshot.getValue(aUser.class);
                    settingEatingStatusCount(user);
                    usersList.add(user);
                }

                // Print users
                createAndShowAlert(position);
                // DEBUGG
                Log.v("Restaurant Count", "" + numberOfEatingAtRestaurant);
                Log.v("Bring Lunch Count", "" + numberOfBringLunch);
                Log.v("Not Attending Count", "" + numberOfNotAttending);
                // DEBUG

            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
    private SpannableStringBuilder getAllUsers()
    {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String[] users = new String[usersList.size()];
        builder.append("Users: \n");
        for(int i = 0 ; i< usersList.size(); i++)
        {
                users[i] = usersList.get(i).getName() + "\n";
                switch (usersList.get(i).getEatingStatus())
                {
                    case "NOT_ATTENDING":
                        SpannableString notA= new SpannableString(users[i]);
                        notA.setSpan(new ForegroundColorSpan(Color.RED), 0, users[i].length(), 0);
                        builder.append(notA);
                        break;
                    case "BRING_LUNCH":
                        SpannableString brinL= new SpannableString(users[i]);
                        brinL.setSpan(new ForegroundColorSpan(Color.GREEN), 0, users[i].length(), 0);
                        builder.append(brinL);
                        break;
                    case "EATING_AT_RESTAURANT":
                        SpannableString eaR= new SpannableString(users[i]);
                        eaR.setSpan(new ForegroundColorSpan(Color.BLUE), 0, users[i].length(), 0);
                        builder.append(eaR);
                        break;
                    default:
                        SpannableString def= new SpannableString(users[i]);
                        def.setSpan(new ForegroundColorSpan(Color.RED), 0, users[i].length(), 0);
                        builder.append(def);
                        break;
                }
        }

        return builder;
    }
    private void createAndShowAlert(int position)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(homePage.this);
        LayoutInflater inflater = getLayoutInflater();

        View customView = inflater.inflate(R.layout.customalert, null);
        TextView group = (TextView)customView.findViewById(R.id.groupInfo);
        TextView users = (TextView)customView.findViewById(R.id.usersInfo);

        TextView nL = (TextView)customView.findViewById(R.id.lunchCounter);
        TextView nR = (TextView)customView.findViewById(R.id.restCounter);
        TextView nNA = (TextView)customView.findViewById(R.id.notACounter);
        Button invitation = (Button) customView.findViewById(R.id.invitations);
        invitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogForPhoneNumber();
            }
        });


        alert.setTitle("Lets Lunch ");
        alert.setIcon(R.drawable.splash_img);

        alert.setView(customView);


        group.setText("Group: "+groupList.get(position).getName()+
                "\n"+"Id: "+groupList.get(position).getID()+
                "\n"+"Location: "+groupList.get(position).getLocation()+
                "\n"+"Time: "+groupList.get(position).getTime());

        users.setText(getAllUsers(), TextView.BufferType.SPANNABLE);

        SpannableStringBuilder notAC_builder = new SpannableStringBuilder();
        SpannableStringBuilder BLC_builder = new SpannableStringBuilder();
        SpannableStringBuilder BRLC_builder = new SpannableStringBuilder();

        SpannableString notAC= new SpannableString(""+numberOfNotAttending);
        notAC.setSpan(new ForegroundColorSpan(Color.RED), 0, (""+numberOfNotAttending).length(), 0);
        notAC_builder.append(notAC);

        SpannableString BLC= new SpannableString(""+numberOfBringLunch);
        BLC.setSpan(new ForegroundColorSpan(Color.GREEN), 0, (""+numberOfBringLunch).length(), 0);
        BLC_builder.append(BLC);

        SpannableString BRLC= new SpannableString(""+numberOfEatingAtRestaurant);
        BRLC.setSpan(new ForegroundColorSpan(Color.BLUE), 0, (""+numberOfEatingAtRestaurant).length(), 0);
        BRLC_builder.append(BRLC);

        nL.setText(notAC_builder,TextView.BufferType.SPANNABLE);
        nR.setText(BLC_builder,TextView.BufferType.SPANNABLE);
        nNA.setText(BRLC_builder,TextView.BufferType.SPANNABLE);

        /*
        alert.setMessage("Group: "+groupList.get(position).getName()+
                "\n"+"Id: "+groupList.get(position).getID()+
                "\n"+"Location: "+groupList.get(position).getLocation()+
                "\n"+"Time: "+groupList.get(position).getTime()+"\n"+
                "\n"+"Users \n"+ getAllUsers()
        );
        */

        alert.setNegativeButton(R.string.lunch_box,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.BRING_LUNCH;
                        dialog.dismiss();
                        userResponseToEating(userStatus.toString());
                    }
                });
        alert.setPositiveButton(R.string.restaurant,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.EATING_AT_RESTAURANT;
                        userResponseToEating(userStatus.toString());
                        dialog.cancel();
                        startMap();
                    }
                });
        alert.setNeutralButton(R.string.neutral,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eatingStatus userStatus = eatingStatus.NOT_ATTENDING;
                        userResponseToEating(userStatus.toString());
                        dialog.cancel();
                    }
                });
        alert.show();
    } // End of createAndShowAlert()

    // The purpose of this function is to specify the number of user having each eating status
    private void settingEatingStatusCount(aUser myUser)
    {
        if (myUser.getEatingStatus().equals(eatingStatus.BRING_LUNCH.toString()))
        {
            this.numberOfBringLunch++;
        }
        else if (myUser.getEatingStatus().equals(eatingStatus.EATING_AT_RESTAURANT.toString()))
        {
            this.numberOfEatingAtRestaurant++;
        }
        else
        {
            this.numberOfNotAttending++;
        }


    }

    // The purpose of this function is to reset the count of the user
    private void resetEatingStatusCount()
    {
        this.numberOfEatingAtRestaurant = 0;
        this.numberOfNotAttending       = 0;
        this.numberOfBringLunch         = 0;
    }

    // The purpose of this code is to display a dialog for requesting the phone number for sending the SMS text
    private void dialogForPhoneNumber()
    {
        // Creating the dialog and setting title and splash image
        AlertDialog.Builder alert = new AlertDialog.Builder(homePage.this);
        alert.setTitle("Enter Phone Number");
        alert.setIcon(R.drawable.splash_img);
        // Setting the Textfield for entering the phone number
        final EditText userPhoneInput = new EditText(homePage.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        userPhoneInput.setLayoutParams(lp);
        alert.setView(userPhoneInput);
        // Setting the Send button invitation
        alert.setPositiveButton(R.string.Send_Invitation, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // Clearing the previous Phone Number
                phoneNumberToSendSMS = "";
                // Getting the new phone number
                phoneNumberToSendSMS = userPhoneInput.getText().toString();
                // Sending the SMS
                sendMessage();
            }
        });
        alert.show();
    }
    // The purpose of this function is to set the permissions for sending SMS
    private void sendMessage()
    {
        // If no permission has been granted by the user
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS))
            {
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        else // Permission has been granted by the user
        {
            this.sendingMessageLogic();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    this.sendingMessageLogic();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
    // This is the logic involved for sending a text message
    private void sendingMessageLogic()
    {
        if(this.phoneNumberIsInteger())
        {
            // Setting a message content
            String messageToSet = "Join us with this code = "+ this.groupID;
            // Creating a Manager
            SmsManager smsManager = SmsManager.getDefault();
            // Sending the Text
            smsManager.sendTextMessage(this.phoneNumberToSendSMS, null, messageToSet, null, null);
            // Letting the user know it was a success
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Invalid Phone Number", Toast.LENGTH_LONG).show();
        }
    }
    // Determining if the phone number provided is valie
    private Boolean phoneNumberIsInteger()
    {
        return android.util.Patterns.PHONE.matcher(this.phoneNumberToSendSMS).matches();
    }

} // End of class