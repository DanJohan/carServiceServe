package com.car.carserviceserve;

import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
   // SessionManager sessionManager;
    HashMap<String, String> user;
    String email=null,password=null,keyuserId=null,phoneNumber=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, MainDashboardActivity.class);
                startActivity(i);
                String deviceId = Settings.Secure.getString(SplashActivity.this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                Toast.makeText(SplashActivity.this, deviceId, Toast.LENGTH_SHORT).show();
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
       // sessionManager = new SessionManager(this);
      /*  user = new HashMap<String, String>();
      //  user= sessionManager.getUserDetails();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               *//* email = user.get("email");
                password = user.get("password");
                keyuserId = user.get("userID");
                phoneNumber = user.get("phoneNumber");*//*
                *//*if(email!=null && password!=null&&keyuserId!=null) {
                    Intent i = new Intent(SplashActivity.this, IdentiesMainActivity.class);
                    startActivity(i);
                }else if(phoneNumber!=null && email==null && password==null && keyuserId==null)
                {
                    Intent i = new Intent(SplashActivity.this, SignUpPersonalInfoActivity.class);
                    startActivity(i);
                }else
                {
                    Intent i = new Intent(SplashActivity.this, SignInUPActivity.class);
                    startActivity(i);
                }*//*
                finish();
            }
        }, SPLASH_TIME_OUT);*/

}

