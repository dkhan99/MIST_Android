package com.mistapp.mistandroid;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

/*
 * Actual My Mist Activity (Will contain my_team, my_schedule views)
 */

public class MyMistActivity extends AppCompatActivity {
    private BottomBar bottomBar;
    private static final String TAG = LogInAuth.class.getSimpleName();
    private Bundle args;
    private MyMapFragment myMapFragment;
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


        myMapFragment = new MyMapFragment();
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

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTabPosition(2);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Log.d(TAG, "Tab selected: "+ tabId);
                transaction = getSupportFragmentManager().beginTransaction();

                if (tabId == R.id.tab_map) {
                    transaction.replace(R.id.fragment_container, myMapFragment);
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



}