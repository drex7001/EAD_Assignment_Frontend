package com.example.myapplication;

import static com.example.myapplication.models.Utils.EMAIL_KEY;
import static com.example.myapplication.models.Utils.PASSWORD_KEY;
import static com.example.myapplication.models.Utils.ROLE_KEY;
import static com.example.myapplication.models.Utils.SHARED_PREFS;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    String userEmail;
    String userPwd;
    String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get preference sessions
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userEmail = sharedpreferences.getString(EMAIL_KEY, null);
        userPwd = sharedpreferences.getString(PASSWORD_KEY, null);
        userRole = sharedpreferences.getString(ROLE_KEY, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i;
        if (userEmail != null && userPwd != null && userRole != null) {
            if (Objects.equals(userRole, "User")) {
                i = new Intent(MainActivity.this, UserDashboardActivity.class);
            } else if(Objects.equals(userRole, "Owner")){
                i = new Intent(MainActivity.this, OwnerHomeActivity.class);
            }else{
                i = new Intent(MainActivity.this, UserLoginActivity.class);
            }
        } else {
            i = new Intent(MainActivity.this, UserLoginActivity.class);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}