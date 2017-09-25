package com.example.ian.hopworkorder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView mListView = (ListView) findViewById(R.id.recipe_list_view);

        final ArrayList<Order> orderList = Order.getOrdersFromFile("orders.json", this);
        final OrderAdapter adapter = new OrderAdapter(this, orderList);
        mListView.setAdapter(adapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Order item = (Order) adapter.getItem(i);
                Intent intent = new Intent(ListActivity.this,OrderDetail.class);
                //based on item add info to intent
                startActivity(intent);
            };
        });

    }
}
