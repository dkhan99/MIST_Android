package com.mistapp.mistandroid;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.roughike.bottombar.BottomBar;

import java.io.IOException;
import java.io.InputStream;


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
        image.setImageDrawable(getActivity().getDrawable(R.drawable.saturday));
        if (getArguments().containsKey(getString(R.string.program_image))){
            dayToShow = getArguments().getString(getString(R.string.program_image));
        }

        if (dayToShow.equals("friday")){
            image.setImageDrawable(getActivity().getDrawable(R.drawable.friday));
        }
        else if (dayToShow.equals("saturday")){
            image.setImageDrawable(getActivity().getDrawable(R.drawable.saturday));
        }
        else if(dayToShow.equals("sunday")){
            image.setImageDrawable(getActivity().getDrawable(R.drawable.sunday));
        }
        else{
            image.setImageDrawable(getActivity().getDrawable(R.drawable.saturday));
        }
        return view;
    }


}
