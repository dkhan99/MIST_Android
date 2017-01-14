package com.mistapp.mistandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity implements View.OnTouchListener{

    private static final String TAG = RegisterAuth.class.getSimpleName();

    TextView studentText;
    TextView coachText;
    TextView guestText;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        studentText = (TextView)findViewById(R.id.student);
        coachText = (TextView)findViewById(R.id.coach);
        guestText = (TextView)findViewById(R.id.guest);

        mAuth = FirebaseAuth.getInstance();

        //User is signed in or not already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    editor.putString(getString(R.string.user_uid), user.getUid());
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.putExtra("uid", user.getUid());
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }


    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        studentText.setOnTouchListener(this);
        coachText.setOnTouchListener(this);
        guestText.setOnTouchListener(this);
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

    /**
     * When the register button is clicked, we check if the filled out form is valid
     * @param view
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        //pressed any of the three options
        if (view == studentText || view == coachText || view == guestText) {
            if (event.ACTION_UP == 1) {
                Intent intent;
                if (view == guestText){
                    intent = new Intent(this, MyMistActivity.class);
                }
                else{
                    intent = new Intent(this, LogInAuth.class);
                }

                startActivity(intent);
                view.setOnTouchListener(null);
            }
        }
        return true;

    }

}