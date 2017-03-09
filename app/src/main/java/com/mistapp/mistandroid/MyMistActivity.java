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
    private static final String comp = "math";
    private BottomBar bottomBar;
    private Bundle args;
    private MyMapFragment myMapFragment;
    private CompetitionsFragment competitionsFragment;
    private MyMistFragment myMistFragment;
    private GuestMistFragment guestMistFragment;
    private ProgramFragment programFragment;
    private NotificationsFragment notificationsFragment;
    private String currentUserType;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FragmentTransaction transaction;
    private DatabaseReference mDatabase;
    private CacheHandler cacheHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mist);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getApplication(), sharedPref, editor);

        //sets the current user type - when guest is clicked , it equals guest, when register/login, it equals coach or competitor

        //if current user type was passed off in intent, get it
        if (getIntent().getExtras().containsKey(getString(R.string.current_user_type))) {
            currentUserType = (String) getIntent().getExtras().get(getString(R.string.current_user_type));
            Log.d(TAG, "usertype was passed in intent: " + currentUserType);
        } else {
            //otherwise get the currentUserType from cache
            currentUserType = cacheHandler.getUserType();
            Log.d(TAG, "usertype was not passed in intent. getting from cache: " + currentUserType);
        }



        Log.d(TAG, "CURRENT USER IS A : " + currentUserType);


        //User is signed in or not already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "dash onAuthStateChanged:signed_in:" + user.getUid());
                    //save user's uid in shared preferences
                    cacheHandler.cacheUserUid(user.getUid());
                    cacheHandler.commitToCache();

                } else { // User is signed out
                    Log.d(TAG, "dash onAuthStateChanged:signed_out");
                    //remove user's uid from shared preferences
                    cacheHandler.removeCachedUserFields();
                    cacheHandler.commitToCache();
                }
            }
        };



        Bundle bundle = new Bundle();
        String myMessage = "Stackoverflow is cool!";

        myMapFragment = new MyMapFragment();
        competitionsFragment = new CompetitionsFragment();
        myMistFragment = new MyMistFragment();
        guestMistFragment = new GuestMistFragment();
        programFragment = new ProgramFragment();
        notificationsFragment = new NotificationsFragment();

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTabPosition(2);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                boolean reselected = false;
                Log.d(TAG, "NUM FRAGMENTS IN STACK: "+getSupportFragmentManager().getBackStackEntryCount());
                Log.d(TAG, "selecting tab # " + tabId);
                updateBottomBarNotifications();
                transaction = getSupportFragmentManager().beginTransaction();

                if (tabId == R.id.tab_map) {
                    if (getSupportFragmentManager().findFragmentByTag("map") == null) {
                        Log.d(TAG, "is null");
                    }
                    else{
                        reselected = true;
                    }
                    transaction.replace(R.id.fragment_container, myMapFragment, "map");
                }
                if (tabId == R.id.tab_competitions) {
                    if (getSupportFragmentManager().findFragmentByTag("competitions") == null) {
                        Log.d(TAG, "is null");
                    }
                    else{
                        reselected = true;
                    }
                    transaction.replace(R.id.fragment_container, competitionsFragment, "competitions");
                }
                if (tabId == R.id.tab_my_mist) {
                    if (currentUserType.equals("guest")) {
                        if (getSupportFragmentManager().findFragmentByTag("mist") == null) {
                            Log.d(TAG, "is null");
                        }
                        else{
                            reselected = true;
                        }
                        transaction.replace(R.id.fragment_container, guestMistFragment, "mist");
                    } else {
                        if (getSupportFragmentManager().findFragmentByTag("mist") == null) {
                            Log.d(TAG, "is null");
                        }
                        else{
                            reselected = true;
                        }
                        transaction.replace(R.id.fragment_container, myMistFragment, "mist");
                    }
                }
                if (tabId == R.id.tab_program) {
                    if (getSupportFragmentManager().findFragmentByTag("program") == null) {
                        Log.d(TAG, "is null");
                    }
                    else{
                        reselected = true;
                    }
                    transaction.replace(R.id.fragment_container, programFragment, "program");
                }
                if (tabId == R.id.tab_notifications) {
                    if (getSupportFragmentManager().findFragmentByTag("notifications") == null) {
                        Log.d(TAG, "is null");
                    }
                    else{
                        reselected = true;
                    }
                    transaction.replace(R.id.fragment_container, notificationsFragment, "notifications");
                }

                transaction.commit();

                if (!reselected) {
                    transaction.addToBackStack(null);
                    // Commit the transaction
                }
                else{
                    Log.d(TAG, "Was Reselected");
                }
            }
        });


        transaction = getSupportFragmentManager().beginTransaction();

        //if coming bc event was clicked
        if (getIntent().getExtras().containsKey("eventLocation")){
            Log.d(TAG, "coming from event click");
            bundle.putString("eventLocation", getIntent().getStringExtra("eventLocation"));
            myMapFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container, myMapFragment, "map");
        }
        //if coming through a notification
        else if (getIntent().getExtras().containsKey(getString(R.string.received_notification))){
            Log.d(TAG, "coming from notification");
            transaction.replace(R.id.fragment_container, notificationsFragment, "notifications");
        }
        //if coming bc my_mist tab was pressed
        else {
            Log.d(TAG, "coming through mist-tab click");
            // Replace whatever is in the fragment_container view with this fragment and add the transaction to the back stack so the user can navigate back
            if (currentUserType.equals("guest")) {
                transaction.replace(R.id.fragment_container, guestMistFragment, "mist");
            } else {
                transaction.replace(R.id.fragment_container, myMistFragment, "mist");
            }
        }
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ACTIVITY HAS Resumed!");
        //Any time the activity is started from a hidden state, check for updates in notifications
        updateBottomBarNotifications();
        isInForeground = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInForeground = false;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    //at this time, the actual new notification will have been added to shared preferences
    public void updateBottomBarNotifications() {
        int numUnreadNotifications = cacheHandler.getNumUnreadNotifications(0);
        Gson gson = new Gson();
        BottomBarTab notificationTab = bottomBar.getTabAtPosition(4);

        //remove badge if there are no new notifications. Set the badge count appropriately if there are
        if (numUnreadNotifications == 0) {
            notificationTab.removeBadge();
            Log.d(TAG, "No notifications to show -> remove badge");
        } else {
            notificationTab.setBadgeCount(numUnreadNotifications);
            Log.d(TAG, "Adding notification badges: " + numUnreadNotifications);
        }

    }

    public BottomBar getBottomBar() {
        return this.bottomBar;
    }

    @Override
    public void onBackPressed() {
        Log.d("~~~~~~~~~~~`", "back, has been pressed!");

        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag("mist");
        Fragment bracketFragment = getSupportFragmentManager().findFragmentByTag("brackets");

        //if current fragment was main fragment (myMistFragment or GuestMistFragment), do nothing if back is hit
        if (mainFragment != null && mainFragment.isVisible()){
            Log.d(TAG,"current fragment was already main fragment... doing nother");
        }
        else if (bracketFragment != null && bracketFragment.isVisible()){
            Log.d(TAG,"current fragment is bracket fragment... going back to cop page");
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, competitionsFragment, "competitions");

            transaction.commit();
        }
        //if current fragment was any other fragment besides main fragment, show main fragment
        else{
            Log.d(TAG,"current fragment was another fragment... going back to main fragment");
            transaction = getSupportFragmentManager().beginTransaction();
            if (currentUserType.equals("guest")) {
                transaction.replace(R.id.fragment_container, guestMistFragment, "mist");
            }
            else{
                transaction.replace(R.id.fragment_container, myMistFragment, "mist");
            }
            transaction.commit();
        }

    }



}