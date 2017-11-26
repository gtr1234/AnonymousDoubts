package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;

import java.util.HashMap;
import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHashMap;

    public MyExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String,List<String>> listHashMap){

        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {

        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {


        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {

        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {

        return listHashMap.get(listDataHeader.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {

        return i;
    }

    @Override
    public long getChildId(int i, int i1) {

        return i1;
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
        return view;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        final String childText = (String)getChild(i,i1);
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