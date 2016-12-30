package com.mistapp.mistandroid;

import android.content.Intent;
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

/**
 * A login screen that offers login via email/password.
 */
public class RegisterAuth extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegisterAuth.class.getSimpleName();


    // UI references, essentially the elements on the android activity
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mMISTIdView;
    private Button mRegisterButton;
    private TextView mtextViewRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_auth);

        //Initialize firebase auth object
        mAuth = FirebaseAuth.getInstance();
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
                // ...
            }
        };
        // initialize views, which were declared earlier
        mEmailView = (TextInputEditText) findViewById(R.id.email);
        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mFirstNameView = (TextInputEditText) findViewById(R.id.first_name);
        mLastNameView = (TextInputEditText) findViewById(R.id.last_name);
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
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mMISTIdView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String mistId = mMISTIdView.getText().toString().trim();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
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

        //Check for valid first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_firstName));
            focusView = mFirstNameView;
            cancel = true;
        }

        //Check for a valid last name
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_lastName));
            focusView = mLastNameView;
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

    //Registers user's pass and email to firebase
    public void register() {
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //User is successufully registered and logged in
                            // we will start the profule activity here
                            // right now lets display a toast
                            Toast.makeText(RegisterAuth.this, "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LogInAuth.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterAuth.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * When register button is clicked, this method is executed
     */
    public void goLogIn(View view) {
        Intent intent = new Intent(view.getContext(), LogInAuth.class);
        startActivity(intent);
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