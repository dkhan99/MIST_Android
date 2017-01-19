package com.mistapp.mistandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Notification;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by aadil on 1/17/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private BottomBar bottomBar;
    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        boolean sendingKeyValPairs = false;

        sharedPref = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // If the application is in the foreground handle both data and notification messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        remoteMessage.getNotification().getTitle();

        //create notification and add to shared preferences
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        long currentTime = System.currentTimeMillis();
        Notification newNotification = new Notification(title, body, currentTime ,false);

        //if shared preferences doesnt contain notifications
        if (sharedPref.contains("notifications")){
            Log.d(TAG, "Notification prefs exist");
            Gson gson = new Gson();

            //retrieving notification list
            String jsonList = sharedPref.getString("notifications", "");
            Log.d(TAG, "retreived json list: " +jsonList);
            ArrayList<Notification> notificationArray = gson.fromJson(jsonList, new TypeToken <ArrayList<Notification>>() {}.getType());
            Log.d(TAG, "size of object list: " +notificationArray.size());
            //adding new notification to array and saving to shared preferences
            notificationArray.add(newNotification);
            String jsonArray = gson.toJson(notificationArray);
            editor.putString("notifications", jsonArray);
            Log.d(TAG, "pushing notifications to editor - exist");
        }
        //shared preferences contain notifications
        else {
            Log.d(TAG, "Notification prefs dont exist");
            //creating new notification list and saving to shared preferences
            ArrayList<Notification> newNotificationArray = new ArrayList<Notification>();
            newNotificationArray.add(newNotification);
            Gson gson = new Gson();
            String jsonArray = gson.toJson(newNotificationArray);
            Log.d(TAG, "converted gson to json");
            editor.putString("notifications", jsonArray);
            Log.d(TAG, "pushing notifications to editor - didnt exist");
        }
        //add one to whatever the number of original unread notifications were. default = 0
        int numUnreadNotifications = sharedPref.getInt("numUnreadNotifications", 0) + 1;

        editor.putInt("numUnreadNotifications", numUnreadNotifications);
        editor.commit();
    }
}