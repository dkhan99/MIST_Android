package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.TreeSet;

import static com.mistapp.mistandroid.R.id.uid;

public class MyTeamFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    private TextView nameText;
    private TextView teamNameText;
    private TextView emailText;
    private TextView mistIdText;

    private ProgressBar progressBar;

    private CacheHandler cacheHandler;

    private String userTeamName;
    private String userMistId;

    private TextView noTeammatesText;


    ListView myTeamList;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_team, container, false);


        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        final TeamAdapter adapter = new TeamAdapter();

        progressBar = (ProgressBar) view.findViewById(R.id.team_progress);

        myTeamList = (ListView)view.findViewById(R.id.my_team_list);

        noTeammatesText = (TextView)view.findViewById(R.id.no_teammates_text);

        cacheHandler = CacheHandler.getInstance(getActivity().getApplication(), sharedPref, editor);

        nameText = (TextView)view.findViewById(R.id.user_name);
        teamNameText = (TextView)view.findViewById(R.id.team_name);
        mistIdText = (TextView)view.findViewById(R.id.mist_id);
        emailText = (TextView)view.findViewById(R.id.email_address);

        AdapterView.OnItemClickListener listener = createItemClickListener();

        myTeamList.setOnItemClickListener(listener);

        setUserProfile();

        String teammates = cacheHandler.getCachedTeammatesJson();

        Gson gson = new Gson();

        progressBar.setVisibility(View.VISIBLE);

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

                    addToAdapter(adapter, teammateList, coachList, teammateList.size(), coachList.size());
                    ListView teammates_lv = (ListView)view.findViewById(R.id.my_team_list);
                    progressBar.setVisibility(View.GONE);
                    teammates_lv.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    progressBar.setVisibility(View.GONE);
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
                //capitalizing first letter in the name
                String origName = currentTeammate.getName();
                if (origName!=null && origName.length()>0) {
                    currentTeammate.setName(origName.substring(0, 1).toUpperCase() + origName.substring(1));
                }
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


            addToAdapter(adapter, teammateList, coachList, teammateList.size(), coachList.size());
            ListView teammates_lv = (ListView)view.findViewById(R.id.my_team_list);
            progressBar.setVisibility(View.GONE);
            teammates_lv.setAdapter(adapter);

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

    //adds event items and separater items to the adapter
    public void addToAdapter(MyTeamFragment.TeamAdapter adapter, ArrayList<Teammate>teammateArrayList, ArrayList<Teammate>coachArrayList, int numTeammates, int numCoaches) {
        //sort array and add items and separator items to adapter
        for (int x = 0; x < teammateArrayList.size(); x++) {
            Log.d("TAG", "NAME: " + teammateArrayList.get(x).toString());
        }


        if (numCoaches != 0) {
            adapter.addSeparatorItem("Coaches", 0);
            for (int x = 0; x < coachArrayList.size(); x++) {
                adapter.addItem(coachArrayList.get(x));
            }
        }
        if (numTeammates != 0) {
            int indexToAdd = 0;
            if (numCoaches!=0){
                indexToAdd += coachArrayList.size();
            }
            adapter.addSeparatorItem("Teammates", indexToAdd);
            for (int x = 0; x < teammateArrayList.size(); x++) {
                adapter.addItem(teammateArrayList.get(x));
            }
        }

        if (numCoaches == 0 && numTeammates == 0) {
            noTeammatesText.setVisibility(View.VISIBLE);
        }

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

    //format the number with area code + country code
    private String getFormattedPhoneNumber(long number){
        String phoneNumber = Long.toString(number);
        if (phoneNumber.length() == 10){
            phoneNumber = ( "(" + phoneNumber.substring(0,3) + ")-" + phoneNumber.substring(3,6) + "-" + phoneNumber.substring(6,9));
        }
        else if (phoneNumber.length() == 11){
            phoneNumber = ( "+" + phoneNumber.charAt(0) + "(" + phoneNumber.substring(1,4) + ")-" + phoneNumber.substring(4,7) + "-" + phoneNumber.substring(7,10));
        }
        //else ->  keep phone number as is
        return phoneNumber;

    }

    private class TeamAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<Object> mData = new ArrayList<Object>();
        private LayoutInflater mInflater;

        private TreeSet mSeparatorsSet = new TreeSet();

        public TeamAdapter() {
            mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final Teammate teammate) {
            Log.d("123123", "adding item" + teammate.getName());
            mData.add(teammate);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item, int indexOfAddition) {
            mData.add(indexOfAddition, item);
            // save separator position
            mSeparatorsSet.add(indexOfAddition);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
            int type = getItemViewType(position);

            switch(type){
                case TYPE_ITEM:
                    TeamViewHolder holder1;

                    if (convertView == null) {
                        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = vi.inflate(R.layout.item_team_member, parent, false);
                        holder1 = new TeamViewHolder();
                        holder1.nameText = (TextView) convertView.findViewById(R.id.teammember_name);
                        holder1.phoneNumberText = (TextView) convertView.findViewById(R.id.teammember_phone);

                        convertView.setTag(holder1);
                    }
                    else {
                        holder1 = (TeamViewHolder) convertView.getTag();
                    }

                    Object myItem = mData.get(position);
                    if (myItem instanceof Teammate) {
                        // set up the list item
                        if (myItem != null) {
                            // set item text
                            Log.d(TAG, "myitem is NOT null!!");
                            if (holder1.nameText != null) {
                                holder1.nameText.setText(((Teammate) myItem).getName());
                            }
                            if (holder1.phoneNumberText != null){
                                holder1.phoneNumberText.setText(getFormattedPhoneNumber(((Teammate) myItem).getPhoneNumber()));
                            }
                        }
                        else{
                            Log.d(TAG, "myitem is null!!");
                        }
                    }

                    // return the created view
                    return convertView;

                case TYPE_SEPARATOR:
                    MyTeamFragment.TitleViewHolder holder2;

                    if (convertView == null) {
                        LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = vi.inflate(R.layout.item_day_header, parent, false);
                        holder2 = new TitleViewHolder();
                        holder2.teammateTypeText = (TextView) convertView.findViewById(R.id.event_day_header);
                        convertView.setTag(holder2);
                    }
                    else {
                        holder2 = (TitleViewHolder) convertView.getTag();
                    }

                    Object myItem1 = mData.get(position);
                    if (myItem1 instanceof String) {
                        // set up the list item
                        if (myItem1 != null) {
                            // set item text
                            if (holder2.teammateTypeText != null) {
                                holder2.teammateTypeText.setText(((String) myItem1));
                            }
                        }
                    }


                    // return the created view
                    return convertView;
            }

            return convertView;
        }

    }

    public static class TeamViewHolder {
        public TextView nameText;
        public TextView phoneNumberText;
    }

    public static class TitleViewHolder {
        public TextView teammateTypeText; //coach or teammate
    }

}