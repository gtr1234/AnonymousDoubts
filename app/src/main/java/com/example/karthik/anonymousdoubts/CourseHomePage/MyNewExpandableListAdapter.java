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


    @Override
    public Object getGroup(int i) {

        String startDate = weekMetaData.get(i).getStartDate();
        String endDate   = weekMetaData.get(i).getEndDate();
        String week_count = weekMetaData.get(i).getWeek_count();
        Log.e(TAG,"size = " +Integer.toString(weekMetaData.get(i).lectureMetaData.size()));
        return "Week "+week_count+" "+startDate+" "+endDate;
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
        String date = weekMetaData.get(i).lectureMetaData.get(i1).getDate();

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

        if(view==null){

            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.elist_view_content,null);

        }
        TextView elistviewHeader = (TextView)view.findViewById(R.id.elistview_header);
        elistviewHeader.setTypeface(null, Typeface.BOLD);
        elistviewHeader.setText(headerTitle);
        Log.e(TAG,"------headerText--------"+headerTitle);
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