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

    private static final String FILENAME = "com.mycompany.myAppName";

    String email, password;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        prefs = getSharedPreferences(FILENAME, MODE_PRIVATE);


        if(prefs.getString("email", "anonymous@example.com").equals("anonymous@example.com")){

            onLoginFailed();
        }

        else{
            email = prefs.getString("email", "anonymous@example.com");
            password = prefs.getString("password", "password");

            loginCheckInServer();

        }

    }




    private void loginCheckInServer(){

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        String url_login = "http://192.168.1.109/android/login.php";

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url_login , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {

                    JSONObject json = new JSONObject(response);
                    System.out.println(response);
                    int success = json.getInt("success");

                    if(success==1){


                        SharedPreferences mySharedPreferences ;
                        mySharedPreferences=getSharedPreferences(FILENAME,MODE_PRIVATE);

                        SharedPreferences.Editor editor= mySharedPreferences.edit();

                        editor.putString("username",json.getString("username"));
                        editor.putString("email",json.getString("email"));
                        editor.putString("password",json.getString("password"));

                        editor.apply();
                        //pDialog.dismiss();
                        onLoginSuccess();
                    }
                    else{
                        onLoginFailed();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                onLoginFailed();
                }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                //MyData.put("category", title);
                MyData.put("email",email);
                MyData.put("password",password);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);


    }

    public void onLoginFailed() {

        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void onLoginSuccess() {

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
