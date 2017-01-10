package com.mistapp.mistandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private boolean exists;
    private boolean taken;
    private DataSnapshot currentUserSnapshot;
    // UI references, essentially the elements on the android activity
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mMISTIdView;
    private Button mRegisterButton;
    private TextView mtextViewRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);

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
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // initialize views, which were declared earlier
        mEmailView = (TextInputEditText) findViewById(R.id.email);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mMISTIdView = (TextInputEditText) findViewById(R.id.mist_id);

        mRegisterButton = (Button) findViewById(R.id.email_register_button);

        mtextViewRegister = (TextView)findViewById(R.id.textViewSignin);

        //attaching listeners to button and link
        mRegisterButton.setOnClickListener(this);
        mtextViewRegister.setOnClickListener(this);
    }

    /**
     * When the register button is clicked, we check if the filled out form is valid
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == mRegisterButton) {
            attemptRegister();
        }
        if (view == mtextViewRegister) {
            Intent intent = new Intent(view.getContext(), LogInAuth.class);
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

//        if (isMistIdTaken(mistId)) {
//            mMISTIdView.setError(getString(R.string.error_invalid_mistId));
//            focusView = mMISTIdView;
//            cancel = true;
//        }

        //If anythnig was invalid, then this is true
        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            Log.d(TAG, "11111");
            focusView.requestFocus();
            return;
        } else {
            Log.d(TAG, "2222");
            //Register the user
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
        if (mistId.length() == 10) {
            if (mistId.charAt(4) == '-') {
                return true;
            }
        }
        return false;
    }




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

    //Registers user's pass and email to firebase


    /*
     * Checks Firebase's User DB to see if th MIST ID exists
     * Creates Authenticated user with email + password
     * [TODO] Creates an entry in the RegisteredUser DB
     */
    private void register() {

        final String email = mEmailView.getText().toString().trim();
        final String password = mPasswordView.getText().toString().trim();
        final String mistId = mMISTIdView.getText().toString().trim();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = mDatabase.child("user");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exists = false;
                Log.d(TAG, "333333");
                // This method is called only once
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(mistId)) {
                        Log.d(TAG, "EXISTS!!!!");
                        exists = true;
                        currentUserSnapshot = child;
                    }
                }
                Log.d(TAG, "4444");
                if (exists){

                    //create auth user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterAuth.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // Sign in failed- display a message to the user
                                    if (!task.isSuccessful()) {
                                        Log.d(TAG, "CreateUserWithEmailAndPass failed", task.getException());
                                        Toast.makeText(context, "Technical error or user exists... please try again or contact support",
                                                Toast.LENGTH_SHORT).show();
                                    // Sucessful sign in. Add user to registered-users database
                                    } else {
                                        Log.d(TAG, "createUserWithEmailAndPass succeeded", task.getException());
                                        String currentUserType = (String)currentUserSnapshot.child("userType").getValue();

                                        //figure out the type of user, create a respective object, populate its fields from the db, and save to 'registered-user' table
                                        Object currentUser = null;
                                        if (currentUserType.equals("competitor")) {
                                            currentUser = currentUserSnapshot.getValue(MistData.class);
                                            //saving to database
                                        } else if (currentUserType.equals("coach")){
                                            currentUser = currentUserSnapshot.getValue(Coach.class);
                                        } else if (currentUserType.equals("guest")){
                                            currentUser = currentUserSnapshot.getValue(Guest.class);
                                        }

//                                      Competitor currentUser = currentUserSnapshot.getValue(Competitor.class);
                                        Log.d(TAG, "USER RETREIVED: " + currentUser.toString());
                                        String newEmail = email.replaceAll("\\.","*");
//                                        String newEmail = email.substring(0,dotIndex);
                                        mDatabase.child("registered-user").child(newEmail).setValue(currentUser);
                                        Log.d(TAG, "WHY IS IT NOT PRINTING?: " + currentUser.toString());

                                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                        startActivity(intent);
                                    }
                                    // ...
                                }
                            });
                }

                Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    public void shit(String email, String password){
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
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                            startActivity(intent);
                        }
                        // ...
                    }
                });
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