package com.letslunch.agileteam8.letslunch.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.letslunch.agileteam8.letslunch.Activities.JoinGroupActivity;
import com.letslunch.agileteam8.letslunch.Group;
/**
 * Created by Carl-Henrik Hult on 2017-05-07.
 */

public class DatabaseHandler
{
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private String groupCode;

    public DatabaseHandler ()
    {
        this.firebaseAuth       = FirebaseAuth.getInstance();
        this.databaseReference  = FirebaseDatabase.getInstance().getReference();
    }

    public boolean isUserAlreadySignedIn(FirebaseAuth firebaseObject)
    {
        if(firebaseObject.getCurrentUser() != null)
        {
            return true;
        }


        return false;

    }

}

