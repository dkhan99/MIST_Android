package com.mistapp.mistandroid;

/**
 * Created by aadil on 1/13/17.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.mistapp.mistandroid.R.id.brackets;

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
        getActivity().setTitle("Competitions");

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

        //default value is Mist Bowl (index = 14)
        competitionPicker.setSelection(14);

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
            Fragment fragment = new BracketFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Bundle args = new Bundle();
            args.putString("PARAM", url);
            fragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if (view == viewRulebookButton) {

            viewRulebookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String competitionPicked = competitionPicker.getSelectedItem().toString();
                    String url = rulebookUrls.get(competitionPicked);
                    Log.d(TAG, "URL CHOSEN: " + url);
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rulebookUrls.get(competitionPicked))));
                }
            });
        }
    }

    //double check the capitalizations and special characters
    public void updateBracketCompetitions(){
        bracketCompetitions = new ArrayList<String>(Arrays.asList("Debate", "Math Olympics", "MIST Bowl",
                "Sister’s Improv", "Brother’s Improv", "Sister’s Basketball", "Brother’s Basketball"));
    }

    //dummy values - need actual urlsl
    public void updateRulebookUrls(){
        rulebookUrls.put("Debate","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7dffbf629abc01d8d25f/1485667840236/Debate.pdf");
        rulebookUrls.put("Math Olympics","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d849dd1758e87f05fdb9b/1485669534042/Math+Olympics.pdf");
        rulebookUrls.put("MIST Bowl","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e3db3db2b428d8c2037/1485667901837/Mobile+Applications.pdf");
        rulebookUrls.put("Sister’s Improv","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e1e1b631b0e7cf6d19f/1485667870356/Improv.pdf");
        rulebookUrls.put("Brother’s Improv","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e1e1b631b0e7cf6d19f/1485667870356/Improv.pdf");
        rulebookUrls.put("Sister’s Basketball","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7dd603596e0fcbc0e989/1485667798080/Basketball.pdf");
        rulebookUrls.put("Brother’s Basketball","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7dd603596e0fcbc0e989/1485667798080/Basketball.pdf");

        rulebookUrls.put("Knowledge Tests - Book 1-4","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e242e69cf76b7569607/1485667876517/Knowledge+Tests.pdf");
        rulebookUrls.put("Quran Level 1-4","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d816303596e0fcbc10050/1485668708157/Quran.pdf");
        rulebookUrls.put("2D Art","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7db9bebafb21325bd50f/1485667770165/2D+Art.pdf");
        rulebookUrls.put("3D Art","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7dbfff7c501134731a67/1485667775113/3D+Art.pdf");
        rulebookUrls.put("Fashion","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e1146c3c4023d82d555/1485667858578/Fashion+Design.pdf");
        rulebookUrls.put("Graphic","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e183a0411d31b4498f9/1485667864863/Graphic+Design.pdf");
        rulebookUrls.put("Photography","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d808bb3db2b428d8c3015/1485668492248/Photography.pdf");
        rulebookUrls.put("Culinary","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7dee579fb35be4f27176/1485667822428/Culinary+Arts.pdf");

        rulebookUrls.put("Extemporaneous Essay","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e068419c2ec3fd8bd4f/1485667846417/Extemporaneous+Essay.pdf");
        rulebookUrls.put("Extemporaneous Speaking","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e0c1e5b6c3aa8039c3e/1485667852801/Extemporaneous+Speaking.pdf");
        rulebookUrls.put("Original Oratory","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e4f46c3c4023d82d703/1485667919539/Original+Oratory.pdf");
        rulebookUrls.put("Poetry","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d80a1e4fcb5a6cdc37e95/1485668514129/Poetry+Literature.pdf");
        rulebookUrls.put("Prepared Essay","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d814f5016e177f5435b4a/1485668687867/Prepared+Essay.pdf");
        rulebookUrls.put("Short Fiction","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d8190f7e0ab9c094d4f03/1485668753085/Short+Fiction.pdf");
        rulebookUrls.put("Spoken Word","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d80e9be6594f328541a31/1485668586010/Poetry+Spoken+Word.pdf");

        rulebookUrls.put("Business Venture","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7dda86e6c04b27522ee4/1485667802939/Business+Venture.pdf");
        rulebookUrls.put("Nasheed/Rap","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e481e5b6c3aa8039de4/1485667912453/Nasheed+and+Rap.pdf");
        rulebookUrls.put("Community Service","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7ddfe6f2e152d3e246ae/1485667807443/Community+Service.pdf");
        rulebookUrls.put("Science Fair","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d8173725e2506f4bbf4c1/1485668724184/Science+Fair.pdf");
        rulebookUrls.put("Short Film","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d81ada5790aa54e5d75d3/1485668781566/Short+Film.pdf");
        rulebookUrls.put("Social Media","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d81bcf7e0ab9c094d4ff7/1485668796254/Social+Media.pdf");
        rulebookUrls.put("Mobile Applications","http://docs.google.com/gview?embedded=true&url=static1.squarespace.com/static/5670ede7a976af3e2f3af0af/t/588d7e3db3db2b428d8c2037/1485667901837/Mobile+Applications.pdf");
    }

    //dummy values - need actual urls
    public void updateBracketUrls() {
        bracketUrls.put("Debate", "Debate");
        bracketUrls.put("Math Olympics", "Math Olympics");
        bracketUrls.put("MIST Bowl", "MIST Bowl");
        bracketUrls.put("Sister’s Improv", "Sister's Improv");
        bracketUrls.put("Brother’s Improv", "Brother's Improv");
        bracketUrls.put("Sister’s Basketball", "Sister's Basketball");
        bracketUrls.put("Brother’s Basketball", "Brother's Basketball");
    }

    @Override
    public void onResume(){
        super.onResume();
        BottomBar bbar = ((MyMistActivity)getActivity()).getBottomBar();
        bbar.selectTabAtPosition(1);
    }

}