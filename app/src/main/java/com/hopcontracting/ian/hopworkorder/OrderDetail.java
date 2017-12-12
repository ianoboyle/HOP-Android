package com.hopcontracting.ian.hopworkorder;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import id.zelory.compressor.Compressor;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.hopcontracting.ian.hopworkorder.Utils.FileUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetail extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    final ArrayList<WorkType> workTypesList = new ArrayList<WorkType>();
    ImageView selectedImageButton;
    ConstraintLayout mConstraintLayout;

    EditText reportField;
    Order order;
    ImageView button1;
    Bitmap bmp1;
    ImageView button2;
    Bitmap bmp2;
    ImageView button3;
    Bitmap bmp3;
    ImageView button4;
    Bitmap bmp4;
    Bitmap compressedBitmap;
    File compressedFile=null;
    File cameraCompressedFile=null;
    String cameraImage;

    private View mProgressView;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private File newfile;
    private File cameraFile;
    final String dir = Environment.getExternalStoragePublicDirectory(".HopConsulting") + "/Folder/";
    //final String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
    private boolean progress=true;

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
        //Scroll View for progress bar

        // Setup EditText
        reportField = (EditText) findViewById(R.id.edit_text_input);
        reportField.setMaxLines(4);
        reportField.setHorizontallyScrolling(false);


        // Setup Image buttons
        button1 = (ImageView) findViewById(R.id.imageButton);
        button2 = (ImageView) findViewById(R.id.imageButton2);
        button3 = (ImageView) findViewById(R.id.imageButton3);
        button4 = (ImageView) findViewById(R.id.imageButton4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OrderDetail.this.selectedImageButton = button1;
                selectedImageButton=button1;

                showPickImageDialog();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OrderDetail.this.selectedImageButton = button2;
                selectedImageButton=button2;
                showPickImageDialog();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageButton=button3;


                showPickImageDialog();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageButton=button4;

                showPickImageDialog();
            }
        });

        ImageButton completeOrderButton = (ImageButton) findViewById(R.id.complete_order_button);
        // Setup items for spinner
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderDetail.this, android.R.layout.simple_spinner_item, spinnerArray);
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
                                        final WorkTypeAdapater workTypeAdapater = new WorkTypeAdapater(OrderDetail.this, workTypesList);
                                        mWorkTypesListView.setAdapter(workTypeAdapater);

                                        workTypeAdapater.notifyDataSetChanged();

                                        mWorkTypesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                AlertDialog alertDialog = new AlertDialog.Builder(OrderDetail.this).create();
                                                alertDialog.setTitle("Warning");
                                                final WorkType temp = (WorkType) mWorkTypesListView.getItemAtPosition(i);
                                                alertDialog.setMessage("Do you want to delete " + temp.getTitle() +  " from the list of job types?");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                workTypesList.remove(temp);
                                                                final WorkTypeAdapater workTypeAdapater = new WorkTypeAdapater(OrderDetail.this, workTypesList);
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
                        AlertDialog alertDialog = new AlertDialog.Builder(OrderDetail.this).create();
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


        // Capture the layout's TextView and set the string as its text
        final  ImageButton button = (ImageButton) findViewById(R.id.complete_order_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText editText = (EditText) findViewById(R.id.edit_text_input);

                View focusView = null;
                Boolean cancel = false;

                // Check for a valid email address.
                if (editText.getText().toString().isEmpty()) {
                    editText.setError(getString(R.string.report_field_required));
                    focusView = editText;
                    cancel = true;
                }
                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {

                    showProgress(true);

                    final Intent intent = new Intent(OrderDetail.this, CustomerFeedbackActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



                    String url = getString(R.string.global_url) + "/works/" + order.id + "/";

                    VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            String resultResponse = new String(response.data);
                            // parse success output
                            showProgress(false);
                            intent.putExtra("Order", order);
                            startActivity(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            showProgress(false);
                            AlertDialog alertDialog = new AlertDialog.Builder(OrderDetail.this).create();
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
                            params.put("report", editText.getText().toString());
                            String jobTypes = "";
                            for (int i = 0; i < workTypesList.size(); i++) {
                                jobTypes += workTypesList.get(i).getId() + ",";
                            }
                            params.put("job_types", jobTypes);
                            return params;
                        }

                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            // file name could found file base or direct access from real path
//                        // for now just get bitmap data from ImageView
                            if (bmp1 != null) {
                                params.put("photo1", new DataPart("photo1.png", AppHelper.getFileDataFromBitmap(getBaseContext(), bmp1), "image/png"));
                            }
                            if (bmp2 != null) {
                                params.put("photo2", new DataPart("photo2.png", AppHelper.getFileDataFromBitmap(getBaseContext(), bmp2), "image/png"));
                            }
                            if (bmp3 != null) {
                                params.put("photo3", new DataPart("photo3.png", AppHelper.getFileDataFromBitmap(getBaseContext(), bmp3), "image/png"));
                            }
                            if (bmp4 != null) {
                                params.put("photo4", new DataPart("photo4.png", AppHelper.getFileDataFromBitmap(getBaseContext(), bmp4), "image/png"));
                            }
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Authorization", "JWT " + PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("MYTOKEN", ""));
                            return params;
                        }
                    };
                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
                }
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
                        if (which == 0){
                            /*Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code*/
                            /*Intent pickPhoto = new Intent();
                            pickPhoto.setType("image*//*");
                            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(pickPhoto,"Select a picture"), 1);*/
                            //Picking image from gallery
                            if (ContextCompat
                                    .checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat
                                        .requestPermissions(OrderDetail.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select a Picture"), 1);

                        }
                        if (which == 1){
                            //Getting Image from camera
                            /*Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);//zero can be replaced with any action code*/

                            if (ContextCompat
                                    .checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat
                                        .requestPermissions(OrderDetail.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                            }

                            cameraImage = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".png";
                            File folder=new File(dir);
                            cameraFile = new File(cameraImage);
                            try
                            {
                                folder.mkdirs();
                                cameraFile.createNewFile();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                           // Uri outputFileUri = Uri.fromFile(cameraFile);
                            Uri outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", cameraFile);
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivityForResult(cameraIntent, 0);
                        }
                    }
                });
        builderSingle.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode != 1) {
            /*Uri uriPhoto = data.getData();
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //selectedImageButton.setImageURI(uriPhoto);

            if (selectedImageButton == button1){
                bmp1 = photo;
            }
            if (selectedImageButton == button2){
                bmp2 = photo;
            }
            if (selectedImageButton == button3){
                bmp3 = photo;
            }
            if (selectedImageButton == button4){
                bmp4 = photo;
            }*/
            Log.i("Demo Pic", Long.toString(cameraFile.getTotalSpace()));
            try
            {
                cameraCompressedFile = new Compressor(getApplicationContext())
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG).compressToFile(cameraFile);
                Log.i("Demo Pic", Long.toString(cameraCompressedFile.length()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            /*Picasso.with(getApplicationContext()).load(Uri.fromFile(compressedFile)).into(ivAvatar);*/
            compressedBitmap = BitmapFactory.decodeFile(cameraCompressedFile.getAbsolutePath());
            if (selectedImageButton == button1){
                Picasso.with(getApplicationContext())
                        .load(cameraCompressedFile)
                        .into(button1);
                bmp1=compressedBitmap;
            }
            if (selectedImageButton == button2){
                Picasso.with(getApplicationContext())
                        .load(cameraCompressedFile)
                        .into(button2);
                bmp2=compressedBitmap;
            }
            if (selectedImageButton == button3){
                Picasso.with(getApplicationContext())
                        .load(cameraCompressedFile)
                        .into(button3);
                bmp3=compressedBitmap;
            }
            if (selectedImageButton == button4){
                Picasso.with(getApplicationContext())
                        .load(cameraCompressedFile)
                        .into(button4);
                bmp4=compressedBitmap;
            }


        }
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            final InputStream imageStream;
                /*Uri uriPhoto = data.getData();
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageButton.setImageURI(uriPhoto);

                if (selectedImageButton == button1){
                    bmp1 = photo;
                }
                if (selectedImageButton == button2){
                    bmp2 = photo;
                }
                if (selectedImageButton == button3){
                    bmp3 = photo;
                }
                if (selectedImageButton == button4){
                    bmp4 = photo;
                }*/
            Uri uriPhoto = data.getData();
            String imageFilePath = FileUtils.getPath(OrderDetail.this, uriPhoto);
            /*Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,50,outputstream);

            byte[] byteArray = outputstream.toByteArray();

            compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);*/
            newfile = new File(imageFilePath);
            Log.d("OrderDetail", "onActivityResult: "+String.valueOf(newfile.length()));

            try
            {
                compressedFile = new Compressor(getApplicationContext())
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG).compressToFile(newfile);
                Log.i("Demo Pic", Long.toString(compressedFile.length()));
            }
            catch (Exception e)
            {}
             compressedBitmap = BitmapFactory.decodeFile(compressedFile.getAbsolutePath());

            if (selectedImageButton == button1){
                Picasso.with(getApplicationContext())
                        .load(compressedFile)
                        .into(button1);
                bmp1=compressedBitmap;
            }
            if (selectedImageButton == button2){
                Picasso.with(getApplicationContext())
                        .load(compressedFile)
                        .into(button2);
                bmp2=compressedBitmap;
            }
            if (selectedImageButton == button3){
                Picasso.with(getApplicationContext())
                        .load(compressedFile)
                        .into(button3);
                bmp3=compressedBitmap;
            }
            if (selectedImageButton == button4){
                Picasso.with(getApplicationContext())
                        .load(compressedFile)
                        .into(button4);
                bmp4=compressedBitmap;
            }

           /* Picasso.with(getApplicationContext())
                    .load(compressedFile)
                    .into(button2);*/
        }
        selectedImageButton = null;
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
        if (show)
        {
            progress=false;
        }
        else{
            progress = true;
        }
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.constraint_layout);
        mProgressView = (ProgressBar) findViewById(R.id.order_detail_progress);
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

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (progress) {
            super.onBackPressed();
        }
        */
    }
}
