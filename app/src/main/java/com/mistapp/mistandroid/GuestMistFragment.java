package com.mistapp.mistandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.messaging.FirebaseMessaging;
import com.roughike.bottombar.BottomBar;

/**
 * Created by aadil on 1/23/17.
 */

public class GuestMistFragment extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("MY MIST");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_guest_mist, container, false);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //adding signin text to actionbar
        inflater.inflate(R.menu.actionbar_menu_guest, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.signin_item:
                FirebaseMessaging.getInstance().unsubscribeFromTopic("guest");
                //goes to login activity
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.putExtra("guest_sign_in", "guest_sign_in");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        BottomBar bbar = ((MyMistActivity)getActivity()).getBottomBar();
        bbar.selectTabAtPosition(2);
    }
}
