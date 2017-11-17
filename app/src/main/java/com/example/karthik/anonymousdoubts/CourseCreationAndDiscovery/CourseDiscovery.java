package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.karthik.anonymousdoubts.Authentication.LoginActivity;
import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.UIDecorator.DividerItemDecoration;
import com.example.karthik.anonymousdoubts.CourseHomePage.CourseHomepageActivity;
import com.example.karthik.anonymousdoubts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseDiscovery extends AppCompatActivity {

    private static final int CREATE_COURSE = 0;
    private static final String TAG = "CourseDiscovery";
    private List<CourseMetaData> courseMetaDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;

    private static int enrolledCourseCount = 1;

    private final String COURSENAME = "Course Name : ";
    private final String COURSETEACHER = "Course Teacher : ";
    private final String COURSECODE = "Course Code : ";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference courseUIdsEndPoint;
    private DatabaseReference courseMetaDataEndPoint;

    String email;
    boolean isTeacher;
    boolean isStudent;

    String userId;
    FloatingActionButton fab;
    String userName;

    NavigationView navigationView;

    int initialState = 1;

    private FastAdapter fastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        searchView = (SearchView) findViewById(R.id.inp_searchView);



        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        String institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);
        courseUIdsEndPoint = userIdEndPoint.child("courseUIds");
        courseMetaDataEndPoint = mDatabase.child("institution").child(institution);

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

        // fast adapters usage
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();

        final ItemAdapter headerAdapter = new ItemAdapter();
        final ItemAdapter itemAdapter = new ItemAdapter();

        fastAdapter = FastAdapter.with(Arrays.asList(headerAdapter, itemAdapter));
        fastAdapter.withSelectable(true);

        fastAdapter.setHasStableIds(true);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(stickyHeaderAdapter.wrap(fastAdapter));

        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.addItemDecoration(decoration);

        // filling data
        addCourseMetaData(headerAdapter, itemAdapter);

        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });



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


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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

        fastAdapter.withSavedInstanceState(savedInstanceState);

        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

    }

    private void addCourseMetaData(ItemAdapter headerAdapter, final ItemAdapter itemAdapter) {

        //headerAdapter.add(new CourseMetaDataView().withHeader("TestingHeader").withIdentifier(1));

        final List<IItem> items = new ArrayList<>();

        final ProgressDialog progressDialog = new ProgressDialog(CourseDiscovery.this,
                R.style.AppTheme_Dark_Dialog);

        if(initialState == 1){
            Log.e(TAG, "progress dialog");
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Fetching Courses...");
            progressDialog.show();
            initialState = -1;
        }


        courseUIdsEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    String courseUid = noteSnapshot.getValue(String.class);

                    if(!containsCourseId(courseUid)){

                        enrolledCourseCount++;

                        DatabaseReference courseMetaDataEndPointNew = courseMetaDataEndPoint.child("allCoursesMetaData").child(courseUid);

                        courseMetaDataEndPointNew.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int state = 0;

                                ArrayList<String> courseMetaDataValues = new ArrayList<>();

                                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                                    courseMetaDataValues.add(noteSnapshot.getValue(String.class));
                                }

                                if(courseMetaDataValues.size() == 5) {
                                    CourseMetaData courseMetaData2 = new CourseMetaData(courseMetaDataValues.get(0),
                                            courseMetaDataValues.get(1), courseMetaDataValues.get(2));
                                    courseMetaData2.courseUId = courseMetaDataValues.get(3);
                                    courseMetaData2.passcode = courseMetaDataValues.get(4);

                                    if (!courseMetaDataList.contains(courseMetaData2)) {
                                        state = 1;
                                        courseMetaDataList.add(courseMetaData2);
                                        items.add(new CourseMetaDataView().withCourseName(courseMetaData2.getCourseName())
                                                .withCourseTeacher(courseMetaData2.getCourseTeacher())
                                                .withCourseCode(courseMetaData2.getCourseCode())
                                                .withHeader("Enrolled Courses").withIdentifier(100 + enrolledCourseCount));
                                        enrolledCourseCount++;
                                    }
                                }


                                /*if(state == 1){
                                    mAdapter.notifyDataSetChanged();
                                }*/
                                if(initialState == -1){
                                    progressDialog.dismiss();
                                    initialState = 0;
                                    itemAdapter.add(items);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);

        Log.e(TAG,"item size = "+itemAdapter.getAdapterItemCount());
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


    private boolean containsCourseId(String courseUId){

        for (CourseMetaData courseMetaData: courseMetaDataList){
            if(courseMetaData.courseUId.equals(courseUId)){
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

