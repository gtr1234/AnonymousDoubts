package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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


public class Course_StudentsFragment extends Fragment {

    //Edited here for  students list
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference courseUIdsEndPoint;
    private DatabaseReference courseMetaDataEndPoint;
    private DatabaseReference allCoursesDataUIdEndPoint;
    private DatabaseReference studentesListEndPoint;
    private static final String TAG = "CourseStudentsFragment";



    String userId;
    //Edited here
    private static String institution;


    private ListView listView_students;
    private ArrayList<String> list_students;
    private ArrayAdapter<String> student_list_adapter;

    public Course_StudentsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_course__students,container, false);

        String courseUId = this.getArguments().getString("courseUId");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        list_students = new ArrayList<String>();
        student_list_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                list_students);

        mDatabase =  FirebaseDatabase.getInstance().getReference();


        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);
        courseUIdsEndPoint = userIdEndPoint.child("courseUIds");
        courseMetaDataEndPoint = mDatabase.child("institution").child(institution);
        allCoursesDataUIdEndPoint = mDatabase.child("institution").child(institution).child("allCoursesData").child(courseUId);
        studentesListEndPoint = allCoursesDataUIdEndPoint.child("StudentList");




        listView_students = (ListView)v.findViewById(R.id.course_students_list);



        studentesListEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    String studentUId = noteSnapshot.getValue(String.class);

                    DatabaseReference studentEndPoint =  mDatabase.child("institution").child(institution).child("users").child(studentUId);

                    studentEndPoint.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue(String.class);

                            list_students.add(name);
                            //Log.d(TAG,"StudentNumber_in = "+list_students.size());
                            student_list_adapter.notifyDataSetChanged();

                            //Log.d(TAG,"StudentsNumber = "+list_students.size());
                            listView_students.setAdapter(student_list_adapter);
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




        return v;
    }


}
