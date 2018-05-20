package com.example.olife.complainboxfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail,inputPassword;

    private Button loginButton;

    private String email,password;

    private ProgressDialog pDialog;

    private static String FILENAME,url_login,TAG_SUCCESS,TAG_USERNAME,TAG_EMAIL,TAG_PASSWORD;

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();


        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    email = inputEmail.getText().toString();

                    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        inputEmail.setError("enter a valid email address");

                    }
                }

            }
        });

        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){

                    password = inputPassword.getText().toString();

                    if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                        inputPassword.setError("between 4 and 10 alphanumeric characters");
                    }

                }

            }
        });


    }

    private void initialize(){

        FILENAME = getResources().getString(R.string.local_storage_file_name);
        url_login = getResources().getString(R.string.domain)+getResources().getString(R.string.login_url_path);
        TAG_USERNAME = getResources().getString(R.string.username_tag);
        TAG_EMAIL = getResources().getString(R.string.email_tag);
        TAG_PASSWORD = getResources().getString(R.string.password_tag);
        TAG_SUCCESS = getResources().getString(R.string.success_tag);

        prefs = getSharedPreferences(FILENAME, MODE_PRIVATE);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        loginButton  = findViewById(R.id.loginButton);

    }

    public void loginbuttonclick(View view){

        if (!validate()) {
            //onLoginFailed();
            showToastMessageAndEnableSignUpButton("login failed, enter valid email and password");
            return;
        }

        loginButton.setEnabled(false);

        loginCheckInServer();

    }




    private void loginCheckInServer(){

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url_login , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                try {

                    JSONObject json = new JSONObject(response);
                    System.out.println(response);
                    int success = json.getInt(TAG_SUCCESS);

                    if(success==1){

                        SharedPreferences.Editor editor= prefs.edit();

                        editor.putString(TAG_USERNAME,json.getString(TAG_USERNAME));
                        editor.putString(TAG_EMAIL,json.getString(TAG_EMAIL));
                        editor.putString(TAG_PASSWORD,json.getString(TAG_PASSWORD));

                        editor.apply();
                        //pDialog.dismiss();
                        onLoginSuccess();
                    }
                    else{
                        showToastMessageAndEnableSignUpButton(json.getString("message"));
                        //Toast.makeText(LoginActivity.this,json.getString("message"),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                pDialog.dismiss();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                //Toast.makeText(LoginActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
                showToastMessageAndEnableSignUpButton("bad network connection");
                pDialog.dismiss();
                //Toast.makeText(LoginActivity.this, "bad network connection",Toast.LENGTH_SHORT).show();
                //loginButton.setEnabled(true);
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

    public void onLoginSuccess() {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public boolean validate() {

        boolean valid = true;

        email = inputEmail.getText().toString();
        password = inputPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }

        return valid;

    }

    private void showToastMessageAndEnableSignUpButton(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);

    }

    public void skipButtonClick(View view){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    public void signUpClick(View view){

        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();

    }

    public void forgotPasswordclick(View view){
/////////////////
    }

}
