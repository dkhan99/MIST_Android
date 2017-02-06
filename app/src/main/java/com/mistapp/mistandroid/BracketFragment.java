package com.mistapp.mistandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BracketFragment extends Fragment {

    private RecyclerView recycler1, recycler2, recycler3, recycler4, recycler5;
    private TextView r1Text, r2Text, r3Text, r4Text, r5Text;
    private FirebaseRecyclerAdapter<Bracket, PersonHolder> mAdapter, mAdapter2, mAdapter3, mAdapter4, mAdapter5;
    private String competition;
    private DatabaseReference ref, mref, mreff, mrefff, mreffff;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bracket, container, false);

        TextView bracketTitle = (TextView)view.findViewById(R.id.brackets_title);

        r1Text = (TextView) view.findViewById(R.id.round1_text);
        r2Text = (TextView) view.findViewById(R.id.round2_text);
        r3Text = (TextView) view.findViewById(R.id.round3_text);
        r4Text = (TextView) view.findViewById(R.id.round4_text);
        r5Text = (TextView) view.findViewById(R.id.round5_text);

        competition = getArguments().getString("PARAM");
        bracketTitle.setText(competition + " Bracket");

        Log.d("`````", "competition chosen is: " + competition);
        recycler1 = (RecyclerView) view.findViewById(R.id.recycler);
        recycler2 = (RecyclerView) view.findViewById(R.id.recycler_1);
        recycler3 = (RecyclerView) view.findViewById(R.id.recycler_2);
        recycler4 = (RecyclerView) view.findViewById(R.id.recycler_3);
        recycler5 = (RecyclerView) view.findViewById(R.id.recycler_4);

        ref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.firebase_bracket_table)).child(competition).child("round-one");

        //recycler1.setHasFixedSize(true);
        recycler1.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecorations = new DividerItemDecoration(recycler1.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler1.addItemDecoration(dividerItemDecorations);
        Log.d("TAG", "we got here");

        mAdapter = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, R.layout.item_bracket, PersonHolder.class, ref) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setTeamNameField(bracket.getTeamName());
                ViewHolder.setBuildingField(bracket.getBuilding());
                ViewHolder.setRoomField(bracket.getRoom());
                ViewHolder.setDayField(bracket.getDay());
                ViewHolder.setTimeField(bracket.getTime());
            }
        };
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                changeRoundName(mAdapter.getItemCount(), r1Text);
            }
        });
        recycler1.setAdapter(mAdapter);

        //recycler2.setHasFixedSize(true);
        recycler2.setLayoutManager(new LinearLayoutManager(getContext()));
        dividerItemDecorations = new DividerItemDecoration(recycler2.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler2.addItemDecoration(dividerItemDecorations);
        mref = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.firebase_bracket_table)).child(competition).child("round-two");

        mAdapter2 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, R.layout.item_bracket, PersonHolder.class, mref) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setTeamNameField(bracket.getTeamName());
                ViewHolder.setBuildingField(bracket.getBuilding());
                ViewHolder.setRoomField(bracket.getRoom());
                ViewHolder.setDayField(bracket.getDay());
                ViewHolder.setTimeField(bracket.getTime());
            }
        };
        mAdapter2.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                changeRoundName(mAdapter2.getItemCount(), r2Text);
            }
        });
        recycler2.setAdapter(mAdapter2);

        //recycler3.setHasFixedSize(true);
        recycler3.setLayoutManager(new LinearLayoutManager(getContext()));
        dividerItemDecorations = new DividerItemDecoration(recycler3.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler3.addItemDecoration(dividerItemDecorations);
        mreff = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.firebase_bracket_table)).child(competition).child("round-three");

        mAdapter3 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, R.layout.item_bracket, PersonHolder.class, mreff) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setTeamNameField(bracket.getTeamName());
                ViewHolder.setBuildingField(bracket.getBuilding());
                ViewHolder.setRoomField(bracket.getRoom());
                ViewHolder.setDayField(bracket.getDay());
                ViewHolder.setTimeField(bracket.getTime());
            }
        };
        mAdapter3.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                changeRoundName(mAdapter3.getItemCount(), r3Text);
            }
        });
        recycler3.setAdapter(mAdapter3);

        //recycler4.setHasFixedSize(true);
        recycler4.setLayoutManager(new LinearLayoutManager(getContext()));
        dividerItemDecorations = new DividerItemDecoration(recycler4.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler4.addItemDecoration(dividerItemDecorations);
        mrefff = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.firebase_bracket_table)).child(competition).child("round-four");

        mAdapter4 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, R.layout.item_bracket, PersonHolder.class, mrefff) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setTeamNameField(bracket.getTeamName());
                ViewHolder.setBuildingField(bracket.getBuilding());
                ViewHolder.setRoomField(bracket.getRoom());
                ViewHolder.setDayField(bracket.getDay());
                ViewHolder.setTimeField(bracket.getTime());
            }
        };
        mAdapter4.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                changeRoundName(mAdapter4.getItemCount(), r4Text);
            }
        });
        recycler4.setAdapter(mAdapter4);


        //recycler4.setHasFixedSize(true);
        recycler5.setLayoutManager(new LinearLayoutManager(getContext()));
        dividerItemDecorations = new DividerItemDecoration(recycler5.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler5.addItemDecoration(dividerItemDecorations);
        mreffff = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.firebase_bracket_table)).child(competition).child("round-five");

        mAdapter5 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, R.layout.item_bracket, PersonHolder.class, mreffff) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setTeamNameField(bracket.getTeamName());
                ViewHolder.setBuildingField(bracket.getBuilding());
                ViewHolder.setRoomField(bracket.getRoom());
                ViewHolder.setDayField(bracket.getDay());
                ViewHolder.setTimeField(bracket.getTime());
            }
        };
        mAdapter5.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                changeRoundName(mAdapter5.getItemCount(), r5Text);
            }
        });
        recycler5.setAdapter(mAdapter5);

        return view;
    }

    //set text based on how many teams are in each. If this method is called, the round exists -> set visibility to visible (default = invisible)
    private void changeRoundName(int itemCount, TextView currentRoundText){
        currentRoundText.setVisibility(View.VISIBLE);

        if (itemCount >= 5 && itemCount <= 8){
            currentRoundText.setText("Quarter-Finals");
        }
        else if (itemCount >= 3 && itemCount <= 4){
            currentRoundText.setText("Semi-Finals");
        }
        else if (itemCount == 2){
            currentRoundText.setText("Finals");
        }
        else if (itemCount == 1){
            currentRoundText.setText("Champions");
        }
        //if itemCount > 9, round name stays as is (eg: "Round 1")
    }


    public static class Bracket {
        private String teamName;
        private String room;
        private String building;
        private String day;
        private String time;

        public Bracket(String teamName, String building, String room, String day, String time) {
            this.teamName = teamName;
            this.building = building;
            this.room = room;
            this.day = day;
            this.time = time;
        }
        public Bracket() {
        }

        public String getRoom() {
            return room;
        }
        public String getBuilding() {
            return building;
        }
        public String getDay() {
            return day;
        }
        public String getTime() {
            return time;
        }
        public String getTeamName() {
            return teamName;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public static class PersonHolder extends RecyclerView.ViewHolder {
        private final TextView teamNameField;
        private final TextView buildingField;
        private final TextView roomField;
        private final TextView dayField;
        private final TextView timeField;

        public PersonHolder(View itemView) {
            super(itemView);
            teamNameField = (TextView) itemView.findViewById(R.id.bracket_team_name);
            buildingField = (TextView) itemView.findViewById(R.id.bracket_building);
            roomField = (TextView) itemView.findViewById(R.id.bracket_room);
            dayField = (TextView) itemView.findViewById(R.id.bracket_day);
            timeField = (TextView) itemView.findViewById(R.id.bracket_time);
        }
        public void setTeamNameField(String teamName) {
            teamNameField.setText(teamName);
        }
        public void setBuildingField(String building) {
            buildingField.setText(building);
        }
        public void setRoomField(String room) {
            roomField.setText(room);
        }
        public void setDayField(String day) {
            dayField.setText(day);
        }
        public void setTimeField(String time) {
            timeField.setText(time);
        }
    }
}

