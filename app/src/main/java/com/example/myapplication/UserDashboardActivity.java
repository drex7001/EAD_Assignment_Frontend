package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class UserDashboardActivity extends AppCompatActivity {
    String EmailHolder;
    TextView Email,LogOUT;
    LinearLayout linearLayout;
    Toolbar toolbar;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        toolbar = findViewById(R.id.user_dashboard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - User Dashboard");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Email = (TextView)findViewById(R.id.textView1);
        LogOUT = findViewById(R.id.button1);
        linearLayout = findViewById(R.id.join_queue);
        Intent intent = getIntent();
        // Receiving User Email Send By MainActivity.
        EmailHolder = intent.getStringExtra(UserLoginActivity.UserEmail);
        // Setting up received email to TextView.
        Email.setText(Email.getText().toString()+ EmailHolder);
        // Adding click listener to Log Out button.
        LogOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Finishing current DashBoard activity on button click.
                finish();
                Toast.makeText(UserDashboardActivity.this,"Log Out Successful", Toast.LENGTH_LONG).show();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, ShedList.class);
                startActivity(intent);
            }
        });

    }

}