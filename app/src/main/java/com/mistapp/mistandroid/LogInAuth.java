package com.mistapp.mistandroid;

import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInAuth extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LogInAuth.class.getSimpleName();
    private EditText mEmailView;
    private EditText mPasswordView;

    private Button mLogInButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_auth);

        //Initialize firebase auth object
        mAuth = FirebaseAuth.getInstance();
        //Firebase listener
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
        //Initialize views
        mEmailView = (TextInputEditText) findViewById(R.id.log_in_email);
        mPasswordView = (TextInputEditText) findViewById(R.id.log_in_password);
        mLogInButton = (Button) findViewById(R.id.email_register_button);
        mtextView = (TextView) findViewById(R.id.textViewSignin);
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
            Intent intent = new Intent(this, Rulebook.class);
            startActivity(intent);
        }
        if (view == mtextView) {
            Intent intent = new Intent(this, RegisterAuth.class);
            startActivity(intent);
        }
    }

    /**
     * Start firebase auth
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Stop firebase auth
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Checks with firebase if email/password combination is correct
     */
    public void attemptSignIn() {
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
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
                        }

                        // ...
                    }
                });
    }

    /**
     * When sign-in button is clicked, this method is executed
     */
    public void goRulebook(View view) {
        Intent intent = new Intent(view.getContext(), Rulebook.class);
        startActivity(intent);

    }

    /**
     * When register link is clicked, this method is executed
     */
    public void goRegAuth(View view) {
        Intent intent = new Intent(view.getContext(), RegisterAuth.class);
        startActivity(intent);

    }
}
