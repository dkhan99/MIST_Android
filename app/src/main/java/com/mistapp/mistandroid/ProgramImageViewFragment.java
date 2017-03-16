package com.mistapp.mistandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ProgramImageViewFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.program_page_title));
        View view = inflater.inflate(R.layout.fragment_program_image, container, false);
        TouchImageView image = (TouchImageView) view.findViewById(R.id.image_friday);
        image.setMaxZoom(4f);
        String dayToShow = "saturday";
        image.setImageDrawable(getActivity().getDrawable(R.drawable.saturday25dp));
        if (getArguments().containsKey(getString(R.string.program_image))){
            dayToShow = getArguments().getString(getString(R.string.program_image));
        }

        if (dayToShow.equals("friday")){
            image.setImageDrawable(getActivity().getDrawable(R.drawable.friday25dp));
        }
        else if (dayToShow.equals("saturday")){
            image.setImageDrawable(getActivity().getDrawable(R.drawable.saturday25dp));
        }
        else if(dayToShow.equals("sunday")){
            image.setImageDrawable(getActivity().getDrawable(R.drawable.sunday25dp));
        }
        else{
            image.setImageDrawable(getActivity().getDrawable(R.drawable.saturday25dp));
        }
        return view;
    }


}
