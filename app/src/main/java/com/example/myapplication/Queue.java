package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Queue extends AppCompatActivity {

    Button leave_btn, complete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        //init ids
        leave_btn = findViewById(R.id.user_join_leave_queue_btn);
        complete_btn = findViewById(R.id.user_join_complete_filling_btn);

        //toolbar
        Toolbar toolbar;
        toolbar = findViewById(R.id.user_shed_join_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - Joined Queue Data");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}