package com.hopcontracting.ian.hopworkorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WorkTypeAdapater extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<WorkType> mDataSource;

    public WorkTypeAdapater(Context context, ArrayList<WorkType> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.work_type_item, viewGroup, false);

        // Get title element
        TextView titleTextView = (TextView) rowView.findViewById(R.id.work_type_title);
        // 1
        WorkType workType = (WorkType) getItem(i);
        // 2
        titleTextView.setText(workType.getTitle());

        return rowView;
    }


}
