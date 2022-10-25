package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class OwnerAddNewShed extends AppCompatActivity {

    Toolbar ownerToolbar;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_add_new_shed);

        //set toolbar
        ownerToolbar =  findViewById(R.id.owner_toolbar);
        setSupportActionBar(ownerToolbar);
        getSupportActionBar().setTitle("Fuel Q - Add Fuel Station");

        //set spinner
        spinner = (Spinner) findViewById(R.id.owner_add_fuel_types_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fuel_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //form
        TextView stationName = (TextView) findViewById(R.id.addFuelStation_StationName);
        TextView stationAddress = (TextView) findViewById(R.id.addFuelStation_StationAddress);

        Button submitBtn = findViewById(R.id.add_fuel_station_button);

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String fType = spinner.getSelectedItem().toString();
                if( stationName.getText().toString().matches("") || stationAddress.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), "Please Fill All Fields!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), fType, Toast.LENGTH_SHORT).show();
                    //api call
                }
            }
        });


    }


}