package com.example.myapplication;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.models.FuelStation;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ViewShedDataActivity extends AppCompatActivity {

    Toolbar viewStationToolbar;
    TextView stationName, stationAddress, stationFuelTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shed_data);

        //set toolbar
        viewStationToolbar =  findViewById(R.id.owner_viewStation_toolbar);
        viewStationToolbar.setTitle("Fuel Q - View Station Data");
        setSupportActionBar(viewStationToolbar);
        viewStationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //setup ids
        stationName = findViewById(R.id.viewStation_station_name);
        stationAddress = findViewById(R.id.viewStation_station_address);
        stationFuelTypes = findViewById(R.id.viewStation_station_fueltypes);

        //get intent data
        Gson gson = new Gson();
        FuelStation fuelStation = gson.fromJson(getIntent().getStringExtra("FUEL_STATION_DATA"), FuelStation.class);
        stationName.setText("Name :"+fuelStation.getName());
        stationAddress.setText("Address :"+fuelStation.getAddress());

        List<String> fuelTypes = new ArrayList<>();
        String allFuelTypes = "";
        fuelTypes = fuelStation.getFuelTypes();

        for (int i = 0; i < fuelTypes.size(); i++) {
            if(fuelTypes.get(i).equals("P")){
                allFuelTypes = allFuelTypes + "| Petrol ";
            }else{
                allFuelTypes = allFuelTypes + "| Diesel ";
            }
        }

        stationFuelTypes.setText("Fuel Type : "+allFuelTypes);


        Log.i("Fuel Station Data: ", fuelStation.getAddress());
    }
}