package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Teammate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyTeamFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private TextView nameText;
    private TextView teamNameText;
    private TextView emailText;
    private TextView mistIdText;
    private SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private String userTeamName;
    private String userMistId;

    ListView coaches_lv;
    ListView teammates_lv;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_team, container, false);

        sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        coaches_lv = (ListView)view.findViewById(R.id.coaches_list);
        teammates_lv = (ListView)view.findViewById(R.id.teammates_list);

        nameText = (TextView)view.findViewById(R.id.user_name);
        teamNameText = (TextView)view.findViewById(R.id.team_name);
        mistIdText = (TextView)view.findViewById(R.id.mist_id);
        emailText = (TextView)view.findViewById(R.id.email_address);

        AdapterView.OnItemClickListener listener = createItemClickListener();

        coaches_lv.setOnItemClickListener(listener);
        teammates_lv.setOnItemClickListener(listener);

        setUserProfile();


        //get from currentUser's information in shared prefs
//        final String teamName1 = "FoCo - South Forsyth";
//        final String userMistId1 = "3198-35252";

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("team");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean teamExists = false;
                ArrayList<Teammate> coachList = new ArrayList<Teammate>();
                ArrayList<Teammate> teammateList = new ArrayList<Teammate>();

                DataSnapshot currentTeammateSnapshot = dataSnapshot.child(userTeamName);
                HashMap<String, HashMap> teamMap = (HashMap<String, HashMap>) currentTeammateSnapshot.getValue();
                Iterator it = teamMap.entrySet().iterator();

                //go through each teammate in the current team
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    String mistId = (String)pair.getKey();

                    //if current teammate is not the current user that is logged in...
                    //create a Teammate object and add to corresponding list (coaches or teammates)
                    if (!mistId.equals(userMistId)){
                        Teammate currentMate = currentTeammateSnapshot.child((String)pair.getKey()).getValue(Teammate.class);

                        if (currentMate.getIsCompetitor() == 1){
                            teammateList.add(currentMate);
                        }
                        else{
                            coachList.add(currentMate);
                        }
                        Log.d(TAG, "Adding Teammate with mistid: " + mistId + " name: " + currentMate.getName() + " phone: " + currentMate.getPhoneNumber() + " iscompetitor: " + currentMate.getIsCompetitor());
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }

                MyTeamAdapter coachesAdapter = new MyTeamAdapter(getActivity(), coachList);
                MyTeamAdapter teammatesAdapter = new MyTeamAdapter(getActivity(), teammateList);
                ListView coaches_lv = (ListView)view.findViewById(R.id.coaches_list);
                ListView teammates_lv = (ListView)view.findViewById(R.id.teammates_list);
                coaches_lv.setAdapter(coachesAdapter);
                teammates_lv.setAdapter(teammatesAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        return view;
    }

    public AdapterView.OnItemClickListener createItemClickListener(){
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProgressDialog progress = new ProgressDialog(getActivity());
                progress.setMessage("Opening call :) ");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                Log.d(TAG, "Clicked a teammate");
                Teammate clickedTeammate = (Teammate)parent.getItemAtPosition(position);
                long phoneNumber = clickedTeammate.getPhoneNumber();
                Uri number = Uri.parse("tel:"+phoneNumber);
                Log.d(TAG, "phonenumber: " + phoneNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                getActivity().startActivity(callIntent);
            }
        };
        return listener;
    }

    public void setUserProfile(){

        Gson gson = new Gson();
        String userType = sharedPref.getString(getString(R.string.current_user_type), "competitor");

        if (userType.equals("competitor")){
            String json = sharedPref.getString(getString(R.string.current_user_key), "asdf");

            Log.d(TAG, "CurrentUser from cache: " + json);
            Competitor currentUser = gson.fromJson(json, Competitor.class);
            nameText.setText(currentUser.getName());
            teamNameText.setText(currentUser.getTeam());
            mistIdText.setText(currentUser.getMistId());
            emailText.setText(currentUser.getEmail());
            //sets variables
            userTeamName = currentUser.getTeam();
            userMistId = currentUser.getMistId();
        }

        else if (userType.equals("coach")){
            String json = sharedPref.getString(getString(R.string.current_user_key), "");
            Coach currentUser = gson.fromJson(json, Coach.class);
            nameText.setText(currentUser.getName());
            teamNameText.setText(currentUser.getTeam());
            mistIdText.setText(currentUser.getMistId());
            emailText.setText(currentUser.getEmail());
            //sets variables
            userTeamName = currentUser.getTeam();
            userMistId = currentUser.getMistId();
        }
    }

}