package com.example.karthik.anonymousdoubts;

/**
 * Created by Karthik on 21-Oct-17.
 */

public class CourseMetaData {
    private String courseName;
    private String courseTeacher;
    private String courseCode;

    public CourseMetaData() {
    }

    public CourseMetaData(String courseName, String courseTeacher, String courseCode) {
        this.courseName = courseName;
        this.courseTeacher = courseTeacher;
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
