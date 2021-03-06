package com.example.karthik.anonymousdoubts.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.CourseDiscovery;
import com.example.karthik.anonymousdoubts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView signupLink;
    ImageView logoImageView;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;

    FirebaseUser user;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.i(TAG, "created");

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        signupLink = (TextView) findViewById(R.id.link_signup);

        logoImageView = (ImageView) findViewById(R.id.logoImageView);

        setSizeLogo(logoImageView);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                signIn(email, password);
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                //finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    onLoginSuccess(null, 0);
                }
            }
        };

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "started");
        // Check if user is signed in (non-null) and update UI accordingly.  AuthStateListener Imp
        FirebaseUser currentUser = mAuth.getCurrentUser();

        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_check_user]

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG,"req "+requestCode+" res "+resultCode);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG,"all okay");
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically

                onLoginSuccess(null, 1);

            }
            else{
                Log.e(TAG,"no ok");
            }
        }
        else{
            Log.e(TAG,"no signup");
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(ProgressDialog progressDialog, int state) {
        loginButton.setEnabled(true);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        user = mAuth.getCurrentUser();

        //edited here for email verification

        try {
            if (user.isEmailVerified()) {
                Log.d(TAG,"onComplete : Success, Email verifired");
                Intent myIntent = new Intent(LoginActivity.this, CourseDiscovery.class);
                //myIntent.putExtra("userName", ); // should send user details
                LoginActivity.this.startActivity(myIntent);
                finish();
            }else{
                /*mAuth.signOut();
                Log.d(TAG,"onComplete : Success, Email not verifired");
                String msg = "";
                if(state == 0){
                    msg = "Email not verified, please check your email";
                }
                else if(state == 1){
                    msg = "Please verify your email";
                }
                */

                Intent myIntent = new Intent(LoginActivity.this, EmailVerificationActivity.class);
                LoginActivity.this.startActivity(myIntent);
                finish();

            }
        }catch (NullPointerException e){
            Log.e(TAG,"onComplete: Nullpointer Exception: "+e.getMessage());
        }

        if(progressDialog != null)
            progressDialog.dismiss();
    }

    public void onLoginFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate(String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validate(email, password)) {
            onLoginFailed("Login failed");
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                           onLoginSuccess(progressDialog, 0);

                        } else {
                            // If sign in fails, display a message to the user.
                            String message = "Authentication failed";
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {
                                message = "Invalid email or password";
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                message = "Invalid email or password";
                            }
                            catch (Exception e)
                            {
                                ;
                            }

                            Log.w(TAG, message, task.getException());
                            onLoginFailed(message);
                            progressDialog.dismiss();
                        }

                    }
                });

    }


    private void setSizeLogo(View view){

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics( dm );

        int screenWidth = dm.widthPixels;
        screenWidth = screenWidth - (int) Math.round(0.15*screenWidth);

        view.getLayoutParams().width = screenWidth;
        view.getLayoutParams().height = screenWidth/2;

    }


}
