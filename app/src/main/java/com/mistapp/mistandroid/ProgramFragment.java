package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;


public class ProgramFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "DEBUG";
    private Button mHelpButton;
    private Button mCampusButton;
    private Button fridayProgramButton;
    private Button saturdayProgramButton;
    private Button sundayProgramButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        getActivity().setTitle("Program");
        View view = inflater.inflate(R.layout.fragment_program, container, false);

        //define and set toolbar. Removed this because of error I was getting... need to look into it
//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        mHelpButton = (Button) view.findViewById(R.id.helpLine);
        mCampusButton = (Button) view.findViewById(R.id.campusPolice);
        fridayProgramButton = (Button) view.findViewById(R.id.friday_schedule_button);
        saturdayProgramButton = (Button) view.findViewById(R.id.saturday_schedule_button);
        sundayProgramButton = (Button) view.findViewById(R.id.sunday_schedule_button);

        //attaching listeners to button and link
        mHelpButton.setOnClickListener(this);
        mCampusButton.setOnClickListener(this);
        fridayProgramButton.setOnClickListener(this);
        saturdayProgramButton.setOnClickListener(this);
        sundayProgramButton.setOnClickListener(this);

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
        else if (view == fridayProgramButton) {
            Log.d(TAG, "friday program");
            Toast.makeText(getActivity(), "need friday program", Toast.LENGTH_SHORT);
//            String fridayProgramUri = "";
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fridayProgramUri)));
        }
        else if (view == saturdayProgramButton) {
            Log.d(TAG, "saturday program");
            Toast.makeText(getActivity(), "need saturday program", Toast.LENGTH_SHORT);
//            String saturdayProgramUri = "";
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(saturdayProgramUri)));
        }
        else if (view == sundayProgramButton) {
            Log.d(TAG, "sunday program");
            Toast.makeText(getActivity(), "need sunday program", Toast.LENGTH_SHORT);
//            String sundayProgramUri = "";
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sundayProgramUri)));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        BottomBar bbar = ((MyMistActivity)getActivity()).getBottomBar();
        bbar.selectTabAtPosition(3);
    }
}


