package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import java.util.ArrayList;

public class WeekMetaData {

    public String count_week;
    public String weekUId;
    public String startDate;
    public String endDate;

    public ArrayList<LectureMetaData> lectureMetaData;

    public WeekMetaData(String count_week,String weekUId, String startDate, String endDate, ArrayList<LectureMetaData> lectureMetaData){

        this.count_week = count_week;
        this.lectureMetaData = lectureMetaData;
        this.weekUId = weekUId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getWeekUId(){
        return weekUId;
    }
    public String getStartDate(){
        return startDate;
    }
    public String getEndDate(){
        return endDate;
    }
    public String getWeek_count(){
        return count_week;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekMetaData that = (WeekMetaData) o;

        if (count_week != null ? !count_week.equals(that.count_week) : that.count_week != null)
            return false;
        if (weekUId != null ? !weekUId.equals(that.weekUId) : that.weekUId != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null)
            return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        return lectureMetaData != null ? lectureMetaData.equals(that.lectureMetaData) : that.lectureMetaData == null;
    }

    @Override
    public int hashCode() {
        int result = count_week != null ? count_week.hashCode() : 0;
        result = 31 * result + (weekUId != null ? weekUId.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (lectureMetaData != null ? lectureMetaData.hashCode() : 0);
        return result;
    }
}
