package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HelpFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "DEBUG";
    private Button mHelpButton;
    private Button mCampusButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        //define and set toolbar. Removed this because of error I was getting... need to look into it
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mHelpButton = (Button) view.findViewById(R.id.helpLine);
        mCampusButton = (Button) view.findViewById(R.id.campusPolice);

        //attaching listeners to button and link
        mHelpButton.setOnClickListener(this);
        mCampusButton.setOnClickListener(this);

        return view;

    }

    /**
     * When the register button is clicked, we check if the filled out form is valid
     * @param view
     */
    @Override
    public void onClick(View view) {
        ProgressDialog progress = new ProgressDialog(getActivity());
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


