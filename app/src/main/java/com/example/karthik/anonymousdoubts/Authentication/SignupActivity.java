package com.example.karthik.anonymousdoubts.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karthik.anonymousdoubts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "SignupActivity";

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    EditText _reEnterPasswordText;
    Button _signupButton;
    TextView _loginLink;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseDatabase mFirebaseInstance;

    private boolean isTeacher;
    private boolean isStudent;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        isTeacher = false;
        isStudent = false;

        mAuth = FirebaseAuth.getInstance();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = _nameText.getText().toString();
                String email = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                String reEnterPassword = _reEnterPasswordText.getText().toString();


                createAccount(email, password, reEnterPassword, name);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    onSignupSuccess();
                }

            }
        };

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> roles = new ArrayList<>();
        roles.add("Teacher");
        roles.add("Student");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseReference = mFirebaseInstance.getReference("institution");


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void createUser(String name, String email, String uId){

        String institution = email.split("@")[1];
        institution = institution.replace(".","");

        User user = new User(email, name, isStudent, isTeacher, null);
        mFirebaseDatabaseReference.child(institution).child("users").child(uId).setValue(user);
        mFirebaseDatabaseReference.child(institution).child("users").child(uId)
                .child("isEmailVerified").setValue(false);

    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        setResult(RESULT_OK, null);
        Log.i(TAG,"finsihed Signup");
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    private void createAccount(final String email, final String password,final String reEnterPassword,
                               final String name) {
        Log.d(TAG, "createAccount:" + email);
        if (!validate(name, email, password, reEnterPassword)) {
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();


        // [START create_user_with_email]

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail(); //Edited here for email verification
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Edited here for verification

                            createUser(name, email, user.getUid());

                            onSignupSuccess();
                        } else {
                            String message = "Registration failed";
                            // If sign in fails, display a message to the user.
                            try
                            {
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                message = "Weak Password";
                            }
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                message = "Malformed Email";
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                message = "Email already exists";
                            }
                            catch (Exception e)
                            {
                                ;
                            }


                            Log.w(TAG, message, task.getException());
                            Toast.makeText(SignupActivity.this, message,
                                    Toast.LENGTH_SHORT).show();
                            onSignupFailed();
                        }

                        progressDialog.dismiss();
                    }
                });
        // [END create_user_with_email]
    }

    //Edited here for email verification

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this,"verification email sent successfully.",Toast.LENGTH_SHORT ).show();
                            }else{
                                Toast.makeText(SignupActivity.this,"Couldn't send verification email.",Toast.LENGTH_SHORT ).show();
                            }
                        }
                    });
        }
    }
    public boolean validate(String name, String email, String password, String reEnterPassword) {
        boolean valid = true;

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }



        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        if(item.equals("Teacher")) {
            isTeacher = true;
            isStudent = false;
        }
        else if(item.equals("Student")) {
            isStudent = true;
            isTeacher = false;
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}