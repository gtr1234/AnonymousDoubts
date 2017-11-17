package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter_extensions.drag.IDraggable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Karthik on 16-Nov-17.
 */

public class CourseMetaDataView extends AbstractItem<CourseMetaDataView, CourseMetaDataView.ViewHolder> implements IDraggable<CourseMetaDataView, IItem> {

    public String header;
    public StringHolder courseName;
    public StringHolder courseTeacher;
    public StringHolder courseCode;

    private boolean mIsDraggable = true;

    public CourseMetaDataView withHeader(String header) {
        this.header = header;
        return this;
    }

    public CourseMetaDataView withCourseName(String CourseName) {
        this.courseName = new StringHolder(CourseName);
        return this;
    }

    public CourseMetaDataView withCourseTeacher(String CourseTeacher) {
        this.courseTeacher = new StringHolder(CourseTeacher);
        return this;
    }


    public CourseMetaDataView withCourseCode(String CourseCode) {
        this.courseCode = new StringHolder(CourseCode);
        return this;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.list_row_relative_layout;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.course_discovery_list_row;
    }

    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public boolean isDraggable() {
        return mIsDraggable;
    }

    @Override
    public CourseMetaDataView withIsDraggable(boolean draggable) {
        this.mIsDraggable = draggable;
        return this;
    }


    protected static class ViewHolder extends FastAdapter.ViewHolder<CourseMetaDataView> {
        protected View view;
        @BindView(R.id.courseName)
        TextView _courseName;
        @BindView(R.id.courseTeacher)
        TextView _courseTeacher;
        @BindView(R.id.courseCode)
        TextView _courseCode;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @Override
        public void bindView(CourseMetaDataView item, List<Object> payloads) {
            StringHolder.applyTo(item.courseName, _courseName);
            StringHolder.applyTo(item.courseTeacher, _courseTeacher);
            StringHolder.applyTo(item.courseCode, _courseCode);
        }

        @Override
        public void unbindView(CourseMetaDataView item) {
            _courseName.setText(null);
            _courseTeacher.setText(null);
            _courseCode.setText(null);
        }
    }

}
