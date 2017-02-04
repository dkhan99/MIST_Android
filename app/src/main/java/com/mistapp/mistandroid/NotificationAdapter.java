package com.mistapp.mistandroid;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mistapp.mistandroid.model.Notification;
import com.mistapp.mistandroid.model.Teammate;

import java.util.ArrayList;

/**
 * Created by aadil on 1/17/17.
 */

public class NotificationAdapter extends ArrayAdapter {

    private static final String TAG = NotificationAdapter.class.getSimpleName();

    private static class ViewHolder {
        TextView text;
        TextView time;
        ImageView notificaiton_star;
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
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text = (TextView) convertView.findViewById(R.id.notification_text);
        viewHolder.time = (TextView) convertView.findViewById(R.id.notification_time);
        viewHolder.notificaiton_star = (ImageView) convertView.findViewById(R.id.notification_star);
        setText(viewHolder, current_notification);
        setTime(viewHolder, current_notification);
        setStar(viewHolder, current_notification);
        // Return the completed view to render on screen
        return convertView;
    }


    public void setText(ViewHolder viewHolder, Notification currentNotification){
        viewHolder.text.setText(currentNotification.getTitle());
        if (currentNotification.getSeen() == false){
            viewHolder.text.setTypeface(null, Typeface.BOLD);
            viewHolder.time.setTypeface(null, Typeface.BOLD);
        }
    }

    public void setTime(ViewHolder viewHolder, Notification currentNotification){
        long currentTime = System.currentTimeMillis();
        long notificationTime = currentNotification.getTime();

        long elapsed = currentTime - notificationTime;

        long seconds = (elapsed / 1000) % 60;
        long minutes = (elapsed / (1000 * 60)) % 60;
        long hours = (elapsed / (1000 * 60 * 60)) % 24;
        long days = (elapsed / (1000 * 60 * 60 * 24)) % 365;

        String formattedTime = "";

        //for some strange reason, this is 4 minutes off. eg: when create new notif -> minutes = -4.
        //It countsdown to 0- then resumes normally
        long value = 0;
        if (days <= 0){
            if (hours <= 0){
                if (minutes <=0){
                    formattedTime = "Just now";
                }
                else{
                    formattedTime = minutes + " minutes ago";
                    value = minutes;
                }
            }
            else{
                formattedTime = hours + " hours ago";
                value = hours;
            }
        }
        else {
            formattedTime = days + " days ago";
            value = days;
        }

        //remove 's' at the end of the time. ex: days becomes day
        if(value == 1){
            formattedTime = formattedTime.replace("s ago", " ago");
        }

//        Log.d(TAG,"d: "+days + " h:" + hours + " m:"+minutes + " s:"+seconds);
        viewHolder.time.setText(formattedTime);
    }

    public void setStar(ViewHolder viewHolder, Notification currentNotification){
        //show notification star if the notificaiton hasn't been seen
        if (currentNotification.getSeen() == false){
            viewHolder.notificaiton_star.setVisibility(View.VISIBLE);
        }
    }
}
