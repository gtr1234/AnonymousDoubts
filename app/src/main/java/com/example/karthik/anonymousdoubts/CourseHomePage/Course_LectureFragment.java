package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Expandable List View Implemented from Here


//Expandable List View ENDS HERE


public class Course_LectureFragment extends Fragment {


    public Course_LectureFragment() {
        // Required empty public constructor
    }

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

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
        initData();
        listAdapter = new ExpandableListAdapter(getActivity(),listDataHeader,listHash);
        listView.setAdapter(listAdapter);
        return v;
    }



}
