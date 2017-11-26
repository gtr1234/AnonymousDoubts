package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CourseDiscovery extends AppCompatActivity {

    private static final int CREATE_COURSE = 0;
    private static final String TAG = "CourseDiscovery";
    private RecyclerView recyclerView;
    private SearchView searchView;

    ProgressDialog progressDialog;

    int noCourseState = 1;

    final ArrayList<String> enrolledCourseUIdList = new ArrayList<>();
    final ArrayList<String> availableCourseUIdList = new ArrayList<>();

    private static int enrolledCourseCount = 0;
    private static int unEnrolledCourseCount = 0;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference courseUIdsEndPoint;
    private DatabaseReference courseMetaDataEndPoint;

    private static String institution;

    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;
    private TextView header_email_id;
    private TextView userNameHeader;

    String email_for_header;
    String email;
    boolean isTeacher;
    boolean isStudent;

    String userId;
    FloatingActionButton fab;
    String userName;


    private FastAdapter fastAdapter;
    private Toast backtoast;
    //Edited here for onbackpressed

    public void onBackPressed() {

            if(backtoast!=null&&backtoast.getView().getWindowToken()!=null) {
                finish();
            } else {
                backtoast = Toast.makeText(this, "Press back to exit", Toast.LENGTH_SHORT);
                backtoast.show();
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_discovery);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        //Edited here

        mDrawerlayout = (DrawerLayout)findViewById(R.id.content_layout_discovery);
        mToggle = new ActionBarDrawerToggle(this,mDrawerlayout,R.string.open,R.string.close);

        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        //Ends here



        searchView = (SearchView) findViewById(R.id.inp_searchView);

        //new MaterializeBuilder().withActivity(this).build();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        email_for_header =firebaseUser.getEmail();

        //Edited for header
        NavigationView navigationView = (NavigationView) findViewById(R.id.Navigation_view);
        View headerView = navigationView.getHeaderView(0);
        header_email_id = (TextView) headerView.findViewById(R.id.emailid_header_user);

        userNameHeader = (TextView) headerView.findViewById(R.id.username_header);



        //Log.e(TAG, "Email = "+email_for_header);
        header_email_id.setText(email_for_header);


        institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);
        courseUIdsEndPoint = userIdEndPoint.child("courseUIds");
        courseMetaDataEndPoint = mDatabase.child("institution").child(institution);

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

        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });


        userIdEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue(String.class);
                isStudent = (boolean) dataSnapshot.child("isStudent").getValue();
                isTeacher = (boolean) dataSnapshot.child("isTeacher").getValue();
                userName = dataSnapshot.child("name").getValue(String.class);
                userNameHeader.setText(userName);

                if(isTeacher) {
                    fab.show();
                }

                addCourseMetaData(headerAdapter, itemAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
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


        progressDialog = new ProgressDialog(CourseDiscovery.this,
                R.style.AppTheme_Dark_Dialog);


        Log.e(TAG, "progress dialog");
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Courses...");
        progressDialog.show();

        fastAdapter.withOnClickListener(new OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter<IItem> adapter, IItem item, int position) {

                if (item instanceof CourseMetaDataView) {

                    String header = ((CourseMetaDataView) item).header;

                    if(header.equals("Available Courses")){

                        checkInputPasscode(item, position, itemAdapter);

                    }
                    else if(header.equals("Enrolled Courses") || header.equals("Your Courses")){
                        Intent myIntent = new Intent(CourseDiscovery.this, CourseHomepageActivity.class);
                        myIntent.putExtra("courseUId", ((CourseMetaDataView) item).courseUId); // should send user details
                        CourseDiscovery.this.startActivity(myIntent);
                        finish();
                    }


                }
                /*else if (item instanceof Header) {

                }*/
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



    //EDITED HERE FOR HEADER



    private void addCourseMetaData(ItemAdapter headerAdapter, final ItemAdapter itemAdapter) {


        courseUIdsEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                noCourseState = 0;

                final ArrayList<String> newEnrolledCourseUIdList = new ArrayList<>();

                Log.e(TAG, " enrolled courselist "+enrolledCourseUIdList.size());
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    String courseUid = noteSnapshot.getValue(String.class);

                    if (!enrolledCourseUIdList.contains(courseUid)) {
                        newEnrolledCourseUIdList.add(courseUid);
                    }
                }

                Log.e(TAG, " newenrolled courselist "+newEnrolledCourseUIdList.size());
                if(isTeacher && !isStudent){
                    getEnrolledCoursesMetaDataTeacher(newEnrolledCourseUIdList, itemAdapter , progressDialog);
                }
                else if(!isTeacher && isStudent){
                    getAllCoursesMetaDataStudent(newEnrolledCourseUIdList,enrolledCourseUIdList,
                            availableCourseUIdList, itemAdapter, progressDialog);
                }





                //courseUIdsEndPoint.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });



        Handler handler = new Handler();  // call should be state alert!!!!!!!!!!!!
        handler.postDelayed(new Runnable() {
            public void run() {
                if(noCourseState == 1) {
                    if (!isTeacher && isStudent) {
                        getAllCoursesMetaDataStudent(new ArrayList<String>(), enrolledCourseUIdList,
                                availableCourseUIdList, itemAdapter, progressDialog);
                    } else {
                        progressDialog.dismiss();
                    }
                }
                else{
                    progressDialog.dismiss();
                }
            }
        }, 3000);

    }


    private void getEnrolledCoursesMetaDataTeacher(final ArrayList<String> newEnrolledCourseUIdList,
                                                   final ItemAdapter itemAdapter, final ProgressDialog progressDialog){

        for (int i = 0; i < newEnrolledCourseUIdList.size(); i++) {
            final String courseUid = newEnrolledCourseUIdList.get(i);

            DatabaseReference courseMetaDataEndPoint2 = courseMetaDataEndPoint.child("allCoursesMetaData")
                    .child(courseUid);

            final int finalI = i;
            courseMetaDataEndPoint2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String courseName = dataSnapshot.child("courseName").getValue(String.class);
                    String courseTeacher = dataSnapshot.child("courseTeacher").getValue(String.class);
                    String courseCode = dataSnapshot.child("courseCode").getValue(String.class);
                    String passcode = dataSnapshot.child("passcode").getValue(String.class);

                    itemAdapter.add(new CourseMetaDataView().withCourseName(courseName)
                            .withCourseTeacher(courseTeacher)
                            .withCourseCode(courseCode)
                            .withCourseUId(courseUid)
                            .withPasscode(passcode)
                            .withHeader("Your Courses").withIdentifier(1000 + enrolledCourseCount));

                    enrolledCourseUIdList.add(courseUid);
                    enrolledCourseCount++;

                    if(finalI == newEnrolledCourseUIdList.size() - 1){
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }



    private void getAllCoursesMetaDataStudent(final ArrayList<String> newEnrolledCourseUIdList,
                                              final ArrayList<String> alreadyEnrolledCourseUIdList,
                                              final ArrayList<String> alreadyavailableCourseUIdList,
                                              final ItemAdapter itemAdapter, final ProgressDialog progressDialog){

        Log.e(TAG, "size newenrolled = "+newEnrolledCourseUIdList.size()+" already enrolled "+
                alreadyEnrolledCourseUIdList.size() +" already avail "+alreadyavailableCourseUIdList.size());

        /*final List<IItem> items;
        final List<IItem> unEnrolledItems;*/

        DatabaseReference courseMetaDataEndPoint2 = courseMetaDataEndPoint.child("allCoursesMetaData");
        courseMetaDataEndPoint2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    CourseMetaData courseMetaData2 = noteSnapshot.getValue(CourseMetaData.class);

                    String courseUIdKey = noteSnapshot.getKey();

                    if (courseMetaData2 != null) {
                        if (newEnrolledCourseUIdList.contains(courseUIdKey)
                                && !enrolledCourseUIdList.contains(courseUIdKey)) {


                            List<IItem> items = new ArrayList<>();
                            items.add(new CourseMetaDataView().withCourseName(courseMetaData2.getCourseName())
                                    .withCourseTeacher(courseMetaData2.getCourseTeacher())
                                    .withCourseCode(courseMetaData2.getCourseCode())
                                    .withCourseUId(courseMetaData2.courseUId)
                                    .withPasscode(courseMetaData2.passcode)
                                    .withHeader("Enrolled Courses").withIdentifier(1000 + enrolledCourseCount));
                            itemAdapter.add(0, items);


                            enrolledCourseUIdList.add(courseUIdKey);
                            enrolledCourseCount++;

                        }
                        else if (!alreadyavailableCourseUIdList.contains(courseUIdKey)
                                && !enrolledCourseUIdList.contains(courseUIdKey)) {

                            itemAdapter.add(new CourseMetaDataView().withCourseName(courseMetaData2.getCourseName())
                                    .withCourseTeacher(courseMetaData2.getCourseTeacher())
                                    .withCourseCode(courseMetaData2.getCourseCode())
                                    .withCourseUId(courseMetaData2.courseUId)
                                    .withPasscode(courseMetaData2.passcode)
                                    .withHeader("Available Courses").withIdentifier(2000 + unEnrolledCourseCount));
                            unEnrolledCourseCount++;

                            alreadyavailableCourseUIdList.add(courseUIdKey);

                        }
                    }

                }

                progressDialog.dismiss();
                /*itemAdapter.add(items);
                itemAdapter.add(unEnrolledItems);*/

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


    private void checkInputPasscode(IItem item, final int position, final ItemAdapter itemAdapter){

        final String coursePasscode = ((CourseMetaDataView) item).passcode;
        final String courseName = (String) ((CourseMetaDataView) item).courseName.getText();
        final String courseTeacher = (String) ((CourseMetaDataView) item).courseTeacher.getText();
        final String courseCode = (String) ((CourseMetaDataView) item).courseCode.getText();
        final String courseUId = ((CourseMetaDataView) item).courseUId;

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.course_passcode_input, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        TextView alertDialogTextView = (TextView) findViewById(R.id.alertdialogTextView);
        alertDialogTextView.setText("Enter the Passcode for "+courseCode);
        alertDialogBuilder.setTitle("Passcode");

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.passcodeInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String inputPasscode = String.valueOf(userInput.getText());

                                if(inputPasscode.equals(coursePasscode)){
                                    Log.e(TAG, "correct Passcode");

                                    itemAdapter.remove(position);

                                    /*itemAdapter.add(new CourseMetaDataView().withCourseName(courseName)
                                            .withCourseTeacher(courseTeacher)
                                            .withCourseCode(courseCode)
                                            .withCourseUId(courseUId)
                                            .withPasscode(coursePasscode)
                                            .withHeader("Enrolled Courses").withIdentifier(1000 + enrolledCourseCount));
                                    enrolledCourseCount++;*/

                                    mDatabase.child("institution").child(institution).child("users")
                                            .child(userId).child("courseUIds").child(courseUId)
                                            .setValue(courseUId);

                                    mDatabase.child("institution").child(institution).child("allCoursesData")
                                            .child(courseUId).child("StudentList").child(userId)
                                            .setValue(userId);


                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Wrong Passcode Entered",Toast.LENGTH_SHORT).show();
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

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
        //Edited here for header
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

