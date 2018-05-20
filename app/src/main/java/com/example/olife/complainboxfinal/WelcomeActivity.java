package com.example.olife.complainboxfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private static  String FILENAME,url_login,TAG_SUCCESS,TAG_USERNAME,TAG_EMAIL,TAG_PASSWORD,DEFAULT_EMAIL,DEFAULT_PASSWORD;

    private String email, password;

    private SharedPreferences prefs = null;

    private void initializeLocalStorageNameAndUrl(){

        FILENAME = getResources().getString(R.string.local_storage_file_name);
        url_login = getResources().getString(R.string.domain)+getResources().getString(R.string.login_url_path);
        TAG_USERNAME = getResources().getString(R.string.username_tag);
        TAG_EMAIL = getResources().getString(R.string.email_tag);
        TAG_PASSWORD = getResources().getString(R.string.password_tag);
        TAG_SUCCESS = getResources().getString(R.string.success_tag);

        DEFAULT_EMAIL = getResources().getString(R.string.default_email);
        DEFAULT_PASSWORD = getResources().getString(R.string.default_password);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initializeLocalStorageNameAndUrl();

        prefs = getSharedPreferences(FILENAME, MODE_PRIVATE);

        if(prefs.getString(TAG_EMAIL, DEFAULT_EMAIL).equals(DEFAULT_EMAIL)){

            onAutomaticLoginFailed();
        }

        else{
            email = prefs.getString(TAG_EMAIL, DEFAULT_EMAIL);
            password = prefs.getString(TAG_PASSWORD, DEFAULT_PASSWORD);

            automaticLoginCheck();
        }

    }

    private void automaticLoginCheck(){

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url_login , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {

                    JSONObject json = new JSONObject(response);

                    int success = json.getInt(TAG_SUCCESS);

                    if(success==1){

                        SharedPreferences mySharedPreferences ;
                        mySharedPreferences=getSharedPreferences(FILENAME,MODE_PRIVATE);

                        SharedPreferences.Editor editor= mySharedPreferences.edit();

                        editor.putString(TAG_USERNAME,json.getString(TAG_USERNAME));
                        editor.putString(TAG_EMAIL,json.getString(TAG_EMAIL));
                        editor.putString(TAG_PASSWORD,json.getString(TAG_PASSWORD));

                        editor.apply();
                        onAutomationLoginSuccess();
                    }
                    else{
                        onAutomaticLoginFailed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                onAutomaticLoginFailed();
                }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                //MyData.put("category", title);
                MyData.put(TAG_EMAIL,email);
                MyData.put(TAG_PASSWORD,password);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);

    }

    public void onAutomaticLoginFailed() {

        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void onAutomationLoginSuccess() {

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
