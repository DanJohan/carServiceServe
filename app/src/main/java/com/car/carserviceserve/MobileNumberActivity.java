package com.car.carserviceserve;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.car.carserviceserve.Util.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class MobileNumberActivity extends AppCompatActivity implements View.OnClickListener {
   EditText code,number;
   Button submit;
   ProgressBar progressBar;
   Network network;
   String codeString,numberString,NUMBER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code =(EditText)findViewById(R.id.code);
        number =(EditText)findViewById(R.id.number);
        submit=(Button)findViewById(R.id.submit);
        progressBar =new ProgressBar(this);
        network =new Network();
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.submit):
                getUserDetails();
                break;
        }
    }
    public void getUserDetails() {
        codeString = code.getText().toString();
        numberString = number.getText().toString();
        NUMBER=codeString+numberString;
        if (codeString.equals("") && numberString.equals("")) {
            Toast.makeText(getApplicationContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
        } else if (codeString.equals("")) {
            code.setError("Enter your code");
            code.requestFocus();
        } else if (codeString.equals("")) {
            number.setError("Enter your number");
            number.requestFocus();
        } else if (!codeString.equals("") && !numberString.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            Login();

        }

    }
    public void Login() {
        RequestQueue queue = null;
        queue = Volley.newRequestQueue(this);
        String URL = network.Base_Url + network.Add_Phone;
        // progressDialog.show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("phone", NUMBER);
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
                            String resposne_sucess = response.getString("status");
                            JSONObject resposne_userId = response.getJSONObject("user");
                            if(resposne_sucess.equals("true"))
                            {
                                progressBar.setVisibility(View.GONE);
                         /*       sessionManager.createLoginSession(resposne_userId,KEY_USERID);
                                sessionManager.createLoginSession(Email,KEY_EMAIL);
                                sessionManager.createLoginSession(Password,KEY_PASSWORD);
                                sessionManager.createLoginSession(statuscode,Status_Code);*/
                                String User_ID= response.getString("id");
                                Intent intent = new Intent(MobileNumberActivity.this, VerificationActivity.class);
                                intent.putExtra("USER_ID",User_ID);
                                startActivity(intent);
                                // progressDialog.hide();
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MobileNumberActivity.this,resposne_message, Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
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

}
