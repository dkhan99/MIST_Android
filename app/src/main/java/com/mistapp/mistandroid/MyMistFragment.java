package com.mistapp.mistandroid;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mistapp.mistandroid.model.Teammate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.mistapp.mistandroid.R.id.uid;

public class MyMistFragment extends Fragment {

    private static final String TAG = LogInAuth.class.getSimpleName();
    private DatabaseReference mDatabase;
    private DatabaseReference ref;
    View view;
    ListView coaches_lv;
    ListView teammates_lv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_mist, container, false);

        coaches_lv = (ListView)view.findViewById(R.id.coaches_list);
        teammates_lv = (ListView)view.findViewById(R.id.teammates_list);

        AdapterView.OnItemClickListener listener = createItemClickListener();

        coaches_lv.setOnItemClickListener(listener);
        teammates_lv.setOnItemClickListener(listener);

        //get from currentUser's information in shared prefs
        final String teamName = "FoCo - South Forsyth";
        final String userMistId = "3198-35252";

        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref = mDatabase.child("team");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean teamExists = false;
                ArrayList<Teammate> coachList = new ArrayList<Teammate>();
                ArrayList<Teammate> teammateList = new ArrayList<Teammate>();

                DataSnapshot currentTeammateSnapshot = dataSnapshot.child(teamName);
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



}