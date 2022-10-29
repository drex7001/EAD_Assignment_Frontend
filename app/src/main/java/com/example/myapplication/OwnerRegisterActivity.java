package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Objects;

//https://www.campcodes.com/mobile-app/android-studio/login-and-registration-app-with-sqlite-database-in-android-studio/
//https://www.youtube.com/watch?v=3865rSz9iOs&t=1s

public class OwnerRegisterActivity extends AppCompatActivity {

    EditText Email, Password, Name, RePassword;

    Button Register;
    String NameHolder, EmailHolder, PasswordHolder, RePasswordHolder;

    TextView LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_register);

        Register = (Button) findViewById(R.id.register);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        RePassword = (EditText) findViewById(R.id.password_confirmation);
        LoginButton = findViewById(R.id.login_here);
        Name = (EditText) findViewById(R.id.name);

        // Adding click listener to register button.
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              registerUser();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_LONG).show();
                // Opening new user registration activity using intent on button click.
                Intent intent = new Intent(OwnerRegisterActivity.this, OwnerLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void registerUser() {
        NameHolder = Name.getText().toString();
        EmailHolder = Email.getText().toString();
        PasswordHolder = Password.getText().toString();
        RePasswordHolder = RePassword.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(NameHolder) || TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PasswordHolder) || TextUtils.isEmpty(RePasswordHolder)) {
            Toast.makeText(OwnerRegisterActivity.this, "Please fill all fields!", Toast.LENGTH_LONG).show();

        }
        else if(!EmailHolder.matches(emailPattern)) {
            Toast.makeText(OwnerRegisterActivity.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
        }
        else if(!Objects.equals(PasswordHolder, RePasswordHolder)) {
            Toast.makeText(OwnerRegisterActivity.this, "Password does not matching. Please check again!", Toast.LENGTH_LONG).show();
        }
        else{
            //register user
            postData(EmailHolder, PasswordHolder, NameHolder);
        }
    }

    private void postData(String email, String password, String name) {
        NukeSSLCerts.nuke();
        String LOGIN_USER = BACKEND_URI + "user/register";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject responseBodyData = new JSONObject();
        try {
            responseBodyData.put("email", email);
            responseBodyData.put("name", name);
            responseBodyData.put("password", password);
            responseBodyData.put("role", "Owner");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_USER, responseBodyData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String createdId = response.getString("id");

                    if(!createdId.equals("")){
                        Log.i("Register", "Success!");
                        Toast.makeText(OwnerRegisterActivity.this, "SignUp Success!", Toast.LENGTH_SHORT).show();
                        dialogBox("Registration Success!", "PLease login to continue.");

                    }else{
                        Toast.makeText(OwnerRegisterActivity.this, "User Already Registered! Please login to continue.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(OwnerRegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(OwnerRegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
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

    public void dialogBox(String type, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(type);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        Intent intent = new Intent(OwnerRegisterActivity.this, OwnerLoginActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
