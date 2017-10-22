package com.example.karthik.anonymousdoubts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Karthik on 22-Oct-17.
 */

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.MyViewHolder>{
    private List<CourseMetaData> courseList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView courseName, courseTeacher, courseCode;

        public MyViewHolder(View view) {
            super(view);
            courseName = (TextView) view.findViewById(R.id.courseName);
            courseTeacher = (TextView) view.findViewById(R.id.courseTeacher);
            courseCode = (TextView) view.findViewById(R.id.courseCode);
        }
    }


    public CourseListAdapter(List<CourseMetaData> courseList) {
        this.courseList = courseList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_discovery_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CourseMetaData courseMetaData = courseList.get(position);
        holder.courseName.setText(courseMetaData.getCourseName());
        holder.courseTeacher.setText(courseMetaData.getCourseTeacher());
        holder.courseCode.setText(courseMetaData.getCourseCode());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

}
