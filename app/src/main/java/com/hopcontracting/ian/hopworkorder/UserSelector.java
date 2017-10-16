package com.hopcontracting.ian.hopworkorder;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSelector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton customerButton = (ImageButton) findViewById(R.id.customerButton);
        ImageButton workerButton = (ImageButton) findViewById(R.id.workerButton);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToNextActivity = new Intent(getApplicationContext(), CustomerRequestForm.class);
                startActivity(goToNextActivity);
            }
        });

        workerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent goToNextActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToNextActivity);
            }
        });

        // Check if we have an access token already
        String token = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("MYTOKEN", "");
        if (!token.equals("")){
            // Try to validate the token
            try {
                attemptVerify(token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
    private void attemptVerify(String token) throws JSONException{
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        String url =getString(R.string.global_url)+"/verify/";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("token", token);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Intent goToNextActivity = new Intent(getApplicationContext(), ListActivity.class);
                        startActivity(goToNextActivity);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do nothing

                    }
                });
        requestQueue.add(jsObjRequest);
    }


}
