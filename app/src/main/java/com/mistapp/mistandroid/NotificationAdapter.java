package com.mistapp.mistandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mistapp.mistandroid.model.Notification;
import com.mistapp.mistandroid.model.Teammate;

import java.util.ArrayList;

/**
 * Created by aadil on 1/17/17.
 */

public class NotificationAdapter extends ArrayAdapter {

    private static class ViewHolder {
        TextView text;
        TextView time;
    }

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, R.layout.item_notification, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Notification current_notification = (Notification) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_notification, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.notification_text);
            viewHolder.time = (TextView) convertView.findViewById(R.id.notification_time);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.text.setText(current_notification.getTitle());
        viewHolder.time.setText(Long.toString(current_notification.getTime()));
        // Return the completed view to render on screen
        return convertView;
    }
}
