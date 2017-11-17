package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.materialize.holder.StringHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Karthik on 17-Nov-17.
 */

public class CourseMetaDataHeader extends AbstractItem<CourseMetaDataHeader, CourseMetaDataHeader.ViewHolder> {

    public StringHolder title;

    public CourseMetaDataHeader withTitle(String title) {
        this.title = new StringHolder(title);
        return this;
    }

    // Your getter setters here

    // AbstractItem methods
    @Override
    public int getType() {
        return R.id.course_list_header_rlayout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.course_meta_data_header_item;
    }


    @Override
    public ViewHolder getViewHolder(View v) {
        return null;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.header_text) TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
