package com.mistapp.mistandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Guest;
import com.mistapp.mistandroid.model.MistData;
import com.mistapp.mistandroid.model.User;

import static android.R.attr.value;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterAuth extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegisterAuth.class.getSimpleName();

    //changed when the activity changes states - onPause() and onResume(). Used to correctly show notifications
    public static boolean isInForeground;

    private boolean exists;
    private boolean taken;
    private DataSnapshot currentUserSnapshot;
    // UI references, essentially the elements on the android activity
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mMISTIdView;
    private TextView mRegisterText;
    private TextView mtextViewRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private Context context;
    private ProgressBar progressBar;


    private CacheHandler cacheHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_package_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        cacheHandler = CacheHandler.getInstance(getApplication(), sharedPref, editor);
        progressBar = (ProgressBar) findViewById(R.id.register_progress);

        context = getApplicationContext();

        //Initialize firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //User is signed in or not already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "register onAuthStateChanged:signed_in:" + user.getUid());
                    //save user's uid in shared preferences
                    cacheHandler.cacheUserUid(user.getUid());
                    cacheHandler.commitToCache();
                    Intent intent = new Intent(getApplicationContext(), MyMistActivity.class);
                    intent.putExtra(getString(R.string.user_uid_key), user.getUid());
                    startActivity(intent);
                } else {  // User is signed out
                    Log.d(TAG, "register onAuthStateChanged:signed_out");
                    //remove user's fields from shared preferences
                    cacheHandler.removeCachedUserFields();
                    cacheHandler.commitToCache();
                }
            }
        };

        // initialize views, which were declared earlier
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mMISTIdView = (EditText) findViewById(R.id.mist_id);
        mRegisterText = (TextView) findViewById(R.id.email_register_text);

        mtextViewRegister = (TextView)findViewById(R.id.textViewSignin);

        //attaching listeners to button and link
        mRegisterText.setOnClickListener(this);
        mtextViewRegister.setOnClickListener(this);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i(TAG,"Enter pressed");
                    attemptRegister();
                }
                return false;
            }

        });
    }

    /**
     * When the register button is clicked, we check if the filled out form is valid
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == mRegisterText) {
            attemptRegister();
        }
        if (view == mtextViewRegister) {
            Intent intent = new Intent(this, LogInAuth.class);
            startActivity(intent);
        }
    }


    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made, and user is notified
     */
    private void attemptRegister() {
        boolean cancel = false;

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mMISTIdView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String mistId = mMISTIdView.getText().toString().trim();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for valid mist id
        if (TextUtils.isEmpty(mistId) || !isMistIdValid(mistId)) {
            mMISTIdView.setError(getString(R.string.error_invalid_mistId));
            focusView = mMISTIdView;
            cancel = true;
        }

        //If anythnig was invalid, then this is true
        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        } else {
            //Register the user
            progressBar.setVisibility(View.VISIBLE);
            register();

            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
        }
    }
    //Checks if email is valid, we can add more logic here
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    //Check if password is valid, we could strengthen passwords here
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    //Checks if the mist id has a dash and is of length 10
    private boolean isMistIdValid(String mistId) {
        if (mistId.length() == 10 || mistId.length() == 9) {
            if (mistId.charAt(4) == '-') {
                return true;
            }
        }
        return false;
    }



    //not in use -> This method of returning value wouldn't work because onDataChange happens asynchronously(taken=always default)
    public boolean isMistIdTaken(final String mistId) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = mDatabase.child("mistprofile");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taken = false;
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(mistId)) {
                        taken = true;
                    }
                }
                Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
        Log.w(TAG, "MIST ID TAKEN: " + taken);
        return taken;
    }

    /*
     * Checks Firebase's User DB to see if th MIST ID exists
     * Creates Authenticated user with email + password
     * Creates an entry in the RegisteredUser DB
     */
    private void register() {

        final String email = mEmailView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();
        final String mistId = mMISTIdView.getText().toString().trim();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = mDatabase.child(getResources().getString(R.string.firebase_user_table));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exists = false;
                // This method is called only once
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(mistId)) {
                        Log.d(TAG, "EXISTS!!!!");
                        exists = true;
                        currentUserSnapshot = child;
                    }
                }
                if (exists){

                    Query ref1 = mDatabase.child(getResources().getString(R.string.firebase_registered_user_table)).orderByChild("mistId").equalTo(mistId);
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //if an item already exists (a user with the specific mist id...)
                            if (dataSnapshot.exists()) {
                                Toast.makeText(context, "That MIST ID has already been registered... please try again or contact support",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                //create auth user
                                mAuth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(RegisterAuth.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                                // Sign in failed- display a message to the user
                                                if (!task.isSuccessful()) {
                                                    Log.d(TAG, "CreateUserWithEmailAndPass failed", task.getException());
                                                    Toast.makeText(context, "That email account is already in use... please try again or contact support",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Sucessful sign in. Add user to registered-users database
                                                } else {
                                                    Log.d(TAG, "createUserWithEmailAndPass succeeded", task.getException());
                                                    String currentUserType = (String) currentUserSnapshot.child("userType").getValue();
                                                    String userMistID = "";
                                                    //figure out the type of user, create a respective object, populate its fields from the db, and save to 'registered-user' table
                                                    Object currentUser = null;
                                                    Log.d(TAG, "current user type is this: " + currentUserType);
                                                    if (currentUserType.equals("competitor")) {
                                                        currentUser = currentUserSnapshot.getValue(Competitor.class);
                                                        userMistID = ((Competitor)currentUser).getMistId();
                                                        subscribeToCompetitorTopics((Competitor) currentUser);
                                                    } else if (currentUserType.equals("coach")) {
                                                        currentUser = currentUserSnapshot.getValue(Coach.class);
                                                        userMistID = ((Coach)currentUser).getMistId();
                                                        subscribeToCoachTopics((Coach) currentUser);
                                                    }

                                                    //Saving to Database
                                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                    String uid = user.getUid();
                                                    mDatabase.child(getResources().getString(R.string.firebase_registered_user_table)).child(uid).setValue(currentUser);
                                                    Log.d(TAG, "user added to database: " + currentUser.toString());

                                                    //setting hasRegistered flag in the user db to true
//                                                    mDatabase.child(getResources().getString(R.string.firebase_user_table)).child(userMistID).child("hasRegistered").setValue(1);

                                                    cacheHandler.cacheAllUserFields(uid, currentUser, currentUserType);
                                                    cacheHandler.commitToCache();

                                                    Intent intent = new Intent(getApplicationContext(), MyMistActivity.class);
                                                    intent.putExtra(getString(R.string.user_uid_key), uid);
                                                    intent.putExtra(getString(R.string.current_user_type), currentUserType);
                                                    FirebaseMessaging.getInstance().subscribeToTopic(currentUserType);
                                                    startActivity(intent);
                                                }
                                                // ...
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //if mistID has already been used to register the user



                }
                //mist ID doesn't exist
                else{
                    Log.d(TAG, "The MIST ID: " + mistId + " does not exist. Please try with another MIST ID");
                    Toast.makeText(context, "The MIST ID: " + mistId + " does not exist. Please try with another MIST ID",
                            Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                progressBar.setVisibility(View.GONE);
                Log.w(TAG, "Failed to read value.", databaseError.toException());
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