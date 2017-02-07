package com.mistapp.mistandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mistapp.mistandroid.model.Teammate;
import com.mistapp.mistandroid.model.User;

import java.util.ArrayList;

/**
 * Created by aadil on 1/13/17.
 */

public class MyTeamAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView name;
        TextView phoneNumber;
    }

    public MyTeamAdapter(Context context, ArrayList<Teammate> teammates) {
        super(context, R.layout.item_team_member, teammates);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Teammate teammate = (Teammate) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_team_member, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.teammember_name);
            viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.teammember_phone);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(teammate.getName());
        String formattedNumber = getFormattedPhoneNumber(teammate.getPhoneNumber());
        viewHolder.phoneNumber.setText(formattedNumber);

        // Return the completed view to render on screen
        return convertView;
    }

    //format the number with area code + country code
    private String getFormattedPhoneNumber(long number){
        String phoneNumber = Long.toString(number);
        if (phoneNumber.length() == 10){
            phoneNumber = ( "(" + phoneNumber.substring(0,3) + ")-" + phoneNumber.substring(3,6) + "-" + phoneNumber.substring(6,9));
        }
        else if (phoneNumber.length() == 11){
            phoneNumber = ( "+" + phoneNumber.charAt(0) + "(" + phoneNumber.substring(1,4) + ")-" + phoneNumber.substring(4,7) + "-" + phoneNumber.substring(7,10));
        }
        //else ->  keep phone number as is
        return phoneNumber;

    }

}
