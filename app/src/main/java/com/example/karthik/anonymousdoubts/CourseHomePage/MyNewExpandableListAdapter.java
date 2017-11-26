package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.WeekMetaData;
import com.example.karthik.anonymousdoubts.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MyNewExpandableListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "MyNewExpandableListView";
    private Context context;
    private ArrayList<WeekMetaData> weekMetaData;
    private HashMap<String,List<String>> listHashMap;

    public MyNewExpandableListAdapter(Context context, ArrayList<WeekMetaData> weekMetaData){

        this.context = context;
        this.weekMetaData = weekMetaData;
    }

    @Override
    public int getGroupCount() {

        return weekMetaData.size();
    }

    @Override
    public int getChildrenCount(int i) {


        return weekMetaData.get(i).lectureMetaData.size();
    }

    public Object getDates(int i){

        String date_string="";
        String startDate = getDateFormat(weekMetaData.get(i).getStartDate());
        String endDate   = getDateFormat(weekMetaData.get(i).getEndDate());

        date_string = startDate+" - "+endDate;
        return date_string;
    }

    public String getDateFormat(String dateFormat){

        String []format = dateFormat.split("/");
        String date = format[0];
        String month = "";
        String year  = format[2];

        switch (format[1]){

            case "01":
                month = " Jan '";
                break;
            case "02":
                month = " Feb '";
                break;
            case "03":
                month = " Mar '";
                break;
            case "04":
                month = " Apr '";
                break;
            case "05":
                month = " May '";
                break;
            case "06":
                month = " Jun '";
                break;
            case "07":
                month = " Jul '";
                break;
            case "08":
                month = " Aug '";
                break;
            case "09":
                month = " Sep '";
                break;
            case "10":
                month = " Oct '";
                break;
            case "11":
                month = " Nov '";
                break;
            case "12":
                month = " Dec '";
                break;

                default:
                    break;
        }

        date = date+month+year;
        return date;
    }
    @Override
    public Object getGroup(int i) {


        String week_count = weekMetaData.get(i).getWeek_count();
        //Log.e(TAG,"size = " +Integer.toString(weekMetaData.get(i).lectureMetaData.size()));
        return "Week "+week_count;
        //return "WEEK";
    }

    public String getUid(int i,int i1){

        return weekMetaData.get(i).lectureMetaData.get(i1).lectureId;
    }
    @Override
    public long getChildId(int i, int i1) {

        return i1;
    }

    @Override
    public Object getChild(int i, int i1) {

        String day = weekMetaData.get(i).lectureMetaData.get(i1).getDay();
        String date = getDateFormat(weekMetaData.get(i).lectureMetaData.get(i1).getDate());

        //return "Lecture";
        return day+" - "+date;
    }

    @Override
    public long getGroupId(int i) {

        return i;
    }



    @Override
    public boolean hasStableIds() {

        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        view = null;
        String headerTitle = (String)getGroup(i);
        String dates = (String)getDates(i);

        if(view==null){

            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.elist_view_content,null);

        }
        TextView elistviewHeader = (TextView)view.findViewById(R.id.elistview_header);
        TextView elistviewHeader_sub = (TextView)view.findViewById(R.id.elistview_header_dates);
        elistviewHeader.setTypeface(null, Typeface.NORMAL);
        elistviewHeader_sub.setTypeface(null,Typeface.NORMAL);

        elistviewHeader_sub.setText(dates);
        elistviewHeader.setText(headerTitle);
       // Log.e(TAG,"------headerText--------"+headerTitle);
        return view;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final String childText = (String)getChild(i,i1);
        Log.e(TAG,"----childText----"+childText);
        view = null;
        if(view==null){

            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.elist_view_items,null);
        }
        TextView txtListChild = (TextView)view.findViewById(R.id.elistview_item);

        txtListChild.setText(childText);

        return view;

    }


    @Override
    public boolean isChildSelectable(int i, int i1) {

        return true;
    }
}