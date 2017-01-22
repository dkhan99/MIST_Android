package com.mistapp.mistandroid;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Notification;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mistapp.mistandroid.R.id.bottom;
import static com.mistapp.mistandroid.R.id.uid;

/*
 * Actual My Mist Activity (Will contain my_team, my_schedule views)
 */

public class MyMistActivity extends AppCompatActivity {

    //changed when the activity changes states - onPause() and onResume(). Used to correctly show notifications
    public static boolean isInForeground;

    private static final String TAG = LogInAuth.class.getSimpleName();

    private BottomBar bottomBar;
    private Bundle args;
    private MapFragment mapFragment;
    private CompetitionsFragment competitionsFragment;
    private MyMistFragment myMistFragment;
    private HelpFragment helpFragment;
    private NotificationsFragment notificationsFragment;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FragmentTransaction transaction;
    private DatabaseReference mDatabase;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mist);

        sharedPref = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        //User is signed in or not already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                sharedPref = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
                editor = sharedPref.edit();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "dash onAuthStateChanged:signed_in:" + user.getUid());
                    //save user's uid in shared preferences
                    editor.putString(getString(R.string.user_uid_key), user.getUid());
                    editor.commit();
                } else { // User is signed out
                    Log.d(TAG, "dash onAuthStateChanged:signed_out");
                    //remove user's uid from shared preferences
                    editor.remove(getString(R.string.user_uid_key));
                    editor.remove(getString(R.string.current_user_key));
                    editor.commit();
                }
            }
        };


        mapFragment = new MapFragment();
        competitionsFragment = new CompetitionsFragment();
        myMistFragment = new MyMistFragment();
        helpFragment = new HelpFragment();
        notificationsFragment = new NotificationsFragment();

        args = new Bundle();

        transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, myMistFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTabPosition(2);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Log.d(TAG, "Tab selected: "+ tabId);
                updateBottomBarNotifications();
                transaction = getSupportFragmentManager().beginTransaction();

                if (tabId == R.id.tab_map) {
                    transaction.replace(R.id.fragment_container, mapFragment);
                }
                if (tabId == R.id.tab_competitions) {
                    transaction.replace(R.id.fragment_container, competitionsFragment);
                }
                if (tabId == R.id.tab_my_mist) {
                    transaction.replace(R.id.fragment_container, myMistFragment);
                }
                if (tabId == R.id.tab_help) {
                    transaction.replace(R.id.fragment_container, helpFragment);
                }
                if (tabId == R.id.tab_notifications) {
                    transaction.replace(R.id.fragment_container, notificationsFragment);
                }

                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_map) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ACTIVITY HAS Resumed!");
        //Any time the activity is started from a hidden state, check for updates in notifications
        updateBottomBarNotifications();
        isInForeground =true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInForeground = false;
    }


    //at this time, the actual new notification will have been added to shared preferences
    public void updateBottomBarNotifications(){
        int numUnreadNotifications = sharedPref.getInt("numUnreadNotifications", 0);

        Gson gson = new Gson();
        String jsonList = sharedPref.getString("notifications", "");
//        ArrayList<Notification> notificationArray = gson.fromJson(jsonList, new TypeToken<ArrayList<Notification>>() {}.getType());
//        int numUnread = 0;
//        for (Notification current : notificationArray){
//            if (current.getSeen()== false){
//                numUnread ++;
//            }
//        }

        BottomBarTab notificationTab = bottomBar.getTabAtPosition(4);

        //remove badge if there are no new notifications. Set the badge count appropriately if there are
        if (numUnreadNotifications == 0){
            notificationTab.removeBadge();
            Log.d(TAG, "No notifications to show -> remove badge");
        } else{
            notificationTab.setBadgeCount(numUnreadNotifications);
            Log.d(TAG, "Adding notification badges: " +numUnreadNotifications);
        }

    }


}