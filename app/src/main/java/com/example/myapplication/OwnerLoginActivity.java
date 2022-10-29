package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;
import static com.example.myapplication.models.Utils.EMAIL_KEY;
import static com.example.myapplication.models.Utils.PASSWORD_KEY;
import static com.example.myapplication.models.Utils.ROLE_KEY;
import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_ID_KEY;
import static com.example.myapplication.models.Utils.USER_NAME_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class OwnerLoginActivity extends AppCompatActivity {

    String EmailHolder, PasswordHolder;

    SharedPreferences sharedpreferences;

    Button LogInButton;
    EditText Email, Password;
    Boolean EditTextEmptyHolder;
    TextView RegisterButton,UserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        //shared pref init...
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        LogInButton = (Button) findViewById(R.id.login);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        RegisterButton = (TextView) findViewById(R.id.register_here_text);
        UserLogin = (TextView) findViewById(R.id.user_login);


        //Adding click listener to log in button.
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFunction();
            }
        });
        // Adding click listener to register button.
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(OwnerLoginActivity.this, OwnerRegisterActivity.class);
                startActivity(intent);
            }
        });

        UserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(OwnerLoginActivity.this, UserLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Login function starts from here.
    public void LoginFunction() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Getting value from All EditText and storing into String Variables.
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();
        // Checking EditText is empty or no using TextUtils.
        if (TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder)) {
            Toast.makeText(OwnerLoginActivity.this, "Please fill all fields!", Toast.LENGTH_LONG).show();
        }
        else if(!EmailHolder.matches(emailPattern)){
            Toast.makeText(OwnerLoginActivity.this, "Please enter valid email address!", Toast.LENGTH_LONG).show();
        }
        else {
            //login activity
            postData(EmailHolder, PasswordHolder);
        }
    }

    private void postData(String email, String password) {
        NukeSSLCerts.nuke();
        String LOGIN_USER = BACKEND_URI + "user/login";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject responseBodyData = new JSONObject();
        try {
            responseBodyData.put("email", email);
            responseBodyData.put("password", password);
            responseBodyData.put("role", "Owner");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_USER, responseBodyData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean createdId = response.getBoolean("success");

                    if(createdId){
                        Log.i("Login", "Success!");
                        JSONObject userOBj = response.getJSONObject("user");
                        String userName = userOBj.getString("name");
                        String userID = userOBj.getString("id");

                        Toast.makeText(OwnerLoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();

                        //save user data to local
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(EMAIL_KEY, EmailHolder);
                        editor.putString(PASSWORD_KEY, PasswordHolder);
                        editor.putString(ROLE_KEY, "Owner");
                        editor.putString(USER_NAME_KEY, userName);
                        editor.putString(USER_ID_KEY, userID);
                        editor.apply();

                        Toast.makeText(OwnerLoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        // Going to Dashboard activity after login success message.
                        Intent intent = new Intent(OwnerLoginActivity.this, OwnerHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(OwnerLoginActivity.this, "Email or Password is Wrong, Please Try Again.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(OwnerLoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(OwnerLoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
    }


}