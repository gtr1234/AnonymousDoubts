package com.example.karthik.anonymousdoubts;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourseDiscovery extends AppCompatActivity {

    private static final int CREATE_COURSE = 0;
    private static final String TAG = "CourseDiscovery";
    private List<CourseMetaData> courseMetaDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CourseListAdapter mAdapter;

    private final String COURSENAME = "Course Name : ";
    private final String COURSETEACHER = "Course Teacher : ";
    private final String COURSECODE = "Course Code : ";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private User currentUserObj;
    String userId;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        String institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new CourseListAdapter(courseMetaDataList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        addCourseMetaData();


        //if(currentUserObj != null && currentUserObj.isTeacher) {}

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateCourseActivity.class);
                startActivityForResult(intent, CREATE_COURSE);
            }
        });

    }

    private void addCourseMetaData() {
        CourseMetaData courseMetaData = new CourseMetaData(COURSENAME+"Software Engineering",
                COURSETEACHER+"Dr.Balwinder Sodhi",COURSECODE+"CSL456");
        courseMetaDataList.add(courseMetaData);

        courseMetaData = new CourseMetaData(COURSENAME+"BTP",COURSETEACHER+"Dr.Sowmitra",COURSECODE+"CSL555");
        courseMetaDataList.add(courseMetaData);

        final List<User> userArrayList = new ArrayList<>();

        userIdEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userArrayList.add(user);
                currentUserObj = user;
                if(user.isTeacher) fab.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        if(userArrayList.size() > 0) {
            User user = userArrayList.get(0);
            ArrayList<CourseMetaData> courseList = user.courseMetaDataArrayList;
            if(courseList != null) {
                for (CourseMetaData course : courseList) {
                    courseMetaDataList.add(course);
                }
            }
        }


        mAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG,"req "+requestCode+" res "+resultCode);
        if (requestCode == CREATE_COURSE) {
            if (resultCode == RESULT_OK) {
                Log.e(TAG,"all okay-- course created");

                /*
                Intent myIntent = new Intent(LoginActivity.this, CourseDiscovery.class);
                // myIntent.putExtra("key", value); // should send user details
                LoginActivity.this.startActivity(myIntent);

                this.finish();
                */

            }
            else{
                Log.e(TAG,"no ok");
            }
        }
        else{
            Log.e(TAG,"no signup");
        }
    }


}
