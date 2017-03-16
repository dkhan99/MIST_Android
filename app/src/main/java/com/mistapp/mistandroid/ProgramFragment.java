package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class ProgramFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "DEBUG";
    private Button mHelpButton;
    private Button mCampusButton;
    private Button mNurseButton;
    private Button fridayProgramButton;
    private Button saturdayProgramButton;
    private Button sundayProgramButton;
    private CacheHandler cacheHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        getActivity().setTitle(getResources().getString(R.string.program_page_title));
        View view = inflater.inflate(R.layout.fragment_program, container, false);

        //define and set toolbar. Removed this because of error I was getting... need to look into it
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getActivity().getApplication(), sharedPref, editor);


        mHelpButton = (Button) view.findViewById(R.id.helpLine);
        mCampusButton = (Button) view.findViewById(R.id.campusPolice);
        mNurseButton = (Button) view.findViewById(R.id.nurse);
        fridayProgramButton = (Button) view.findViewById(R.id.friday_schedule_button);
        saturdayProgramButton = (Button) view.findViewById(R.id.saturday_schedule_button);
        sundayProgramButton = (Button) view.findViewById(R.id.sunday_schedule_button);

        //attaching listeners to button and link
        mHelpButton.setOnClickListener(this);
        mCampusButton.setOnClickListener(this);
        mNurseButton.setOnClickListener(this);
        fridayProgramButton.setOnClickListener(this);
        saturdayProgramButton.setOnClickListener(this);
        sundayProgramButton.setOnClickListener(this);

        showNurseButton();

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

            Uri number = Uri.parse("tel:6785616478");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);

        } else if (view == mCampusButton) {
            Log.d(TAG, "campus police");

            Uri number = Uri.parse("tel:7065425813");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);

        } else if (view == mNurseButton) {
            Log.d(TAG, "nurse");
            Uri number = Uri.parse("tel:4043171431");
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);
        }

        else if (view == fridayProgramButton) {
            Log.d(TAG, "friday program");
            Intent intent = new Intent(getActivity().getBaseContext(),
                    MyMistActivity.class);
            intent.putExtra(getString(R.string.program_image), "friday");
            getActivity().startActivity(intent);
        }
        else if (view == saturdayProgramButton) {
            Log.d(TAG, "saturday program");
            Intent intent = new Intent(getActivity().getBaseContext(),
                    MyMistActivity.class);
            intent.putExtra(getString(R.string.program_image), "saturday");
            getActivity().startActivity(intent);
        }
        else if (view == sundayProgramButton) {
            Log.d(TAG, "sunday program");
            Intent intent = new Intent(getActivity().getBaseContext(),
                    MyMistActivity.class);
            intent.putExtra(getString(R.string.program_image), "sunday");
            getActivity().startActivity(intent);
        }

    }

    private void showNurseButton(){
        if (cacheHandler.cacheContains(getString(R.string.current_user_type))){
            if (cacheHandler.getUserType().equals("coach") && isCurrentDateDuringMistWeekend()){
                mNurseButton.setVisibility(View.VISIBLE);
            } else{
                mNurseButton.setVisibility(View.GONE);
            }

        }

    }

    public static boolean isCurrentDateDuringMistWeekend() {

        String fromString = "March 18, 2017"; //Sat Mar 18 00:00:00 EDT 2017
        String toString = "March 20, 2017"; //Mon Mar 20 00:00:00 EDT 2017

        // SimpleDateFormat parser = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy");
        DateFormat parser = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);

        Date fromDate = new Date();
        Date toDate = new Date();
        Date currentDate = new Date();

        try{
            fromDate = parser.parse(fromString);
            toDate = parser.parse(toString);
            currentDate = new Date();

        } catch(Exception e){
            System.out.println(e);
            return false;
        }

        //if current date is during mist weekend, return true
        if (currentDate.after(fromDate) && currentDate.before(toDate)){
            return true;
        }

        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        BottomBar bbar = ((MyMistActivity)getActivity()).getBottomBar();
        bbar.selectTabAtPosition(3);
    }
}


