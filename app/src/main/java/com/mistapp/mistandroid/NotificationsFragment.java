package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Notification;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    ListView notifications_lv;
    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    BottomBar bottomBar;
    ArrayList<Notification> notificationArray;
    View popupView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        notifications_lv = (ListView)view.findViewById(R.id.notification_list);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        popupView = inflater.inflate(R.layout.fragment_notification_detail, container, false);


        ArrayList<Notification> notificationList = new ArrayList<Notification>();

        AdapterView.OnItemClickListener listener = createItemClickListener();


        //retreive notifications from shared preferences
        if (sharedPref.contains("notifications")){
            Gson gson = new Gson();
            String jsonList = sharedPref.getString("notifications", "");
            notificationArray = gson.fromJson(jsonList, new TypeToken<ArrayList<Notification>>() {}.getType());

            Log.d(TAG, "Showing x notifications: " +notificationArray.size() );
            Log.d(TAG, "Showing notifications: " +jsonList );

            //Set listview's adapter and set onClickListener
            NotificationAdapter listAdapter = new NotificationAdapter(getActivity(), notificationArray);
            notifications_lv.setAdapter(listAdapter);
            notifications_lv.setOnItemClickListener(listener);

        }
        else{
            Log.d(TAG, "No notifications exist- cannot show anything here");
        }


        return view;

    }

    public AdapterView.OnItemClickListener createItemClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Clicked a Notification");
                Notification clickedNotification = (Notification)parent.getItemAtPosition(position);

                removeNotificationHighlighting(view);

                //[TODO]add code to display detailed notificaiton view (show title and the body)

                //Set notification = seen and update shared preferences to reflect the seen = true
                clickedNotification.setSeen(true);
                Gson gson = new Gson();
                String jsonArray = gson.toJson(notificationArray);
                editor.putString("notifications", jsonArray);
                editor.commit();

            }
        };
        return listener;
    }

    //makes the notification row unbolded and removes the star immediately on click
    public void removeNotificationHighlighting(View view){
        TextView text = (TextView) view.findViewById(R.id.notification_text);
        TextView time = (TextView) view.findViewById(R.id.notification_time);
        ImageView star = (ImageView)view.findViewById(R.id.notification_star);
        text.setTypeface(null, Typeface.NORMAL);
        time.setTypeface(null, Typeface.NORMAL);
        star.setVisibility(View.INVISIBLE);
    }
}


