package com.mistapp.mistandroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;

public class Team extends ActionBarActivity {

    private ListView recycler1, recycler2;
    private FirebaseRecyclerAdapter<Coach, PersonHolder> mAdapter;
    private FirebaseRecyclerAdapter<Competitor, PersonHolder> mAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        RecyclerView recycler1 = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView recycler2 = (RecyclerView) findViewById(R.id.recycler_1);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("coaches-data");
        recycler1.setHasFixedSize(true);
        recycler1.setLayoutManager(new LinearLayoutManager(this));
        recycler1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler1.getContext(),
                (new LinearLayoutManager(this)).getOrientation());
        recycler1.addItemDecoration(dividerItemDecoration);
        mAdapter = new FirebaseRecyclerAdapter<Coach, PersonHolder>(Coach.class, android.R.layout.two_line_list_item, PersonHolder.class, ref) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Coach coach, int position) {
                ViewHolder.setName(coach.getName());
                ViewHolder.setText(coach.getMistId());
            }
        };
        recycler1.setAdapter(mAdapter);


        recycler2.setHasFixedSize(true);
        recycler2.setLayoutManager(new LinearLayoutManager(this));
        recycler2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        DividerItemDecoration dividerItemDecorationa = new DividerItemDecoration(recycler2.getContext(),
                (new LinearLayoutManager(this)).getOrientation());
        recycler2.addItemDecoration(dividerItemDecoration);
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("competitors-data");

        mAdapter2 = new FirebaseRecyclerAdapter<Competitor, PersonHolder>(Competitor.class, android.R.layout.two_line_list_item, PersonHolder.class, mref) {
            @Override
            public void populateViewHolder(PersonHolder ViewHolder, Competitor competitor, int position) {
                ViewHolder.setName(competitor.getName());
                ViewHolder.setText(competitor.getMistId());
            }
        };
        recycler2.setAdapter(mAdapter2);

    }


    @Override
    protected void onDestroy() {
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

