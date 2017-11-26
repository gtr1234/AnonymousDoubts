package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Course_AboutMeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference courseUIdsEndPoint;
    private DatabaseReference courseMetaDataEndPoint;

    private static final String TAG = "CourseAboutMeFragment";

    private DatabaseReference allCoursesDataUIdEndPoint;
    String userId;
    //Edited here
    private static String institution;
    private TextView course_description;
    public String Description;



    public Course_AboutMeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String courseUId = this.getArguments().getString("courseUId");

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();

        //End points references

        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);
        courseUIdsEndPoint = userIdEndPoint.child("courseUIds");
        courseMetaDataEndPoint = mDatabase.child("institution").child(institution);
        allCoursesDataUIdEndPoint = mDatabase.child("institution").child(institution).child("allCoursesData").child(courseUId);


        View v =  inflater.inflate(R.layout.fragment_course__about_me, container, false);
        //inflater.inflate(R.layout.fragment_course__about_me, container, false);
        course_description = (TextView)v.findViewById(R.id.course_description_text);

        //Edited here
        allCoursesDataUIdEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Description = dataSnapshot.child("courseDescription").getValue(String.class);

                Log.e(TAG,"description = "+Description);
                course_description.setText(Description);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }

}
