package com.mistapp.mistandroid;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

/*
 * Actual My Mist Activity (Will contain my_team, my_schedule views)
 */

public class MyMistActivity extends AppCompatActivity {
    private BottomBar bottomBar;
    private static final String TAG = LogInAuth.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mist);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                Log.d(TAG, "TAB SELECTEDDD: "+ tabId);

                if (tabId == R.id.tab_map) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }
                if (tabId == R.id.tab_competitions) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }
                if (tabId == R.id.tab_my_mist) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }
                if (tabId == R.id.tab_help) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }
                if (tabId == R.id.tab_notifications) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }
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