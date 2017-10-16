package com.hopcontracting.ian.hopworkorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRequestForm extends AppCompatActivity {

    ListView mWorkTypesListView;
    EditText mPhoneNumberEditText;
    EditText mEmailEditText;
    EditText mNotesEditText;
    Button submitButton;
    ConstraintLayout mConstraintLayout;

    final ArrayList<WorkType> workTypesList = new ArrayList<WorkType>();
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_request_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submitButton = (Button) findViewById(R.id.submit_button);

        mEmailEditText = (EditText) findViewById(R.id.edit_text_email);
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_text_input_phone_number);
        mPhoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        mNotesEditText = (EditText) findViewById(R.id.edit_text_input_notes);
        mNotesEditText.setMaxLines(4);
        mNotesEditText.setHorizontallyScrolling(false);

        final ListView mWorkTypesListView = (ListView) findViewById(R.id.work_types_list_view);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.global_url) + "/job_types/";
        JsonArrayRequest localJReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                final ArrayList<WorkType> workTypes = WorkType.getWorksTypesFromJsonArray(response);
                final List<String> spinnerArray =  new ArrayList<String>();
                spinnerArray.add("Select Work Type");
                for(int i = 0; i < workTypes.size(); i++) {
                    spinnerArray.add(workTypes.get(i).getTitle());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CustomerRequestForm.this, android.R.layout.simple_spinner_item, spinnerArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                final Spinner sItems = (Spinner) findViewById(R.id.work_type_spinner);
                sItems.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position != 0) {
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            final WorkType workType = workTypes.get(position - 1);
                            if (!workTypesList.contains(workType) && !workType.getTitle().equals("Select Work Type")) {
                                workTypesList.add(workType);
                                final WorkTypeAdapater workTypeAdapater = new WorkTypeAdapater(CustomerRequestForm.this, workTypesList);
                                mWorkTypesListView.setAdapter(workTypeAdapater);
                                workTypeAdapater.notifyDataSetChanged();

                                mWorkTypesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        AlertDialog alertDialog = new AlertDialog.Builder(CustomerRequestForm.this).create();
                                        alertDialog.setTitle("Warning");
                                        final WorkType temp = (WorkType) mWorkTypesListView.getItemAtPosition(i);
                                        alertDialog.setMessage("Do you want to delete" + temp.getTitle() +  "from the list of job types?");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes",
                                                new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                workTypesList.remove(temp);
                                                final WorkTypeAdapater workTypeAdapater = new WorkTypeAdapater(CustomerRequestForm.this, workTypesList);
                                                mWorkTypesListView.setAdapter(workTypeAdapater);
                                                workTypeAdapater.notifyDataSetChanged();
                                            }
                                        });
                                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "No",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                });
                                        alertDialog.show();
                                    };
                                });
                            }
                            sItems.setSelection(0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Do nothing
                    }
                });
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog alertDialog = new AlertDialog.Builder(CustomerRequestForm.this).create();
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
                }) {//here before semicolon ; and use { }.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                return params;            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        requestQueue.add(localJReq);


        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try {
                    attemptSubmit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void makeRequest() throws JSONException {
        String url = getString(R.string.global_url) + "/request/";

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                // parse success output
                showProgress(false);
                AlertDialog alertDialog = new AlertDialog.Builder(CustomerRequestForm.this).create();
                alertDialog.setTitle("Success!");
                alertDialog.setMessage("You have successfully submitted your request!");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                showProgress(false);
                AlertDialog alertDialog = new AlertDialog.Builder(CustomerRequestForm.this).create();
                alertDialog.setTitle("Failure!");
                alertDialog.setMessage("We are sorry, something went wrong, please try again later!");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("notes", mNotesEditText.getText().toString());
                params.put("email", mEmailEditText.getText().toString());
                params.put("phone", mPhoneNumberEditText.getText().toString());
                String jobTypes = "";
                for (int i = 0; i < workTypesList.size(); i++) {
                    jobTypes += workTypesList.get(i).getId() + ",";
                }
                params.put("types", jobTypes);
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }


    private void attemptSubmit() throws JSONException {

        // Reset errors.
        mEmailEditText.setError(null);
        mPhoneNumberEditText.setError(null);
        mNotesEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailEditText.getText().toString();
        String phoneNumber = mPhoneNumberEditText.getText().toString();
        String notes = mNotesEditText.getText().toString();

        boolean cancel = false;
        boolean typesFlag = false;
        View focusView = null;

        // Check for a valid email address.
        if (email.isEmpty()) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        }
        if (phoneNumber.isEmpty()) {
            mPhoneNumberEditText.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberEditText;
            cancel = true;
        }

        if (notes.isEmpty()) {
            mNotesEditText.setError(getString(R.string.error_field_required));
            focusView = mNotesEditText;
            cancel = true;
        }
        if (workTypesList.size() == 0){
            typesFlag = true;

        }


        if (cancel || typesFlag) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (cancel) {
                focusView.requestFocus();
            }
            if (typesFlag){
                AlertDialog alertDialog = new AlertDialog.Builder(CustomerRequestForm.this).create();
                alertDialog.setTitle("Missing Work Type");
                alertDialog.setMessage("Please select one type of Work Type by pressing on the Select Work Type button");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                alertDialog.show();
            }
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            makeRequest();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        // Setup Progress View
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.customer_request_form_layout);
        mProgressView = (ProgressBar) findViewById(R.id.request_progress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mConstraintLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mConstraintLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mConstraintLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mConstraintLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
