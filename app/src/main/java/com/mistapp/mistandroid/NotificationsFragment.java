package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Notification;
import com.mistapp.mistandroid.model.Teammate;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    ListView notifications_lv;
    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    BottomBar bottomBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications_lv = (ListView)view.findViewById(R.id.notification_list);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        ArrayList<Notification> notificationList = new ArrayList<Notification>();


        //retreive notifications from shared preferences
        if (sharedPref.contains("notifications")){
            Gson gson = new Gson();
            String jsonList = sharedPref.getString("notifications", "");
            ArrayList<Notification> notificationArray = gson.fromJson(jsonList, new TypeToken<ArrayList<Notification>>() {}.getType());
            Log.d(TAG, "Showing x notifications: " +notificationArray.size() );

            //set all notifications seen = true
            for (Notification notification : notificationArray){
                notification.setSeen(true);
            }

            //update shared preferences to reflect the seen = true
            String jsonArray = gson.toJson(notificationArray);
            editor.putString("notifications", jsonArray);
            editor.commit();

            NotificationAdapter listAdapter = new NotificationAdapter(getActivity(), notificationArray);
            notifications_lv.setAdapter(listAdapter);
        }
        else{
            Log.d(TAG, "No notifications exist- cannot show anything here");
        }


        return view;

    }
}


