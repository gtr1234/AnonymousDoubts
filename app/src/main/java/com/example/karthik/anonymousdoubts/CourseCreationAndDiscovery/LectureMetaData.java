package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class LectureMetaData {

    public String lectureId;
    public String date;
    public String day;

    public LectureMetaData(String lectureId,String date,String day){

        this.lectureId = lectureId;
        this.date = date;
        this.day  = day;
    }

    public String getLectureId(){
        return lectureId;
    }
    public String getDate(){
        return date;
    }
    public String getDay(){
        return day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LectureMetaData that = (LectureMetaData) o;

        if (lectureId.equals(that.lectureId))
            return true;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        return day != null ? day.equals(that.day) : that.day == null;
    }

    @Override
    public int hashCode() {
        int result = lectureId != null ? lectureId.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }
}