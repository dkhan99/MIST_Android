package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HelpDirectory extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "DEBUG";
    private Button mHelpButton;
    private Button mCampusButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_directory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHelpButton = (Button) findViewById(R.id.helpLine);
        mCampusButton = (Button) findViewById(R.id.campusPolice);

        //attaching listeners to button and link
        mHelpButton.setOnClickListener(this);
        mCampusButton.setOnClickListener(this);


    }


    /**
     * When the register button is clicked, we check if the filled out form is valid
     * @param view
     */
    @Override
    public void onClick(View view) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Opening call :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        Log.d(TAG, "Clicked");
        if (view == mHelpButton) {
            Log.d(TAG, "help");

            Uri number = Uri.parse("tel:6783083161");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);



        } else if (view == mCampusButton) {
            Log.d(TAG, "campus");

            Uri number = Uri.parse("tel:4703433248");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);

        }
    }

}
