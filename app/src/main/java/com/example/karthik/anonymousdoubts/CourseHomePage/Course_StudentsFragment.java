package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.karthik.anonymousdoubts.R;

import java.util.ArrayList;


public class Course_StudentsFragment extends Fragment {

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
        listView_students = (ListView)v.findViewById(R.id.course_students_list);

        list_students = new ArrayList<String>();

        list_students.add("Kamlesh");
        list_students.add("Suresh");
        list_students.add("Ramesh");

        student_list_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,
                list_students);
        listView_students.setAdapter(student_list_adapter);

        return v;
    }


}
