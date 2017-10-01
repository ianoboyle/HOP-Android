package com.example.ian.hopworkorder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetail extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    final ArrayList<WorkType> workTypesList = new ArrayList<WorkType>();
    ImageButton selectedImageButton;
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        this.order = (Order) getIntent().getSerializableExtra("Order");


        // Setup listView
        final ListView mWorkTypesListView = (ListView) findViewById(R.id.work_types_list_view);


        TextView textView = (TextView) findViewById(R.id.address_text_view);
        textView.setText(order.title);

        // Setup Image buttons
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        final ImageButton button2 = (ImageButton) findViewById(R.id.imageButton2);
        final ImageButton button3 = (ImageButton) findViewById(R.id.imageButton3);
        final ImageButton button4 = (ImageButton) findViewById(R.id.imageButton4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetail.this.selectedImageButton = button1;
                showPickImageDialog();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetail.this.selectedImageButton = button2;
                showPickImageDialog();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetail.this.selectedImageButton = button3;

                showPickImageDialog();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetail.this.selectedImageButton = button4;
                showPickImageDialog();
            }
        });


        // Setup items for spinner


        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url ="http://10.0.0.17:8000/api/job_types/";

        JsonArrayRequest localJReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        final ArrayList<WorkType> workTypes = WorkType.getWorksTypesFromJsonArray(response);
                        final List<String> spinnerArray =  new ArrayList<String>();
                        spinnerArray.add("Select Work Type");
                        for(int i = 0; i < workTypes.size(); i++) {
                            spinnerArray.add(workTypes.get(i).getTitle());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderDetail.this, android.R.layout.simple_spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner sItems = (Spinner) findViewById(R.id.work_type_spinner);
                        sItems.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                if (position != 0) {
                                    WorkType workType = workTypes.get(position - 1);
                                    if (!workTypesList.contains(workType) && !workType.getTitle().equals("Select Work Type")) {
                                        workTypesList.add(workType);
                                        final WorkTypeAdapater workTypeAdapater = new WorkTypeAdapater(OrderDetail.this, workTypesList);
                                        mWorkTypesListView.setAdapter(workTypeAdapater);

                                        workTypeAdapater.notifyDataSetChanged();
                                    }
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
                    }
                }) {//here before semicolon ; and use { }.
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "JWT "+ PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("MYTOKEN", ""));
                return params;            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        requestQueue.add(localJReq);


        // Capture the layout's TextView and set the string as its text
        final  Button button = (Button) findViewById(R.id.complete_order_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(OrderDetail.this, CustomerFeedbackActivity.class);

                String url ="http://10.0.0.17:8000/api/works/"+order.id+"/";



                VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        // parse success output
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        EditText editText = (EditText) findViewById(R.id.edit_text_input);
                        params.put("report", editText.getText().toString());
                        String jobTypes = "";
                        for(int i = 0; i < workTypesList.size(); i++){
                            jobTypes += workTypesList.get(i).getId() + ",";
                        }
                        params.put("job_types", jobTypes);

                        return params;
                    }

                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        // file name could found file base or direct access from real path
                        // for now just get bitmap data from ImageView
                        params.put("photo1", new DataPart("photo1.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), button1.getDrawable()), "image/jpeg"));
                        params.put("photo2", new DataPart("photo2.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), button2.getDrawable()), "image/jpeg"));
                        params.put("photo3", new DataPart("photo3.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), button3.getDrawable()), "image/jpeg"));
                        params.put("photo4", new DataPart("photo4.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), button4.getDrawable()), "image/jpeg"));
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", "JWT "+PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("MYTOKEN", ""));
                        return params;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
            }
        });
    }

    private void showPickImageDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(OrderDetail.this);
        builderSingle.setTitle("Select One Option");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                OrderDetail.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Gallery");
        arrayAdapter.add("Camera");

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent in = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(in, RESULT_LOAD_IMAGE);

                            case 1:
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }

                    }
                });
        builderSingle.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            selectedImageButton.setImageBitmap(photo);
            selectedImageButton = null;
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            selectedImageButton.setImageBitmap((BitmapFactory.decodeFile(picturePath)));
        }
    }
}
