package com.mistapp.mistandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity implements View.OnTouchListener{

    TextView studentCoachText;
    TextView guestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        studentCoachText = (TextView)findViewById(R.id.studentCoach);
        guestText = (TextView)findViewById(R.id.guest);

    }

    @Override
    protected void onStart(){
        super.onStart();
        studentCoachText.setOnTouchListener(this);
        guestText.setOnTouchListener(this);
    }

    /**
     * When the register button is clicked, we check if the filled out form is valid
     * @param view
     */
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        //pressed any of the three options
        if (view == studentCoachText || view == guestText) {
            if (event.ACTION_UP == 1) {
                TextView pressedItem = (TextView)view;
                Intent intent = new Intent(this, LogInAuth.class);
                intent.putExtra("userType", pressedItem.getText());
                startActivity(intent);
                view.setOnTouchListener(null);
            }
        }
        return true;
//
//        if (view == mRegisterButton) {
//            attemptRegister();
//        }
//        if (view == mtextViewRegister) {
//            Intent intent = new Intent(view.getContext(), LogInAuth.class);
//            startActivity(intent);
//        }
    }

}