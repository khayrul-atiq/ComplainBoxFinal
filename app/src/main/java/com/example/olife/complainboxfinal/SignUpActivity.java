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

public class SignUpActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    private EditText signUpUsername,signUpEmail,signUpPassword,signUpConfirmPassword;

    private String username,email,password,confirmPassword,regex = "[a-zA-Z0-9\\._\\-]{3,}";

    private Button signUpButton;

    private static String FILENAME,url_sign_up,TAG_SUCCESS,TAG_USERNAME,TAG_EMAIL,TAG_PASSWORD;

    private SharedPreferences prefs = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialize();

        signUpUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if (!hasFocus){

                    username = signUpUsername.getText().toString();

                    if (username.isEmpty() || !username.matches(regex)) {
                        signUpUsername.setError("username contains alphanumeric letter greater than 3");
                    }
                }

            }
        });

        signUpEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus){

                    email = signUpEmail.getText().toString();

                    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        signUpEmail.setError("enter a valid email address");
                    }
                }
            }
        });

        signUpPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){

                    password = signUpPassword.getText().toString();

                    if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                        signUpPassword.setError("between 4 and 10 alphanumeric characters");
                    }

                }
            }
        });

        signUpConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){

                    password = signUpPassword.getText().toString();
                    confirmPassword = signUpConfirmPassword.getText().toString();

                    if (confirmPassword.isEmpty() || !password.equals(confirmPassword) || confirmPassword.length() < 4 || confirmPassword.length() > 10 ) {
                        signUpConfirmPassword.setError("password does not match");
                    }

                }
            }
        });

    }

    private void initialize(){

        FILENAME = getResources().getString(R.string.local_storage_file_name);
        url_sign_up = getResources().getString(R.string.domain)+getResources().getString(R.string.signup_url_path);
        TAG_USERNAME = getResources().getString(R.string.username_tag);
        TAG_EMAIL = getResources().getString(R.string.email_tag);
        TAG_PASSWORD = getResources().getString(R.string.password_tag);
        TAG_SUCCESS = getResources().getString(R.string.success_tag);

        prefs = getSharedPreferences(FILENAME, MODE_PRIVATE);

        signUpUsername = findViewById(R.id.signUpUsername);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpConfirmPassword = findViewById(R.id.signUpConfirmPassword);

        signUpButton = findViewById(R.id.signUpButton);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    public void signupButtonclick(View view){

        if (!validate()) {
            showToastMessageAndEnableSignUpButton("sign up failed");
            return;
        }

        signUpButton.setEnabled(false);
        storeUserInformationInServer();

    }



    private void storeUserInformationInServer(){

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        pDialog = new ProgressDialog(SignUpActivity.this);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url_sign_up , new Response.Listener<String>() {
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

                        editor.putString(TAG_USERNAME,username);
                        editor.putString(TAG_EMAIL,email);
                        editor.putString(TAG_PASSWORD,password);

                        editor.apply();
                        onSignupSuccess();
                        //pDialog.dismiss();
                    }
                    else{
                        showToastMessageAndEnableSignUpButton(json.getString("message"));
                        //Toast.makeText(SignUpActivity.this,json.getString("message"),Toast.LENGTH_SHORT).show();
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
                pDialog.dismiss();
                showToastMessageAndEnableSignUpButton("bad network connection");
                //Toast.makeText(SignUpActivity.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                //MyData.put("category", title);
                MyData.put(TAG_USERNAME,username);
                MyData.put(TAG_EMAIL,email);
                MyData.put(TAG_PASSWORD,password);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);

    }

    private void showToastMessageAndEnableSignUpButton(String message){

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        signUpButton.setEnabled(true);

    }


    private void onSignupSuccess() {

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    private boolean validate() {

        boolean valid = true;

        username = signUpUsername.getText().toString();
        email = signUpEmail.getText().toString();
        password = signUpPassword.getText().toString();
        confirmPassword = signUpConfirmPassword.getText().toString();

        if (username.isEmpty() || !username.matches(regex)) {
            signUpUsername.setError("username contains alphanumeric letter greater than 3");
            valid = false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEmail.setError("enter a valid email address");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            signUpPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        }

        if (confirmPassword.isEmpty() || !password.equals(confirmPassword) || confirmPassword.length() < 4 || confirmPassword.length() > 10 ) {
            signUpConfirmPassword.setError("password does not match");
            valid = false;
        }

        return valid;

    }

    public void skipButtonClickSignUp(View view){

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
