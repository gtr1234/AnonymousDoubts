package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.example.karthik.anonymousdoubts.Chat.ChatActivity;
import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.LectureMetaData;
import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.WeekMetaData;
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
    static int week_count =0;
    String userId;
    String week = "Week ";
    private static String institution;



    //private ArrayList<WeekMetaData> weekMetaDataArrayList = new ArrayList<>();;




    private ExpandableListView listView;
    private MyNewExpandableListAdapter listAdapter;
    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String,List<String>> listHash = new HashMap<>();


    public Course_LectureFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

                final ArrayList<WeekMetaData> weekMetaDataArrayList = new ArrayList<>();

                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {

                    final String weekId = noteSnapshot.getValue(String.class);
                    weekDataEndPoint.child(weekId).addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String startDate = dataSnapshot.child("StartDate").getValue(String.class);
                            final String endDate   = dataSnapshot.child("EndDate").getValue(String.class);
                            final String count_week      = Integer.toString(week_count);
                            week_count++;
                            //Log.e(TAG,week+week_count+" "+startDate+"  "+endDate);


                            DatabaseReference LectureIdEndPoint = weekDataEndPoint.child(weekId).child("lectureIds");
                            final ArrayList<LectureMetaData> temp_lectureData = new ArrayList<>();
                            LectureIdEndPoint.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {



                                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                                    {

                                        final String lectureData = dataSnapshot1.getValue(String.class);

                                        lectureDataEndPoint.child(lectureData).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {


                                                String date = dataSnapshot.child("date").getValue(String.class);
                                                String day  = dataSnapshot.child("day").getValue(String.class);

                                                LectureMetaData lectureMetaData = new LectureMetaData(lectureData,date,day);
                                                if(!temp_lectureData.contains(lectureMetaData)) {
                                                    temp_lectureData.add(lectureMetaData);
                                                }
                                                //temp.add(day+" - "+date);
                                                //Log.e(TAG,week+week_count+" "+date+"  "+day);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }

                                    //listHash.put(week+count_week,temp);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            //EDITED HERE

                            WeekMetaData weekMetaData = new WeekMetaData(Integer.toString(week_count),weekId,startDate,endDate,temp_lectureData);
                            if(!weekMetaDataArrayList.contains(weekMetaData)) {
                                weekMetaDataArrayList.add(weekMetaData);
                            }
                            //Log.e(TAG,"Added values "+weekMetaData.weekUId+" "+weekMetaData.getStartDate()+" "+weekMetaData.getEndDate());
                            //temp_lectureData.clear();

                            listAdapter = new MyNewExpandableListAdapter(getActivity(),weekMetaDataArrayList);
                            //Log.d(TAG,"Came here");


                            listView.setAdapter(listAdapter);

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
        week_count =0;


        //Log.e(TAG,"Count = "+Integer.toString(weekMetaDataArrayList.size()));

        //initData();

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                String lectureID = listAdapter.getUid(i,i1);

                intent.putExtra("lectureID",lectureID);
                startActivity(intent);

                return true;
            }
        });
        //listView.setOnChildClickListener();


        //listHash.clear();

        return v;
    }



}
