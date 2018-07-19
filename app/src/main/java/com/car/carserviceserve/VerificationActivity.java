package com.car.carserviceserve;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.car.carserviceserve.Util.Network;
import com.car.carserviceserve.Util.SessionManager;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_PHONENUMBER = "phoneNumber";

    private OkHttpClient mClient;
    String verifyCode, PhoneNumber;
    TextView displayNumber, resendCode, editBack;
    SmsVerifyCatcher smsVerifyCatcher;
    EditText editText1, editText2, editText3, editText4, editText5, editText6;
    String first, two, three, four, five, six;
    String first1 = null, two1 = null, three1 = null, four1 = null, five1 = null, six1 = null;
    LinearLayout pinLayout;
    ProgressDialog progressDialog;
    Network network_url;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mClient = new OkHttpClient();

        editText1 = (EditText) findViewById(R.id.pin_first_edittext);
        editText2 = (EditText) findViewById(R.id.pin_second_edittext);
        editText3 = (EditText) findViewById(R.id.pin_third_edittext);
        editText4 = (EditText) findViewById(R.id.pin_forth_edittext);

        pinLayout = (LinearLayout) findViewById(R.id.pin_layout);

        progressDialog = new ProgressDialog(this);
        network_url = new Network();
        sessionManager =new SessionManager(this);
        Intent intent = getIntent();
        PhoneNumber = intent.getStringExtra("phoneNumber");
        //  verifyCode = intent.getStringExtra("verifyCode");
        displayNumber.setText(PhoneNumber);
        editText1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editText1.getText().toString().length() == 1)     //size as per your requirement
                {
                    editText2.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        editText2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editText2.getText().toString().length() == 1)     //size as per your requirement
                {
                    editText3.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });


        editText3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editText3.getText().toString().length() == 1)     //size as per your requirement
                {
                    editText4.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });


        editText4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Auto-generated method stub
                if(editText4.getText().toString().length() == 1)     //size as per your requirement
                {

                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });


        sendMessage();
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                Log.e("testSms", code);
                if (code != null) {
                    first = String.valueOf(code.charAt(0));
                    two = String.valueOf(code.charAt(1));
                    three = String.valueOf(code.charAt(2));
                    four = String.valueOf(code.charAt(3));

                    editText1.setText(first);
                    editText2.setText(two);
                    editText3.setText(three);
                    editText4.setText(four);

                }
                isCodeValid(code);
                //   etCode.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });

        resendCode.setOnClickListener(this);
        editBack.setOnClickListener(this);
        pinLayout.setOnClickListener(this);
    }

    public void isCodeValid(String code) {
        RequestQueue queue = null;
        queue = Volley.newRequestQueue(this);
        String URL = network_url.Base_Url + network_url.Otp_verification + "phone_no=" + PhoneNumber + "&otp=" + code;
        //progressDialog.show();
      //  progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Log.e("Response", response.toString());
                        String responsemessage = null;
                        try {
                            String resposne_message = response.getString("message");
                            if (resposne_message.equals("OTP successfully matched")) {
                                sessionManager.createLoginSession(PhoneNumber,KEY_PHONENUMBER);
                                Intent intent = new Intent(VerificationActivity.this, SignUpPersonalInfoActivity.class);
                                startActivity(intent);
                            } else {
                                resendCode.setVisibility(View.VISIBLE);
                            }
                            //progressDialog.hide();
                       //     progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(jsObjRequest);

    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void sendMessage() {

        RequestQueue queue = null;
        queue = Volley.newRequestQueue(this);
        String URL = network_url.Base_Url + network_url.Otp_verify;
        //progressDialog.show();
       // progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", NUMBER);
            jsonBody.put("device_type", "ANDROID");
            jsonBody.put("device_id", NUMBER);
            jsonBody.put("otp", NUMBER);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, URL, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Log.e("Response", response.toString());
                        String responsemessage = null;
                        try {
                            String resposne_message = response.getString("message");
                            if (resposne_message.equals("OTP sent successfully")) {

                            } else {
                                resendCode.setVisibility(View.VISIBLE);
                            }
                            //progressDialog.hide();
                          //  progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(jsObjRequest);


    }
   /* okhttp3.Call post(String url, Callback callback) throws IOException {
        String ACCOUNT_SID = "AC49e3cd6d3b0eae78e1c1226c8a7e2c0e";
        String AUTH_TOKEN = "13ff657f9a1c7f662c9e83cce2ac68b5";
        String base64EncodedCredentials = "Basic "
                + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(),
                Base64.NO_WRAP);
        RequestBody formBody = new FormBody.Builder()
                //  .add("To", "+16504929959")
                .add("To",PhoneNumber)
                .add("From", "+18312192503")
                .add("Body","Your pop'n app verification code is "+verifyCode)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization",base64EncodedCredentials)
                .post(formBody)
                .build();
        okhttp3.Call response = mClient.newCall(request);
        response.enqueue(callback);
        return response;
    }
    public void sendMessage(){
        try {
            post("https://api.twilio.com/2010-04-01/Accounts/AC49e3cd6d3b0eae78e1c1226c8a7e2c0e/Messages", new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onResponse(okhttp3.Call call, final Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("Response", response.toString());
                            if(response.isSuccessful()) {
                            }else{
                                resendCode.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.resend_textview):
                sendMessage();
                break;
            case (R.id.edit):
                Intent intent = new Intent(this, MainDashboardActivity.class);
                startActivity(intent);
                break;
            case (R.id.pin_layout):
                String vCode=null;
                first1 = editText1.getText().toString();
                two1 = editText2.getText().toString();
                three1 = editText3.getText().toString();
                four1 = editText4.getText().toString();

                vCode = first1 + two1 + three1 + four1;
                if(vCode!=null){
                    isCodeValid(vCode);
                }


                break;
        }
    }
}
