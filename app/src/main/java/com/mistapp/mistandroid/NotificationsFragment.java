package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Notification;
import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    ListView notifications_lv;
    BottomBar bottomBar;
    ArrayList<Notification> notificationArray;
    View popupView;
    LayoutInflater currentInflater;
    ViewGroup currentContainer;
    private CacheHandler cacheHandler;
    int layoutHeight;
    int layoutWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Notifications");

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getActivity().getApplication(), sharedPref, editor);


        currentInflater = inflater;
        currentContainer = container;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
//        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.notification_list_layout);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        layoutWidth = metrics.widthPixels;
        layoutHeight = metrics.heightPixels;
//        layoutHeight = layout.getHeight();
//        layoutWidth = layout.getWidth();
        notifications_lv = (ListView)view.findViewById(R.id.notification_list);

        //retreive notifications from shared preferences
        if (cacheHandler.cacheContains(getString(R.string.notifications))){
            Gson gson = new Gson();
            String jsonList = cacheHandler.getNotificationsJson();
            notificationArray = gson.fromJson(jsonList, new TypeToken<ArrayList<Notification>>() {}.getType());

            Log.d(TAG, "Showing x notifications: " +notificationArray.size() );
            Log.d(TAG, "Showing notifications: " +jsonList );
            TextView noNotificationsText = (TextView)view.findViewById(R.id.no_notifications_text);
            noNotificationsText.setVisibility(View.INVISIBLE);

            //Set listview's adapter and set onClickListener
            NotificationAdapter listAdapter = new NotificationAdapter(getActivity(), notificationArray);
            notifications_lv.setAdapter(listAdapter);
            AdapterView.OnItemClickListener listener = createItemClickListener();
            notifications_lv.setOnItemClickListener(listener);

        }
        else{
            Log.d(TAG, "No notifications exist- cannot show anything here");
            TextView noNotificationsText = (TextView)view.findViewById(R.id.no_notifications_text);
            noNotificationsText.setVisibility(View.VISIBLE);

        }


        return view;

    }

    public AdapterView.OnItemClickListener createItemClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Clicked a Notification");
                Notification clickedNotification = (Notification)parent.getItemAtPosition(position);


                //[TODO]add code to display detailed notificaiton view (show title and the body)

                //if new notificaiton -> Set notification = seen and update shared preferences to reflect the seen = true
                if (!clickedNotification.getSeen()) {
                    clickedNotification.setSeen(true);
                    //set numUnreadNotifications to one less than before
                    int numUnreadNotifications = cacheHandler.getNumUnreadNotifications(1);
                    cacheHandler.cacheNumUnreadNotifications(numUnreadNotifications - 1);
                    cacheHandler.commitToCache();
                    removeNotificationHighlighting(view);
                }

                String titleText = clickedNotification.getTitle();
                String bodyText = clickedNotification.getBody();
                String timeText = (String)((TextView)view.findViewById(R.id.notification_time)).getText();
                initiatePopupWindow(titleText, bodyText, timeText);

                Gson gson = new Gson();
                String jsonArray = gson.toJson(notificationArray);
                cacheHandler.cacheNotifications(jsonArray);
                cacheHandler.commitToCache();

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

    private void initiatePopupWindow(String title, String body, String time) {
        try {
// We need to get the instance of the LayoutInflater
            View layout = currentInflater.inflate(R.layout.notification_detail,null, false);

            int widthDimen = (int)(layoutWidth * 8.0 / 10.0);
            int heightDimen = (int)(layoutHeight * 5.5 / 10.0);
            Log.d(TAG, "DIMEN WIDTH: "+widthDimen);
            Log.d(TAG, "DIMEN HAIGHT: "+heightDimen);
            final PopupWindow pwindo = new PopupWindow(layout, widthDimen, heightDimen, true);

            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button btnClosePopup = (Button) layout.findViewById(R.id.dismiss);
            TextView titleTextView = (TextView)layout.findViewById(R.id.popup_title);
            TextView bodyTextView = (TextView)layout.findViewById(R.id.popup_body);
            TextView timeText = (TextView)layout.findViewById(R.id.popup_time);

            titleTextView.setText(title);
            bodyTextView.setText(body);
            timeText.setText(time);
            Log.d(TAG, "Values should change: "+ title + " "+body);
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "dismissing notification detail");
                    pwindo.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


