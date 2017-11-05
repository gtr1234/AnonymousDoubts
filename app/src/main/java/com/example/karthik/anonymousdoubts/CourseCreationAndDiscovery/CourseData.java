package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import java.util.ArrayList;

/**
 * Created by Karthik on 05-Nov-17.
 */

public class CourseData {

    public String courseUId;

    public String courseDescription;
    public ArrayList<String> studentUIds;
    public ArrayList<String> weekUids;

    public CourseData(String courseDescription, ArrayList<String> studentUIds, ArrayList<String> weekUids) {
        this.courseDescription = courseDescription;
        this.studentUIds = studentUIds;
        this.weekUids = weekUids;
    }

}
