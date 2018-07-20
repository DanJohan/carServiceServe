package com.car.carserviceserve;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.provider.ContactsContract.Intents.Insert.EMAIL;

public class FacebookIntegration extends AppCompatActivity {
  //  private static final String EMAIL = "email";
  private TextView info;

   // LoginButton loginButton;
    private static final String EMAIL = "email";
    private CallbackManager callbackManager;
    private AccessToken mAccessToken;
     LoginButton loginButton;
    // Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_integration);
        callbackManager = CallbackManager.Factory.create();
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFb();
            }
        });
    }

    public void loginWithFb(){
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                loginResult.getAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.e("DataFb32323", "==" + object.toString() + "///" + response.toString());

                                try {
                                    String fname = object.getString("first_name");
                                    String email = object.getString("email");
                                    String fb_id = object.getString("id");
                                    String name = object.getString("name");
                                    String t = name.replaceAll("\\s+", "");

                                    //String urll = App.FACEBOOK_URL + "fb_id=" + fb_id + "&email=" + email + "&name=" + t;

                                    //    Log.e("urll", "==" + urll + "///");
                                    Log.e("DataFb32323", "==" + object.toString() + "///" + response.toString());
                                    //   loginapp(urll);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}