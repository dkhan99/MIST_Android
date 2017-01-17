package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mistapp.mistandroid.model.Notification;
import com.mistapp.mistandroid.model.Teammate;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    ListView notifications_lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications_lv = (ListView)view.findViewById(R.id.notification_list);

        ArrayList<Notification> notificationList = new ArrayList<Notification>();

        //add notifications
        notificationList.add(new Notification("Lunch time!", "3 mins ago", false));
        notificationList.add(new Notification("Dhuhr salah in Room 302!", "23 mins ago", false));
        notificationList.add(new Notification("Your debate competition is starting!", "50 mins ago", true));

        NotificationAdapter listAdapter = new NotificationAdapter(getActivity(), notificationList);
        notifications_lv.setAdapter(listAdapter);

        return view;

    }
}


