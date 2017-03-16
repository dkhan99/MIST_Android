package com.mistapp.mistandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;

public class LogInAuth extends AppCompatActivity implements View.OnClickListener {

    //changed when the activity changes states - onPause() and onResume(). Used to correctly show notifications
    public static boolean isInForeground;

    private static final String TAG = LogInAuth.class.getSimpleName();
    private EditText mEmailView;
    private EditText mPasswordView;

    private DataSnapshot currentUserSnapshot;
    private boolean exists;

    private TextView mLogInText;
    private TextView mForgotPasswordText;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context context;
    private CacheHandler cacheHandler;

    private ProgressBar progressBar;

    private TextView mtextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_auth);
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        sharedPref = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getApplication(), sharedPref, editor);


        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        mForgotPasswordText = (TextView) findViewById(R.id.text_forgot_password);

        Intent intent = getIntent();
        context = getApplication();

        //Initialize firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //Firebase listener, checks if user is signed in already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // User is signed in
                if (user != null) {
                    Log.d(TAG, "login onAuthStateChanged:signed_in:" + user.getUid());
                    //save user's uid in shared preferences


                    cacheHandler.cacheUserUid(user.getUid());
                    cacheHandler.commitToCache();
                    Intent intent = new Intent(getApplicationContext(), MyMistActivity.class);
                    intent.putExtra(getString(R.string.user_uid_key), user.getUid());
                    startActivity(intent);
                } else {  // User is signed out
                    Log.d(TAG, "login onAuthStateChanged:signed_out");
                    //remove user's fields from shared preferences
                    cacheHandler.removeCachedUserFields();
                    cacheHandler.commitToCache();
                }
                // ...
            }
        };

        //Initialize views
        mEmailView = (EditText) findViewById(R.id.log_in_email);
        mPasswordView = (EditText) findViewById(R.id.log_in_password);
        mLogInText = (TextView) findViewById(R.id.email_sign_in_text);
        mtextView = (TextView) findViewById(R.id.textViewSignin);

        //Listen for user clicks on the button and hyperlink
        mLogInText.setOnClickListener(this);
        mtextView.setOnClickListener(this);
        mForgotPasswordText.setOnClickListener(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG,"Enter pressed");
                    attemptSignIn();
                }
                return false;
            }

        });
    }

    //Checks if email is valid, we can add more logic here
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * If button or textview is clicked, then we go to the next activity after verifying data
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == mLogInText) {
            attemptSignIn();
        }

        if (view == mtextView) {
            finish();
            Intent intent = new Intent(this, RegisterAuth.class);
            startActivity(intent);
        }

        if (view == mForgotPasswordText){
            createPasswordResetDialog();
        }
    }

    public void createPasswordResetDialog(){


        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.forgot_password_popup, null, false);
            final Dialog dialog = new Dialog(LogInAuth.this);
            dialog.setContentView(layout);
            dialog.setTitle("Title...");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();


            Button btnClosePopup = (Button) layout.findViewById(R.id.forgot_pass_dismiss);
            Button btnSendPopup = (Button) layout.findViewById(R.id.send_password_reset_email);
            final EditText emailView = (EditText)layout.findViewById(R.id.forgotten_email);

            btnSendPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Sending password Reset Email!");
                    String email = emailView.getText().toString();
                    if (TextUtils.isEmpty(email)) {
                        emailView.setError(getString(R.string.error_field_required));
                        return;
                    } else if (!isEmailValid(email)) {
                        emailView.setError(getString(R.string.error_invalid_email));
                        return;
                    }
                    else{
                        Log.d(TAG, "sending password reset email to " + email);
                        mAuth.sendPasswordResetEmail(email);
                        Toast.makeText(LogInAuth.this, "Please check your email("+email+") for password reset instructions", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "dismissing notification detail");
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks with firebase if email/password combination is correct
     */
    public void attemptSignIn() {


        final String email = mEmailView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            Toast.makeText(this, "Please enter an password", Toast.LENGTH_LONG).show();
            focusView = mPasswordView;
            focusView.requestFocus();
            return;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_LONG).show();
            focusView = mEmailView;
            focusView.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(LogInAuth.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "signInWithEmail PASSED");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final String uid = user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            final DatabaseReference ref = mDatabase.child(getResources().getString(R.string.firebase_registered_user_table));

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        if (child.getKey().equals(uid)) {
                                            Log.d(TAG, "UID EXISTS!!!!"+uid);
                                            exists = true;
                                            currentUserSnapshot = child;
                                            break;
                                        }
                                    }

                                    //if exists, caches user fields in database, and sets notification topics
                                    if (exists){

                                        // Get User object from db
                                        String currentUserType = (String)currentUserSnapshot.child("userType").getValue();
                                        Object currentUser = null;

                                        if (currentUserType.equals("competitor")) {
                                            currentUser = currentUserSnapshot.getValue(Competitor.class);
                                            subscribeToCompetitorTopics((Competitor)currentUser);

                                        } else if (currentUserType.equals("coach")){
                                            currentUser = currentUserSnapshot.getValue(Coach.class);
                                            subscribeToCoachTopics((Coach)currentUser);

                                        }

                                        Log.d(TAG, "Login success");
                                        Log.d(TAG, currentUser.toString());

                                        //in case user was guest originally, remove the guest's notifications
                                        cacheHandler.removeCachedNotificationFields();

                                        cacheHandler.cacheAllUserFields(uid, currentUser, currentUserType);
                                        cacheHandler.commitToCache();

                                        Intent intent = new Intent(getApplicationContext(), MyMistActivity.class);
                                        intent.putExtra(getString(R.string.user_uid_key), uid);
                                        intent.putExtra(getString(R.string.current_user_type), currentUserType);

                                        startActivity(intent);


                                    } else{
                                        Log.d(TAG, "Login failure");
                                        Toast.makeText(context, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    // ...
                                }
                            });
                            // ...
                        }
                        progressBar.setVisibility(View.GONE);
                    }

         });
    }

    //subscribe to team name, competitions, and "competitor"
    public void subscribeToCompetitorTopics(Competitor currentUser){

        //subscribe to the current user's team name (replaces spaces with underscores in team name)
        String teamName = currentUser.getTeam();
        String underScoreTeamName = teamName.replaceAll(" ", "_");
        FirebaseMessaging.getInstance().subscribeToTopic(underScoreTeamName);
        Log.d(TAG, "TEAM NAME: "+underScoreTeamName);


        //subscribe to the current user type
        FirebaseMessaging.getInstance().subscribeToTopic("competitor");

        String[] compArray = {
                (currentUser).getGroupProject(),
                (currentUser).getArt(),
                (currentUser).getSports(),
                (currentUser).getBrackets(),
                (currentUser).getWriting(),
                (currentUser).getKnowledge()
        };

        //subscribe to user's competitions (replaces spaces with underscores in competition name)
        for (String competition: compArray){
            if (!competition.equals("")) {
                Log.d(TAG, "COMP NAME: "+competition);
                String underScoreCompName = competition.replaceAll(" ", "_");
                underScoreCompName = underScoreCompName.replaceAll("'", "_");
                underScoreCompName = underScoreCompName.replaceAll("/", "_");
                FirebaseMessaging.getInstance().subscribeToTopic(underScoreCompName);
            }
        }
    }

    //suscribes to team name and "coach"
    public void subscribeToCoachTopics(Coach currentCoach){

        //subscribe to the current user's team name (replaces spaces with underscores in team name)
        String teamName = currentCoach.getTeam();
        String underScoreTeamName = teamName.replaceAll(" ", "_");
        FirebaseMessaging.getInstance().subscribeToTopic(underScoreTeamName);

        //subscribe to the current user type
        FirebaseMessaging.getInstance().subscribeToTopic("coach");

    }

    @Override
    public void onResume() {
        super.onResume();
        isInForeground =true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInForeground = false;
    }
}
