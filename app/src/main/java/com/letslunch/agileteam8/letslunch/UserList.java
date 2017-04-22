package com.letslunch.agileteam8.letslunch;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pedrogomezlopez on 22/04/2017.
 */

public class UserList extends ArrayAdapter<aUser>

    {
        private Activity context;
        private List<aUser> usersList;

    public UserList(Activity context, List < aUser > usersList) {
        super(context, R.layout.list_layout, usersList);
        this.context = context;
        this.usersList = usersList;
    }

        @NonNull
        @Override
        public View getView ( int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);
        TextView textViewUserName = (TextView) listViewItem.findViewById(R.id.textViewName);

        aUser user = usersList.get(position);
            textViewUserName.setText(user.getName());

        return listViewItem;
    }
}
