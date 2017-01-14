package com.mistapp.mistandroid;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Map;

/*
 * Actual My Mist Activity (Will contain my_team, my_schedule views)
 */

public class MyMistActivity extends AppCompatActivity {
    private BottomBar bottomBar;
    private static final String TAG = LogInAuth.class.getSimpleName();
    private Bundle args;
    private MapFragment mapFragment;
    private CompetitionsFragment competitionsFragment;
    private MyMistFragment myMistFragment;
    private HelpFragment helpFragment;
    private NotificationsFragment notificationsFragment;

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mist);

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

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTabPosition(2);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Log.d(TAG, "Tab selected: "+ tabId);
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

}