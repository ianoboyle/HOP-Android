package com.hopcontracting.ian.hopworkorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Order> mDataSource;

    public OrderAdapter(Context context, ArrayList<Order> items) {
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
        View rowView = mInflater.inflate(R.layout.order_item, viewGroup, false);

        // Get title element
        TextView titleTextView = (TextView) rowView.findViewById(R.id.order_title);
        // Get subtitle element
        TextView subtitleTextView = (TextView) rowView.findViewById(R.id.order_subtitle);
        // 1
        Order order = (Order) getItem(i);
        // 2
        titleTextView.setText(order.title);
        subtitleTextView.setText(order.description);

        return rowView;
    }


}
