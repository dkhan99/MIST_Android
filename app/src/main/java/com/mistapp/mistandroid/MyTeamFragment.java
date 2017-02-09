package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Event;
import com.mistapp.mistandroid.model.Notification;
import com.mistapp.mistandroid.model.Teammate;
import com.mistapp.mistandroid.model.User;
import com.roughike.bottombar.BottomBar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mistapp.mistandroid.R.id.uid;

public class MyTeamFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private TextView nameText;
    private TextView teamNameText;
    private TextView emailText;
    private TextView mistIdText;
    private TextView coachText;
    private CacheHandler cacheHandler;

    private String userTeamName;
    private String userMistId;

    LinearLayout coachLayout;
    LinearLayout teammateLayout;
    LinearLayout coachTeamLayout;

    ListView coaches_lv;
    ListView teammates_lv;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_team, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        coachLayout = (LinearLayout)view.findViewById(R.id.coaches_layout);
        teammateLayout = (LinearLayout)view.findViewById(R.id.teammates_layout);
        coachTeamLayout = (LinearLayout)view.findViewById(R.id.coach_team_layout);

        cacheHandler = CacheHandler.getInstance(getActivity().getApplication(), sharedPref, editor);
        coaches_lv = (ListView)view.findViewById(R.id.coaches_list);
        teammates_lv = (ListView)view.findViewById(R.id.teammates_list);

        nameText = (TextView)view.findViewById(R.id.user_name);
        teamNameText = (TextView)view.findViewById(R.id.team_name);
        mistIdText = (TextView)view.findViewById(R.id.mist_id);
        emailText = (TextView)view.findViewById(R.id.email_address);

        coachText = (TextView)view.findViewById(R.id.coaches_text);

        AdapterView.OnItemClickListener listener = createItemClickListener();

        coaches_lv.setOnItemClickListener(listener);
        teammates_lv.setOnItemClickListener(listener);

        setUserProfile();

        String teammates = cacheHandler.getCachedTeammatesJson();

        Gson gson = new Gson();

        //teammates are not cached already
        if (teammates.equals("")){
            Log.d(TAG, "teammates are not cached. getting from db, and adding to cache");
            //get teammates from database -> populate view, and add to cache
            mDatabase = FirebaseDatabase.getInstance().getReference();
            ref = mDatabase.child(getResources().getString(R.string.firebase_team_table));

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean teamExists = false;
                    //replaces hashtags and periods
                    String teamNameInDatabase = userTeamName.replaceAll("[#.]", "_");
                    DataSnapshot currentTeammateSnapshot = dataSnapshot.child(teamNameInDatabase);

                    HashMap<String, HashMap> teamMap = (HashMap<String, HashMap>) currentTeammateSnapshot.getValue();
                    Iterator it = teamMap.entrySet().iterator();
                    ArrayList<Teammate> coachList = new ArrayList<Teammate>();
                    ArrayList<Teammate> teammateList = new ArrayList<Teammate>();
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

                    ArrayList<Teammate> allTeammates = new ArrayList<Teammate>();
                    allTeammates.addAll(coachList);
                    allTeammates.addAll(teammateList);

                    cacheHandler.cacheTeammates(allTeammates);
                    cacheHandler.commitToCache();

                    //sorts teammates by name
                    Collections.sort(teammateList, Teammate.TeammateNameComparator);
                    Collections.sort(coachList, Teammate.TeammateNameComparator);

                    if (coachList.size() == 0){
                        removeCoachLayout();
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
        }

        //teammates were cached - need to retreive
        else{
            Log.d(TAG, "teammates are cached. Getting from cache");
            String jsonList = cacheHandler.getCachedTeammatesJson();
            ArrayList<Teammate> coachList = new ArrayList<Teammate>();
            ArrayList<Teammate> teammateList = new ArrayList<Teammate>();
            ArrayList<Teammate> allTeammates = gson.fromJson(jsonList, new TypeToken<ArrayList<Teammate>>() {}.getType());
            for (Teammate currentTeammate : allTeammates){
                if (currentTeammate.getIsCompetitor() == 1){
                    teammateList.add(currentTeammate);
                }
                else{
                    coachList.add(currentTeammate);
                }
            }
            
            //sorts teammates by name
            Collections.sort(teammateList, Teammate.TeammateNameComparator);
            Collections.sort(coachList, Teammate.TeammateNameComparator);

            if (coachList.size() == 0){
                removeCoachLayout();
            }

            MyTeamAdapter coachesAdapter = new MyTeamAdapter(getActivity(), coachList);
            MyTeamAdapter teammatesAdapter = new MyTeamAdapter(getActivity(), teammateList);
            ListView coaches_lv = (ListView)view.findViewById(R.id.coaches_list);
            ListView teammates_lv = (ListView)view.findViewById(R.id.teammates_list);
            coaches_lv.setAdapter(coachesAdapter);
            teammates_lv.setAdapter(teammatesAdapter);
        }


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

    //removes coach text + listview -> this method is called when there are no (other) coaches on the team
    private void removeCoachLayout(){
        Log.d(TAG, "there are no coaches - removing coaches view");
        coachText.setVisibility(View.GONE);
        coaches_lv.setVisibility(View.GONE);
        coachTeamLayout.setWeightSum(1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)coachLayout.getLayoutParams();
        params.weight = 0;
        coachLayout.setLayoutParams(params);
    }


    public void setUserProfile(){

        Gson gson = new Gson();

        String userType = cacheHandler.getUserType();

        if (userType.equals("competitor")){
            String json = cacheHandler.getUserJson();
            Log.d(TAG, "CurrentUser from cache: " + json);

            if (json == null){
                Log.d(TAG, "LOGGING USER OUT");
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "peace out ", Toast.LENGTH_LONG).show();

                //remove user, user uid, user's type, teammates, and notifications from the cache
                cacheHandler.removeCachedUserFields();
                cacheHandler.removeCachedNotificationFields();
                cacheHandler.removeCachedEvents();
                cacheHandler.removeCachedTeammates();
                cacheHandler.commitToCache();
                Intent i= new Intent(getActivity().getApplicationContext(), WelcomeActivity.class);
                getActivity().startActivity(i);
                return;
            }

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
            String json = cacheHandler.getUserJson();
            Log.d(TAG, "CurrentUser from cache: " + json);

            if (json == null){
                Log.d(TAG, "LOGGING USER OUT");
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "peace out ", Toast.LENGTH_LONG).show();

                //remove user, user uid, user's type, teammates, and notifications from the cache
                cacheHandler.removeCachedUserFields();
                cacheHandler.removeCachedNotificationFields();
                cacheHandler.removeCachedEvents();
                cacheHandler.removeCachedTeammates();
                cacheHandler.commitToCache();
                Intent i= new Intent(getActivity().getApplicationContext(), WelcomeActivity.class);
                getActivity().startActivity(i);
                return;
            }

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