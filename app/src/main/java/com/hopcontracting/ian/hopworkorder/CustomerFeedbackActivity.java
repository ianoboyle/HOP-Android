package com.hopcontracting.ian.hopworkorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class CustomerFeedbackActivity extends AppCompatActivity {
    View mFormView;
    Order order;

    ProgressBar mProgressBar;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_feedback);
        final RatingBar mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingBar.setRating(3);
        this.order = (Order) getIntent().getSerializableExtra("Order");
        Button mButton = (Button) findViewById(R.id.button);

        mProgressBar = (ProgressBar) findViewById(R.id.customer_feedback_progress);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.customer_feedback_constraint_layout);

        final SignaturePad signature = (SignaturePad) findViewById(R.id.signature_pad);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent goToNextActivity = new Intent(getApplicationContext(), ListActivity.class);
                goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                goToNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                String url = getString(R.string.global_url) + "/works/"+order.id+"/";
                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        // parse success output
                        showProgress(false);
                        AlertDialog alertDialog = new AlertDialog.Builder(CustomerFeedbackActivity.this).create();
                        alertDialog.setTitle("Thank you!");
                        alertDialog.setMessage("Your Order has been completed");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        startActivity(goToNextActivity);
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        AlertDialog alertDialog = new AlertDialog.Builder(CustomerFeedbackActivity.this).create();
                        alertDialog.setTitle("Failure");
                        alertDialog.setMessage("We are sorry, Something went wrong, please check your connection and try again.");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //
                                    }
                                });
                        alertDialog.show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        Date currentTime = Calendar.getInstance().getTime();
                        Date date = new Date();   // given date
                        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
                        calendar.setTime(date);   // assigns calendar to given date
                        int hours = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
                        int minutes = calendar.get(Calendar.MINUTE);
                        params.put("end_time", hours+":"+minutes);
                        params.put("evaluation", String.valueOf(Math.round(mRatingBar.getRating())));
                        return params;
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        Bitmap bmp = signature.getSignatureBitmap();
                        if (bmp != null){
                            params.put("sign", new DataPart("sign.png", AppHelper.getFileDataFromBitmap(getBaseContext(), bmp), "image/png"));
                        }
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "JWT "+ PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("MYTOKEN", ""));
                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
                showProgress(true);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mConstraintLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mConstraintLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mConstraintLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
