package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.models.Fuel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerAddNewShed extends AppCompatActivity {

    Toolbar addNewStationToolbar;
    Spinner spinner;
    TextView stationName;
    TextView stationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_add_new_shed);

        //set toolbar
        addNewStationToolbar =  findViewById(R.id.owner_toolbar);
        setSupportActionBar(addNewStationToolbar);
        getSupportActionBar().setTitle("Fuel Q - Add Fuel Station");
        addNewStationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set spinner
        spinner = (Spinner) findViewById(R.id.owner_add_fuel_types_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fuel_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //form
        stationName = (TextView) findViewById(R.id.addFuelStation_StationName);
        stationAddress = (TextView) findViewById(R.id.addFuelStation_StationAddress);

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
                    String sName = stationName.getText().toString();
                    String sAddress= stationAddress.getText().toString();
                    List<String> fTypes= new ArrayList<>();

                    if(fType.equals("Petrol")){
                        fTypes.add("P");
                    }else if(fType.equals("Diesel")){
                        fTypes.add("D");
                    }else{
                        fTypes.add("D");
                        fTypes.add("P");
                    }

                    Log.i("FType", fTypes.get(0));
//                    postData();
                }
            }
        });

    }

    private void postData(String name, String address, List<String> types) {
        NukeSSLCerts.nuke();
        String ADD_NEW_FUEL_STATION = BACKEND_URI+"fuel/fuelstation";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, ADD_NEW_FUEL_STATION, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stationName.setText("");
                stationAddress.setText("");
                Toast.makeText(OwnerAddNewShed.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject respObj = new JSONObject(response);
                    String id = respObj.toString();
                    Log.i("Created Station ID", id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerAddNewShed.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("address", address);
                params.put("fuelTypes", types.toString());
                params.put("ownerID", "63575e6a8e1aee177127c7a9");
                return params;
            }
        };
        requestQueue.add(request);
    }

}