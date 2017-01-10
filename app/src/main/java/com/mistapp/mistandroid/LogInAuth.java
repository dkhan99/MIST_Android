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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mistapp.mistandroid.model.Coach;
import com.mistapp.mistandroid.model.Competitor;
import com.mistapp.mistandroid.model.Guest;
import com.mistapp.mistandroid.model.MistData;

public class LogInAuth extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LogInAuth.class.getSimpleName();
    private EditText mEmailView;
    private EditText mPasswordView;

    private DataSnapshot currentUserSnapshot;
    private boolean exists;

    private Button mLogInButton;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String userType;
    private TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_auth);

        Intent intent = getIntent();
        userType = intent.getExtras().getString("userType");
        Toast.makeText(this, userType, Toast.LENGTH_SHORT).show();

        //Initialize firebase auth object
        mAuth = FirebaseAuth.getInstance();

        //Firebase listener, checks if user is signed in already
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //Initialize views
        mEmailView = (TextInputEditText) findViewById(R.id.log_in_email);
        mPasswordView = (TextInputEditText) findViewById(R.id.log_in_password);
        mLogInButton = (Button) findViewById(R.id.email_sign_in_button);
        mtextView = (TextView) findViewById(R.id.textViewSignin);

        //Listen for user clicks on the button and hyperlink
        mLogInButton.setOnClickListener(this);
        mtextView.setOnClickListener(this);
    }

    /**
     * If button or textview is clicked, then we go to the next activity after verifying data
     * @param view
     */
    @Override
    public void onClick(View view) {
        if (view == mLogInButton) {
            attemptSignIn();
        }

        if (view == mtextView) {
            finish();
            Intent intent = new Intent(this, RegisterAuth.class);
            startActivity(intent);
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
                            final String newEmail = email.replaceAll("\\.", "*");
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            final DatabaseReference ref = mDatabase.child("registered-user");

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        if (child.getKey().equals(newEmail)) {
                                            Log.d(TAG, "EMAIL EXISTS!!!!");
                                            exists = true;
                                            currentUserSnapshot = child;
                                            break;
                                        }
                                    }

                                    if (exists){
                                        // Get User object from db
                                        String currentUserType = (String)currentUserSnapshot.child("userType").getValue();
                                        Object currentUser = null;
                                        if (currentUserType.equals("competitor")) {
                                            currentUser = currentUserSnapshot.getValue(Competitor.class);
                                        } else if (currentUserType.equals("coach")){
                                            currentUser = currentUserSnapshot.getValue(Coach.class);
                                        } else if (currentUserType.equals("guest")) {
                                            currentUser = currentUserSnapshot.getValue(Guest.class);
                                        }
                                        Log.d(TAG, "Login success");
                                        Log.d(TAG, currentUser.toString());
                                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                        startActivity(intent);

                                    } else{
                                        Log.d(TAG, "Login failure");
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
                    }
         });


    }
}
