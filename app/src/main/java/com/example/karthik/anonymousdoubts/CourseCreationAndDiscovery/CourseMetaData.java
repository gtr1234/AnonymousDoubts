package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

/**
 * Created by Karthik on 21-Oct-17.
 */

public class CourseMetaData {
    private String courseName;
    private String courseTeacher;
    private String courseCode;

    public String courseUId;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseMetaData that = (CourseMetaData) o;

        if (!courseName.equals(that.courseName)) return false;
        if (!courseTeacher.equals(that.courseTeacher)) return false;
        return courseCode.equals(that.courseCode);

    }

    @Override
    public int hashCode() {
        int result = courseName.hashCode();
        result = 31 * result + courseTeacher.hashCode();
        result = 31 * result + courseCode.hashCode();
        return result;
    }
}
