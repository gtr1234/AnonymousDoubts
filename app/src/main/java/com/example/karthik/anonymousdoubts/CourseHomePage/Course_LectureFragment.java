package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Expandable List View Implemented from Here


//Expandable List View ENDS HERE


public class Course_LectureFragment extends Fragment {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference courseUIdsEndPoint;
    private DatabaseReference courseMetaDataEndPoint;
    private DatabaseReference allCoursesDataUIdEndPoint;
    private DatabaseReference weekIdEndPoint;
    private DatabaseReference weekDataEndPoint;
    private DatabaseReference lectureDataEndPoint;
    private static final String TAG = "CourseLectureFragment";
    static int week_count =1;
    String userId;
    String week = "Week ";
    private static String institution;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String,List<String>> listHash = new HashMap<>();


    public Course_LectureFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    //Function to add junk data
    public void initData(){
        /*
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        */

        listDataHeader.add("Week 1");
        listDataHeader.add("Week 2");
        listDataHeader.add("Week 3");
        listDataHeader.add("Week 4");


        List<String> week1 = new ArrayList<>();
        week1.add("Monday 2:25PM - 3:15PM");
        week1.add("Tuesday 1:30PM - 2:20PM");
        week1.add("Thursday 9:00AM - 9:50AM");

        List<String> week2 = new ArrayList<>();
        week2.add("Monday 10:40AM - 11:30AM");
        week2.add("Wednesday 10:40AM - 11:30AM");
        week2.add("Thursday 11:40AM - 12:30AM");

        List<String> week3 = new ArrayList<>();
        week3.add("Monday 10:40AM - 11:30AM");
        week3.add("Wednesday 10:40AM - 11:30AM");
        week3.add("Friday 9:00AM - 9:50AM");

        List<String> week4 = new ArrayList<>();
        week4.add("Thursday 9:00AM - 9:50AM");
        week4.add("Saturday 9:00AM - 9:50AM");
        week4.add("Sunday 10AM - 11AM");

        listHash.put(listDataHeader.get(0),week1);
        listHash.put(listDataHeader.get(1),week2);
        listHash.put(listDataHeader.get(2),week3);
        listHash.put(listDataHeader.get(3),week4);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_course__lecture, container, false);
        listView = (ExpandableListView) v.findViewById(R.id.expandable_view);

        String courseUId = this.getArguments().getString("courseUId");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);
        courseUIdsEndPoint = userIdEndPoint.child("courseUIds");
        courseMetaDataEndPoint = mDatabase.child("institution").child(institution);
        weekDataEndPoint = mDatabase.child("institution").child(institution).child("weeksData");

        allCoursesDataUIdEndPoint = mDatabase.child("institution").child(institution).child("allCoursesData").child(courseUId);
        weekIdEndPoint = allCoursesDataUIdEndPoint.child("weekIds");
        lectureDataEndPoint = mDatabase.child("institution").child(institution).child("lecturesData");



        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        Log.e(TAG, "progress dialog");
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Lecture Data...");

        progressDialog.show();


        weekIdEndPoint.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {

                    final String weekId = noteSnapshot.getValue(String.class);
                    weekDataEndPoint.child(weekId).addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String startDate = dataSnapshot.child("StartDate").getValue(String.class);
                            final String endDate   = dataSnapshot.child("EndDate").getValue(String.class);
                            final String count_week      = Integer.toString(week_count);
                            week_count++;
                            Log.e(TAG,week+week_count+" "+startDate+"  "+endDate);

                            listDataHeader.add(week+count_week);

                            DatabaseReference LectureIdEndPoint = weekDataEndPoint.child(weekId).child("lectureIds");

                            LectureIdEndPoint.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final List<String> temp = new ArrayList<>();

                                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                                    {

                                        String lectureData = dataSnapshot1.getValue(String.class);

                                        lectureDataEndPoint.child(lectureData).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {



                                                String date = dataSnapshot.child("date").getValue(String.class);
                                                String day  = dataSnapshot.child("day").getValue(String.class);
                                                temp.add(day+" - "+date);

                                                Log.e(TAG,week+week_count+" "+date+"  "+day);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }

                                    listHash.put(week+count_week,temp);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        week_count =1;




        //initData();
        listAdapter = new MyExpandableListAdapter(getActivity(),listDataHeader,listHash);
        listView.setAdapter(listAdapter);
        //listView.setOnChildClickListener();
        listDataHeader.clear();
        //listHash.clear();

        return v;
    }



}
