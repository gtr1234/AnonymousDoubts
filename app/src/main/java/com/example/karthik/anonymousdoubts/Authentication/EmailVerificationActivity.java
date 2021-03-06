package com.example.karthik.anonymousdoubts.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.CourseDiscovery;
import com.example.karthik.anonymousdoubts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EmailVerificationActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference emailVerifiedEndPoint;

    String userId;
    String institution;

    Button resendButton;
    Button alreadyVerified;
    TextView welcomeTextView;
    TextView verifyEmailTextView;

    ProgressDialog progressDialog;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mAuth = FirebaseAuth.getInstance();

        firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        emailVerifiedEndPoint = mDatabase.child("institution").child(institution).child("users")
                .child(userId).child("isEmailVerified");


        resendButton = (Button) findViewById(R.id.resend_button);
        alreadyVerified = (Button) findViewById(R.id.alreadyVerified);
        welcomeTextView = (TextView) findViewById(R.id.welcomeText);
        verifyEmailTextView = (TextView) findViewById(R.id.verifyEmailText);

        welcomeTextView.setVisibility(View.INVISIBLE);
        resendButton.setVisibility(View.INVISIBLE);
        verifyEmailTextView.setVisibility(View.INVISIBLE);


        progressDialog = new ProgressDialog(EmailVerificationActivity.this,
                R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Securing your connection to the MainFrame ...");
        progressDialog.show();


        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(EmailVerificationActivity.this,"verification email sent successfully.",Toast.LENGTH_SHORT ).show();
                                }else{
                                    Toast.makeText(EmailVerificationActivity.this,"Couldn't send verification email.",Toast.LENGTH_SHORT ).show();
                                }
                            }
                        });
            }
        });


        alreadyVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.reload();
                Log.e("EmailVerification"," emailVerified = "+firebaseUser.isEmailVerified() +"  "+firebaseUser);

                Handler handler = new Handler();  // call should be state alert!!!!!!!!!!!!
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if(firebaseUser.isEmailVerified()){
                            Intent myIntent = new Intent(EmailVerificationActivity.this, CourseDiscovery.class);
                            EmailVerificationActivity.this.startActivity(myIntent);
                            finish();
                        }
                        else{
                            Toast.makeText(EmailVerificationActivity.this,
                                    "Please verify your Email first",Toast.LENGTH_SHORT ).show();
                        }
                    }
                }, 2000);

            }
        });


        emailVerifiedEndPoint.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isEmailVerified = dataSnapshot.getValue(Boolean.class);

                progressDialog.dismiss();

                if(isEmailVerified){
                    Intent myIntent = new Intent(EmailVerificationActivity.this, CourseDiscovery.class);
                    EmailVerificationActivity.this.startActivity(myIntent);
                    finish();
                }
                else{
                    welcomeTextView.setVisibility(View.VISIBLE);
                    resendButton.setVisibility(View.VISIBLE);
                    verifyEmailTextView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
