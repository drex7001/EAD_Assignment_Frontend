package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class ViewShedDataActivity extends AppCompatActivity {

    Toolbar ownerToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shed_data);

        //set toolbar
        ownerToolbar =  findViewById(R.id.owner_toolbar);
        setSupportActionBar(ownerToolbar);
        getSupportActionBar().setTitle("Fuel Q - View Station Data");
    }
}