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


public class Brackets extends Fragment {

    private RecyclerView recycler1, recycler2, recycler3, recycler4;
    private FirebaseRecyclerAdapter<Bracket, PersonHolder> mAdapter, mAdapter2, mAdapter3, mAdapter4;
    private String competition;
    private DatabaseReference ref, mref, mreff, mrefff;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bracket, container, false);
        competition = getArguments().getString("PARAM");
        recycler1 = (RecyclerView) view.findViewById(R.id.recycler);
        recycler2 = (RecyclerView) view.findViewById(R.id.recycler_1);
        recycler3 = (RecyclerView) view.findViewById(R.id.recycler_2);
        recycler4 = (RecyclerView) view.findViewById(R.id.recycler_3);
        ref = FirebaseDatabase.getInstance().getReference().child("bracket").child(competition).child("round-one");

        //recycler1.setHasFixedSize(true);
        recycler1.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler1.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler1.addItemDecoration(dividerItemDecoration);
        Log.d("TAG", "we got here");

        mAdapter = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, android.R.layout.two_line_list_item, PersonHolder.class, ref) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setName(bracket.getName());
                ViewHolder.setText(bracket.getRoom());
            }
        };
        recycler1.setAdapter(mAdapter);

        //recycler2.setHasFixedSize(true);
        recycler2.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        DividerItemDecoration dividerItemDecorations = new DividerItemDecoration(recycler2.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler2.addItemDecoration(dividerItemDecorations);
        mref = FirebaseDatabase.getInstance().getReference().child("bracket").child(competition).child("round-one");

        mAdapter2 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, android.R.layout.two_line_list_item, PersonHolder.class, mref) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setName(bracket.getName());
                ViewHolder.setText(bracket.getRoom());
            }
        };
        recycler2.setAdapter(mAdapter2);

        //recycler3.setHasFixedSize(true);
        recycler3.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        DividerItemDecoration dividerItemDecorationz = new DividerItemDecoration(recycler3.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler3.addItemDecoration(dividerItemDecorationz);
        mreff = FirebaseDatabase.getInstance().getReference().child("bracket").child(competition).child("round-two");

        mAdapter3 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, android.R.layout.two_line_list_item, PersonHolder.class, mreff) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setName(bracket.getName());
                ViewHolder.setText(bracket.getRoom());
            }
        };
        recycler3.setAdapter(mAdapter3);

        //recycler4.setHasFixedSize(true);
        recycler4.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        DividerItemDecoration dividerItemDecorationzs = new DividerItemDecoration(recycler4.getContext(),
                (new LinearLayoutManager(getActivity())).getOrientation());
        recycler4.addItemDecoration(dividerItemDecorationzs);
        mrefff = FirebaseDatabase.getInstance().getReference().child("bracket").child(competition).child("round-two");

        mAdapter4 = new FirebaseRecyclerAdapter<Bracket, PersonHolder>(Bracket.class, android.R.layout.two_line_list_item, PersonHolder.class, mrefff) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Bracket bracket, int position) {
                ViewHolder.setName(bracket.getName());
                ViewHolder.setText(bracket.getRoom());

            }
        };
        recycler4.setAdapter(mAdapter4);


        return view;
    }


    public static class Bracket {
        private String name;
        private String room;
        public Bracket(String name, String room) {
            this.name = name;
            this.room = room;
        }
        public Bracket() {
        }
        public String getRoom() {
            return room;
        }
        public String getName() {
            return name;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public static class PersonHolder extends RecyclerView.ViewHolder {
        private final TextView mNameField;
        private final TextView mTextField;

        public PersonHolder(View itemView) {
            super(itemView);
            mNameField = (TextView) itemView.findViewById(android.R.id.text1);
            mTextField = (TextView) itemView.findViewById(android.R.id.text2);
        }
        public void setName(String name) {
            mNameField.setText(name);
        }
        public void setText(String text) {
            mTextField.setText(text);
        }
    }
}

