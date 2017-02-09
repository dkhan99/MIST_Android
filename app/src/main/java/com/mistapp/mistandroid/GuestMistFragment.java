package com.mistapp.mistandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.roughike.bottombar.BottomBar;

/**
 * Created by aadil on 1/23/17.
 */

public class GuestMistFragment extends Fragment implements View.OnClickListener{

    TextView signInText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("MY MIST");

        View view = inflater.inflate(R.layout.fragment_guest_mist, container, false);

        signInText = (TextView)view.findViewById(R.id.guest_login);
        signInText.setOnClickListener(this);

        return view;
    }

    /**
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == signInText){

            FirebaseMessaging.getInstance().unsubscribeFromTopic("guest");
            //goes to login activity
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.putExtra("guest_sign_in", "guest_sign_in");
            startActivity(intent);

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        BottomBar bbar = ((MyMistActivity)getActivity()).getBottomBar();
        bbar.selectTabAtPosition(2);
    }
}
