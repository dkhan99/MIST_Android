package com.mistapp.mistandroid;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = RegisterAuth.class.getSimpleName();

    private Button myTeamButton;
    private Button myScheduleButton;
    private Button helpHotlineButton;
    private Button rulebooksButton;
    private Button orientationVidButton;
    private Button socialMediaButton;
    private Button bracketsButton;
    private Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Initialize firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //User is signed in or not already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        //Initialize views
        myTeamButton = (Button) findViewById(R.id.my_team);
        myScheduleButton = (Button) findViewById(R.id.my_scehedule);
        helpHotlineButton = (Button) findViewById(R.id.help_hotline);
        rulebooksButton = (Button) findViewById(R.id.rulebooks);
        orientationVidButton = (Button) findViewById(R.id.orientation_video);
        socialMediaButton = (Button) findViewById(R.id.social_media);
        bracketsButton = (Button) findViewById(R.id.brackets);
        logoutButton = (Button) findViewById(R.id.logout);

        //Listen for user clicks on the button and hyperlink
        myTeamButton.setOnClickListener(this);
        myScheduleButton.setOnClickListener(this);
        helpHotlineButton.setOnClickListener(this);
        rulebooksButton.setOnClickListener(this);
        orientationVidButton.setOnClickListener(this);
        socialMediaButton.setOnClickListener(this);
        bracketsButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

    }

    /**
     * If button or textview is clicked, then we go to the next activity after verifying data
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == myTeamButton) {

        }
        else if (view == myScheduleButton){
            Intent intent = new Intent(getApplicationContext(), WeekendSched.class);
            startActivity(intent);
        }
        else if (view == helpHotlineButton){

        }
        else if (view == rulebooksButton){
            Intent intent = new Intent(getApplicationContext(), Rulebook.class);
            startActivity(intent);
        }
        else if (view == bracketsButton){

        }
        else if (view == logoutButton){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "peace out ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
        }

    }

    /**
     * Start Firebase authentication?
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * End Firebase authentication
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}