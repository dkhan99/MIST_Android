package com.mistapp.mistandroid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WeekendSched extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekend_sched);
        copyAssets();
         }

    /**
     * Allows user to download the pdf onto their phone from the link
     */
    private void copyAssets() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://drive.google.com/file/d/0B9GG5vxkVHa0Q0tZQjdyRFN0Mmc/view")));
    }
}
