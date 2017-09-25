package com.example.ian.hopworkorder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class OrderDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // you need to have a list of data that you want the spinner to display
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("item1");
        spinnerArray.add("item2");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.work_type_spinner);
        sItems.setAdapter(adapter);


        // Capture the layout's TextView and set the string as its text
        final  Button button = (Button) findViewById(R.id.complete_order_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent intent = new Intent(OrderDetail.this, CustomerFeedbackActivity.class);
                //based on item add info to intent
                startActivity(intent);
            }
        });


        ListView mWorkTypesListView = (ListView) findViewById(R.id.work_types_list_view);

        final ArrayList<WorkType> workTypesList = WorkType.getWorkTypesFromFile("orders.json", this);
        final WorkTypeAdapater workTypeAdapater = new WorkTypeAdapater(this, workTypesList);
        mWorkTypesListView.setAdapter(workTypeAdapater);
    }
}
