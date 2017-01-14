package com.mistapp.mistandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyMistFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_mist, container, false);

        ListView coaches_lv = (ListView)view.findViewById(R.id.coaches_list);
        ListView teammates_lv = (ListView)view.findViewById(R.id.teammates_list);

        final List<String> coaches_list = new ArrayList<String>();
        coaches_list.add("Coach1 name");
        coaches_list.add("Coach2 name");

        final List<String> teammates_list = new ArrayList<String>();
        coaches_list.add("Teammate1 name");
        coaches_list.add("Teammate2 name");
        coaches_list.add("Teammate3 name");
        coaches_list.add("Teammate4 name");
        coaches_list.add("Teammate5 name");
        coaches_list.add("Teammate6 name");
        coaches_list.add("Teammate7 name");

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> coachesArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, coaches_list);
        coaches_lv.setAdapter(coachesArrayAdapter);

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> teammatesArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, teammates_list);
        teammates_lv.setAdapter(teammatesArrayAdapter);

        return view;
    }
}