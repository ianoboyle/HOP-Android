package com.example.ian.hopworkorder;

import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;

public class CustomerFeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_feedback);

        RatingBar mRatingBar = (RatingBar) findViewById(R.id.rating);
        mRatingBar.setRating(3);
    }
}
