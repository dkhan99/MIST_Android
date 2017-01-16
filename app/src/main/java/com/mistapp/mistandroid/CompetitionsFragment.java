package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.jar.Pack200;

public class CompetitionsFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = RegisterAuth.class.getSimpleName();

    Button viewBracketButton;
    Button viewRulebookButton;
    Spinner competitionPicker;
    ArrayList<String> bracketCompetitions;
    //hashmap item looks like {competitionName:url}
    HashMap<String,String> rulebookUrls;
    HashMap<String,String> bracketUrls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_competitions, container, false);

        rulebookUrls = new HashMap<String, String>();
        bracketUrls = new HashMap<String, String>();

        updateBracketCompetitions();
        updateBracketUrls();
        updateRulebookUrls();

        viewBracketButton = (Button) view.findViewById(R.id.view_bracket_btn);
        viewRulebookButton = (Button) view.findViewById(R.id.view_rules_btn);
        competitionPicker = (Spinner) view.findViewById(R.id.competitions_spinner);
        viewBracketButton.setOnClickListener(this);
        viewRulebookButton.setOnClickListener(this);

        competitionPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                String chosenCompetition = ((TextView)view).getText().toString();

                //show 'view bracket' button only if the competition is a bracket competition
                if (bracketCompetitions.contains(chosenCompetition)){
                    viewBracketButton.setVisibility(View.VISIBLE);
                }
                else{
                    viewBracketButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    /**
     * If buttons are clicked, go to corresponding pdf viewer
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == viewBracketButton) {
            String competitionPicked = competitionPicker.getSelectedItem().toString();
            String url = bracketUrls.get(competitionPicked);
            Log.d(TAG, "URL CHOSEN: " + url);
        }

        if (view == viewRulebookButton) {
            String competitionPicked = competitionPicker.getSelectedItem().toString();
            String url = rulebookUrls.get(competitionPicked);
            Log.d(TAG, "URL CHOSEN: " + url);
        }
    }

    //double check the capitalizations and special characters
    public void updateBracketCompetitions(){
        bracketCompetitions = new ArrayList<String>(Arrays.asList("Debate", "Math Olympics", "MIST Quiz Bowl",
                "Sister’s Improv", "Brother’s Improv", "Sister’s Basketball", "Brother’s Basketball"));
    }

    //dummy values - need actual urls
    public void updateRulebookUrls(){
        rulebookUrls.put("Debate","1");
        rulebookUrls.put("Math Olympics","2");
        rulebookUrls.put("MIST Quiz Bowl","3");
        rulebookUrls.put("Sister’s Improv","4");
        rulebookUrls.put("Brother’s Improv","5");
        rulebookUrls.put("Sister’s Basketball","6");
        rulebookUrls.put("Brother’s Basketball","7");

        rulebookUrls.put("Knowledge Test - Book 1","8");
        rulebookUrls.put("Knowledge Test - Book 2","9");
        rulebookUrls.put("Knowledge Test - Book 3","a");
        rulebookUrls.put("Knowledge Test - Book 4","b");
        rulebookUrls.put("Sister’s Quran Level 1","c");
        rulebookUrls.put("Sister’s Quran Level 2","d");
        rulebookUrls.put("Sister’s Quran Level 3","e");
        rulebookUrls.put("Sister’s Quran Level 4","f");
        rulebookUrls.put("Brother’s Quran Level 1","g");
        rulebookUrls.put("Brother’s Quran Level 2","h");
        rulebookUrls.put("Brother’s Quran Level 3","i");
        rulebookUrls.put("Brother’s Quran Level 4","j");
        rulebookUrls.put("2D Art","k");
        rulebookUrls.put("3D Art","l");
        rulebookUrls.put("Fashion","m");
        rulebookUrls.put("Graphic","n");
        rulebookUrls.put("Photography","o");
        rulebookUrls.put("Culinary","p");

        rulebookUrls.put("Extemporaneous Essay","q");
        rulebookUrls.put("Extemporaneous Speaking","r");
        rulebookUrls.put("Original Oratory","s");
        rulebookUrls.put("Poetry","t");
        rulebookUrls.put("Prepared Essay","u");
        rulebookUrls.put("Short Fiction","v");
        rulebookUrls.put("poken Word","w");

        rulebookUrls.put("Business Venture","x");
        rulebookUrls.put("Nasheed/Rap","y");
        rulebookUrls.put("Community Service","z");
        rulebookUrls.put("Science Fair","~");
        rulebookUrls.put("Short Film","!");
        rulebookUrls.put("Social Media","@");
        rulebookUrls.put("Mobile Applications","#");


    }

    //dummy values - need actual urls
    public void updateBracketUrls() {
        bracketUrls.put("Debate", "1");
        bracketUrls.put("Math Olympics", "2");
        bracketUrls.put("MIST Quiz Bowl", "3");
        bracketUrls.put("Sister’s Improv", "4");
        bracketUrls.put("Brother’s Improv", "5");
        bracketUrls.put("Sister’s Basketball", "6");
        bracketUrls.put("Brother’s Basketball", "7");
    }
}