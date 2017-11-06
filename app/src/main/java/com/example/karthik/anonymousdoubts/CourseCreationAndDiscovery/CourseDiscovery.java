package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.karthik.anonymousdoubts.Authentication.LoginActivity;
import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.UIDecorator.DividerItemDecoration;
import com.example.karthik.anonymousdoubts.R;
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
    private DatabaseReference courseMetaDataListEndPoint;

    String email;
    boolean isTeacher;
    boolean isStudent;

    String userId;
    FloatingActionButton fab;
    String userName;

    NavigationView navigationView;

    int initialState = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        String institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);
        courseMetaDataListEndPoint = userIdEndPoint.child("courseMetaDataArrayList");

        userIdEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
                isStudent = (boolean) dataSnapshot.child("isStudent").getValue();
                isTeacher = (boolean) dataSnapshot.child("isTeacher").getValue();
                userName = dataSnapshot.child("name").getValue(String.class);

                if(isTeacher) fab.show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new CourseListAdapter(courseMetaDataList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        addCourseMetaData();

        //EDITED HERE
        navigationView = (NavigationView) findViewById(R.id.Navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){

                    case R.id.db:
                        break;
                    case R.id.activity:
                        break;
                    case R.id.settings:
                        break;
                    case R.id.singout:
                        signOut();
                        break;
                }

                return true;
            }
        });








        //if(currentUserObj != null && currentUserObj.isTeacher) {}

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateCourseActivity.class);
                intent.putExtra("teacherName", userName);
                startActivityForResult(intent, CREATE_COURSE);
            }
        });

    }

    private void addCourseMetaData() {

        final ProgressDialog progressDialog = new ProgressDialog(CourseDiscovery.this,
                R.style.AppTheme_Dark_Dialog);
        if(initialState == 1){
            Log.e(TAG, "progress dialog");
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Fetching Courses...");
            progressDialog.show();
            initialState = -1;
        }


        courseMetaDataListEndPoint.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int state = 0;
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    CourseMetaData courseMetaData1 = noteSnapshot.getValue(CourseMetaData.class);
                    CourseMetaData courseMetaData2 = new CourseMetaData("Course name : " + courseMetaData1.getCourseName(),
                            "Course Teacher : "+courseMetaData1.getCourseTeacher(), "Course code : "+courseMetaData1.getCourseCode());
                    courseMetaData2.courseUId = courseMetaData1.courseUId;
                    if(!courseMetaDataList.contains(courseMetaData2)) {
                        state = 1;
                        courseMetaDataList.add(courseMetaData2);
                    }
                }

                if(state == 1){
                    mAdapter.notifyDataSetChanged();
                }
                if(initialState == -1){
                    progressDialog.dismiss();
                    initialState = 0;
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });


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

    private void signOut(){
        mAuth.signOut();
        Intent logout = new Intent(this, LoginActivity.class);
        startActivity(logout);
    }


}
