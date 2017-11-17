package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

/**
 * Created by Karthik on 21-Oct-17.
 */

public class CourseMetaData {
    private String courseName;
    private String courseTeacher;
    private String courseCode;

    public String passcode;
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
        if (!(o instanceof CourseMetaData)) return false;

        CourseMetaData that = (CourseMetaData) o;

        if (courseName != null ? !courseName.equals(that.courseName) : that.courseName != null)
            return false;
        if (courseTeacher != null ? !courseTeacher.equals(that.courseTeacher) : that.courseTeacher != null)
            return false;
        if (courseCode != null ? !courseCode.equals(that.courseCode) : that.courseCode != null)
            return false;
        if (passcode != null ? !passcode.equals(that.passcode) : that.passcode != null)
            return false;
        return courseUId != null ? courseUId.equals(that.courseUId) : that.courseUId == null;
    }

    @Override
    public int hashCode() {
        int result = courseName != null ? courseName.hashCode() : 0;
        result = 31 * result + (courseTeacher != null ? courseTeacher.hashCode() : 0);
        result = 31 * result + (courseCode != null ? courseCode.hashCode() : 0);
        result = 31 * result + (passcode != null ? passcode.hashCode() : 0);
        result = 31 * result + (courseUId != null ? courseUId.hashCode() : 0);
        return result;
    }
}
