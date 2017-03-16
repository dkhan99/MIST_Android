package com.mistapp.mistandroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Notification;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by aadil on 1/17/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private BottomBar bottomBar;
    private CacheHandler cacheHandler;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences sharedPref = getApplication().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getApplication(), sharedPref, editor);
        boolean sendingKeyValPairs = false;

        // If the application is in the foreground handle both data and notification messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        Map dataMap = remoteMessage.getData();
        String title = (String)dataMap.get("title");
        String body = (String)dataMap.get("body");
        Long time = Long.valueOf((String)dataMap.get("time"));

        if (appIsInForeground()){
            Log.d(TAG, "APP IS IN FOREGROUND");
            createNotification(title, body, time);
        }
        else{
            Log.d(TAG, "APP IS IN BACKGROUND");
            createNotification(title, body, time);
            showNotificationPopup(title, body, time);
        }

    }

    //creates a notification and adds it as well as the total unread notifications to shared preferences.
    //These values are then retrieved by MyMistActivity
    public void createNotification(String title, String body, Long time){
        Notification newNotification = new Notification(title, body, time ,false);

        String jsonArray;

        //if shared preferences doesnt contain notifications
        if (cacheHandler.cacheContains(getString(R.string.notifications))){
            Log.d(TAG, "Notification prefs exist");
            Gson gson = new Gson();

            //retrieving notification list
            String jsonList = cacheHandler.getUserUid();
            Log.d(TAG, "retreived json list: " +jsonList);
            ArrayList<Notification> notificationArray = gson.fromJson(jsonList, new TypeToken <ArrayList<Notification>>() {}.getType());

            //adding new notification to array and saving to shared preferences
            notificationArray.add(0,newNotification);
            jsonArray = gson.toJson(notificationArray);
            Log.d(TAG, "pushing notifications to editor - exist");
        }
        //shared preferences contain notifications
        else {
            Log.d(TAG, "Notification prefs dont exist");
            //creating new notification list and saving to shared preferences
            ArrayList<Notification> newNotificationArray = new ArrayList<Notification>();
            newNotificationArray.add(newNotification);
            Gson gson = new Gson();
            jsonArray = gson.toJson(newNotificationArray);
            Log.d(TAG, "pushing notifications to editor - didnt exist");
        }

        //add one to whatever the number of original unread notifications were. default = 0
        int numUnreadNotifications = cacheHandler.getNumUnreadNotifications(0) + 1;

        cacheHandler.cacheNotifications(jsonArray);
        cacheHandler.cacheNumUnreadNotifications(numUnreadNotifications);
        cacheHandler.commitToCache();

        Log.d("Num unread notifs: " , " lkj "+numUnreadNotifications);
    }

    //shows the actual popup notification. Notification click is handled as well, and the user is taken to the mist-app.
    public void showNotificationPopup(String title, String body, Long time){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.original_logo)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setVibrate(new long[] { 600, 600, 600, 600, 1000 });

        final int version = Build.VERSION.SDK_INT;
        Log.d(TAG, "build version is: "+version);
        if (version >= 23) {
            mBuilder.setColor(ContextCompat.getColor(getApplication().getApplicationContext(), R.color.mistRed));
        } else {
            mBuilder.setColor(getResources().getColor(R.color.mistRed));
        }

        //creating the intent that will be triggered when user clicks the notification
        //title, body, and time of the notification is passed to the intent
        Intent resultIntent = new Intent(this, WelcomeActivity.class);
        resultIntent.putExtra("title",title);
        resultIntent.putExtra("body",body);
        resultIntent.putExtra("time",time);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(WelcomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //when user clicks the notification, the popup dissapears from the tray
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(2, mBuilder.build());
    }

    private boolean appIsInForeground() {
        return MyMistActivity.isInForeground || WelcomeActivity.isInForeground || LogInAuth.isInForeground || RegisterAuth.isInForeground;

    }
}