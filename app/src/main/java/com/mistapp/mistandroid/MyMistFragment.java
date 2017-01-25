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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Teammate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mistapp.mistandroid.R.id.uid;

public class MyMistFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = LogInAuth.class.getSimpleName();
    ListView coaches_lv;
    ListView teammates_lv;
    FragmentTransaction transaction;
    private TextView logoutText;
    private CacheHandler cacheHandler;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_mist, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getActivity().getApplication(), sharedPref, editor);

        logoutText = (TextView) view.findViewById(R.id.logout);
        logoutText.setOnClickListener(this);
        RadioButton myTeamButton = (RadioButton) view.findViewById(R.id.my_team_button);
        RadioButton myScheduleButton = (RadioButton) view.findViewById(R.id.my_schedule_button);
        final MyTeamFragment myTeamFragment = new MyTeamFragment();
        final MyScheduleFragment myScheduleFragment = new MyScheduleFragment();

        showMyTeamFragment(myTeamFragment);
        Log.d(TAG, "Mah Niga");
        myTeamButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true){
                    showMyTeamFragment(myTeamFragment);
                }
            }
        });
        myScheduleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    showMyScheduleFragment(myScheduleFragment);
                }
            }
        });
        return view;
    }

    public void showMyTeamFragment(MyTeamFragment fragment){
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.my_mist_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showMyScheduleFragment(MyScheduleFragment fragment){
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.my_mist_frame_layout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * If button or textview is clicked, then we go to the next activity after verifying data
     * @param view
     */
    @Override
    public void onClick(View view) {

        if (view == logoutText) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "peace out ", Toast.LENGTH_LONG).show();

            Gson gson = new Gson();
            String json = cacheHandler.getUserJson();
            String currentUserType = cacheHandler.getUserType();

            //remove user, user uid, user's type, teammates, and notifications from the cache
            cacheHandler.removeCachedUserFields();
            cacheHandler.removeCachedNotificationFields();
            cacheHandler.removeCachedTeammates();
            cacheHandler.commitToCache();

            //unsibscribing from topics that were previously subscribed to when logged in
            if (currentUserType.equals("coach")){
                Coach currentUser = gson.fromJson(json, Coach.class);
                unSubscribeFromCoachTopics(currentUser);
            }
            else if(currentUserType.equals("competitor")){
                Competitor currentUser = gson.fromJson(json, Competitor.class);
                unSubscribeFromCompetitorTopics(currentUser);
            }

            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
        }
    }


    //subscribe to team name, competitions, and "competitor"
    public void unSubscribeFromCompetitorTopics(Competitor currentUser){

        //subscribe to the current user's team name (replaces spaces with underscores in team name)
        String teamName = currentUser.getTeam();
        String underScoreTeamName = teamName.replaceAll(" ", "_");
        FirebaseMessaging.getInstance().unsubscribeFromTopic(underScoreTeamName);

        //subscribe to the current user type
        FirebaseMessaging.getInstance().unsubscribeFromTopic("competitor");

        String[] compArray = {
                (currentUser).getGroupProject(),
                (currentUser).getArt(),
                (currentUser).getSports(),
                (currentUser).getBrackets(),
                (currentUser).getKnowledge()
        };

        //subscribe to user's competitions (replaces spaces with underscores in competition name)
        for (String competition: compArray){
            if (!competition.equals("")) {
                String underScoreCompName = competition.replaceAll(" ", "_");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(underScoreCompName);
            }
        }
    }

    //suscribes to team name and "coach"
    public void unSubscribeFromCoachTopics(Coach currentCoach){

        //subscribe to the current user's team name (replaces spaces with underscores in team name)
        String teamName = currentCoach.getTeam();
        String underScoreTeamName = teamName.replaceAll(" ", "_");
        FirebaseMessaging.getInstance().unsubscribeFromTopic(underScoreTeamName);

        //subscribe to the current user type
        FirebaseMessaging.getInstance().unsubscribeFromTopic("coach");

    }


}