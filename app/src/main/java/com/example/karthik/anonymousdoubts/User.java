package com.example.karthik.anonymousdoubts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karthik on 22-Oct-17.
 */

public class User {

    public String email;
    public String name;
    public boolean isStudent;
    public boolean isTeacher;
    public ArrayList<CourseMetaData> courseMetaDataArrayList;

    public User() {
    }

    public User(String email, String name, boolean isStudent, boolean isTeacher,
                ArrayList<CourseMetaData> courseMetaDataArrayList) {
        this.email = email;
        this.name = name;
        this.isStudent = isStudent;
        this.isTeacher = isTeacher;
        this.courseMetaDataArrayList = courseMetaDataArrayList;
    }
}
