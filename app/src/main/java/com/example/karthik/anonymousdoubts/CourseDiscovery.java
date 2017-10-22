package com.example.karthik.anonymousdoubts;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CourseDiscovery extends AppCompatActivity {

    private List<CourseMetaData> courseMetaDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CourseListAdapter mAdapter;

    private final String COURSENAME = "Course Name : ";
    private final String COURSETEACHER = "Course Teacher : ";
    private final String COURSECODE = "Course Code : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_discovery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new CourseListAdapter(courseMetaDataList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        addCourseMetaData();

    }

    private void addCourseMetaData() {
        CourseMetaData courseMetaData = new CourseMetaData(COURSENAME+"Software Engineering",
                COURSETEACHER+"B.Sodhi",COURSECODE+"CSL456");
        courseMetaDataList.add(courseMetaData);

        courseMetaData = new CourseMetaData(COURSENAME+"BTP",COURSETEACHER+"Sowmitra",COURSECODE+"CSL555");
        courseMetaDataList.add(courseMetaData);

        mAdapter.notifyDataSetChanged();

    }

}
