package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class UserAddVehicleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_vehicle);

        //Spinner
        Spinner vehicle_categories;
        Spinner vehicle_fuelTypes;

        vehicle_fuelTypes = (Spinner) findViewById(R.id.addUser_fuelType);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.fuel_types, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_fuelTypes.setAdapter(adapter1);

        vehicle_categories = (Spinner) findViewById(R.id.addUserVehicle_category);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.vehicleTypes, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_categories.setAdapter(adapter2);

    }
}