package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;
import static com.example.myapplication.models.Utils.EMAIL_KEY;
import static com.example.myapplication.models.Utils.PASSWORD_KEY;
import static com.example.myapplication.models.Utils.ROLE_KEY;
import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_ID_KEY;
import static com.example.myapplication.models.Utils.USER_NAME_KEY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class UserRegisterActivity extends AppCompatActivity {

    String NameHolder, EmailHolder, PasswordHolder, RePasswordHolder;

    SharedPreferences sharedpreferences;

    EditText Email, Password, Name, RePassword;
    Button Register;
    SQLiteDatabase sqLiteDatabaseObj;
    UserDBHelper userDBHelper;
    TextView LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        //shared pref init...
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        Register = (Button) findViewById(R.id.register);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        RePassword = (EditText) findViewById(R.id.password_confirmation);
        Name = (EditText) findViewById(R.id.name);
        userDBHelper = new UserDBHelper(this);
        LoginButton = (TextView) findViewById(R.id.login_here);

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
                Intent intent = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
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
            Toast.makeText(UserRegisterActivity.this, "Please fill all fields!", Toast.LENGTH_LONG).show();

        }
        else if(!EmailHolder.matches(emailPattern)) {
            Toast.makeText(UserRegisterActivity.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
        }
        else if(!Objects.equals(PasswordHolder, RePasswordHolder)) {
            Toast.makeText(UserRegisterActivity.this, "Password does not matching. Please check again!", Toast.LENGTH_LONG).show();
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
            responseBodyData.put("role", "User");

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
                        Toast.makeText(UserRegisterActivity.this, "SignUp Success!", Toast.LENGTH_SHORT).show();
                        dialogBox("Registration Success!", "PLease login to continue.");

                    }else{
                        Toast.makeText(UserRegisterActivity.this, "User Already Registered! Please login to continue.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UserRegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(UserRegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(UserRegisterActivity.this, UserLoginActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
