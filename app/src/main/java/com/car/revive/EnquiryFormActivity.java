package com.car.revive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.car.revive.Adapters.ImagesAdapter;
import com.car.revive.Models.ImagesModel;
import com.car.revive.Util.AsyncResult;
import com.car.revive.Util.Network;
import com.car.revive.Util.Utility;
import com.car.revive.Util.VolleyMultipartRequest;
import com.car.revive.Util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryFormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static RecyclerView recyclerView;
    static int i=0;
    int ImageId;
    Bitmap thumbnail,bm;
    ImagesAdapter imagesAdapter;
    Button click;
    File destination;
    public static String[] images;
    private String userChoosenTask;
    byte[] test;
    private static ArrayList<ImagesModel> imageList;


    Spinner locationSpinner, timeSpinner;
    EditText addressEt, enquiryEt;
    RelativeLayout calender;
    Button submit;
    RadioButton isLoanerYes, isLoanerNo;
    ImageView uploadImages;
    String location="Delhi", time="11-12 PM", enqiuryData, isLonerYes=null, address;
    static String date;
    Calendar dateSelected;
    private DatePickerDialog datePickerDialog;
    Network network;
    String carID;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_form);

        locationSpinner = (Spinner) findViewById(R.id.location_spinner);
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        calender = (RelativeLayout) findViewById(R.id.calender);
        addressEt = (EditText) findViewById(R.id.address);
        enquiryEt = (EditText) findViewById(R.id.enquiry);
        uploadImages = (ImageView) findViewById(R.id.click);
        isLoanerYes = (RadioButton) findViewById(R.id.radioButton1);
        isLoanerNo = (RadioButton) findViewById(R.id.radioButton2);
        submit = (Button) findViewById(R.id.submit);
        recyclerView = (RecyclerView)
                findViewById(R.id.recycler_view);
        imageList=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        progressDialog =new ProgressDialog(this);
        dateSelected = Calendar.getInstance();
        network =new Network();
        Intent intent =getIntent();
        carID= intent.getStringExtra("car_id");

        List<String> categories = new ArrayList<String>();
        categories.add("Gurugram");
        categories.add("Delhi");
        categories.add("Noida");
        categories.add("Ghaziabad");

        List<String> timings = new ArrayList<String>();
        timings.add("10-11 AM");
        timings.add("11-12 PM");
        timings.add("12-01 PM");
        timings.add("01-02 PM");
        timings.add("02-03 PM");
        timings.add("03-04 PM");
        timings.add("04-05 PM");



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timings);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(dataAdapter1);

        isLoanerYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLonerYes="1";
            }
        });

        isLoanerNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLonerYes="0";
            }
        });

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTruitonDatePickerDialog(view);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address =addressEt.getText().toString();
                enqiuryData=enquiryEt.getText().toString();

               if(location!=null&&time!=null&&date!=null && enqiuryData!=null&&isLonerYes!=null&&address!=null) {
                   saveEnquiryData();
               }else{
                   Toast.makeText(EnquiryFormActivity.this,"Please enter all the information",Toast.LENGTH_SHORT).show();
               }
            }
        });

        uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                // Notify the adapter
                imagesAdapter = new ImagesAdapter(imageList,asyncResult_addNewConnection1);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(imagesAdapter);

            }
        });

    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(EnquiryFormActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(EnquiryFormActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                test = onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                test = onCaptureImageResult(data);
        }
    }



    private byte[] onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        //  Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        // ivImage.setImageBitmap(thumbnail);
        ImagesModel hero = new ImagesModel(i,thumbnail);

        imageList.add(hero);
        i++;
        imagesAdapter.notifyDataSetChanged();
        return byteArrayOutputStream.toByteArray();

   /*
 Log.e("Dest",""+destination);
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


    }


    @SuppressWarnings("deprecation")
    private byte[] onSelectFromGalleryResult(Intent data) {
        ByteArrayOutputStream bytes=null;
        bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 40, bytes);



                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                //  Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ImagesModel hero = new ImagesModel(i,bm);
        imageList.add(hero);
        i++;
        imagesAdapter.notifyDataSetChanged();
        // ivImage.setImageBitmap(bm);
        return bytes.toByteArray();
    }
    AsyncResult<ImagesModel > asyncResult_addNewConnection1 = new AsyncResult<ImagesModel>() {
        @Override
        public void success(ImagesModel  click) {
            ImageId=click.getIid();

            for(int j = 0; j < imageList.size(); j++)
            {


                if(click.getBm().equals(imageList.get(j).getBm())){
                    //found, delete.
                    imageList.remove(j);
                    break;
                }

            }
            imagesAdapter.notifyDataSetChanged();
        }
        @Override
        public void error(String error) {

        }
    };
    public void showTruitonDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
         date  =(day + "/" + (month + 1) + "/" + year);
        }
    }

    private void saveEnquiryData() {

        progressDialog.show();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, network.Base_Url_Car + network.ToSetServiceEnquiry, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Log.e("Resp", response.toString());
                String resultResponse = new String(response.data);
                progressDialog.hide();
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");

                    if (status.equals("true")) {
                   
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        Log.e("Messsage", message);
                    } else {
                        Log.e("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("car_id", carID);
                params.put("address", address);
                params.put("location", location);
                params.put("loaner_vehicle", isLonerYes);
                params.put("enquiry", enqiuryData);
                params.put("pick_up_date", date);
                params.put("pick_up_time", time);
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

             params.put("service_images", new DataPart("file_avatar.jpg", test, "image/*"));
                return params;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case (R.id.location_spinner):
                location = parent.getItemAtPosition(position).toString();
                break;

            case (R.id.time_spinner):
                time = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

}

