package com.letslunch.agileteam8.letslunch;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pedrogomezlopez on 20/04/2017.
 */

public class GroupList extends ArrayAdapter<Group>
{
    private Activity context;
    private List<Group> groupList;

    public GroupList(Activity context, List<Group> groupList){
        super(context,R.layout.list_layout,groupList);
        this.context = context;
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout,null, true);
        TextView textViewGroupName = (TextView) listViewItem.findViewById(R.id.textViewName);

        Group group = groupList.get(position);

        textViewGroupName.setText(group.getName());

        return listViewItem;
    }
}
