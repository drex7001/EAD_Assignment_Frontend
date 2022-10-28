package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapters.OwnerHomeStationListAdapter;
import com.example.myapplication.adapters.OwnerViewFuelListAdapter;
import com.example.myapplication.models.Fuel;
import com.example.myapplication.models.FuelStation;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewShedDataActivity extends AppCompatActivity {

    Toolbar viewStationToolbar;
    TextView stationName, stationAddress, stationFuelTypes, dataNotFound;
    RecyclerView recyclerView;
    CircularProgressIndicator circularProgressIndicator;
    ArrayList<Fuel> fuelDataList;
    OwnerViewFuelListAdapter adapter;
    String fuelStationID = "";
    Button historyViewBtn;

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
        recyclerView = findViewById(R.id.owner_viewStation_fuel_rv);
        circularProgressIndicator = findViewById(R.id.owner_viewStation_progressIndicator);
        dataNotFound = findViewById(R.id.viewFuelStation_noData);
        historyViewBtn = findViewById(R.id.owner_fuel_data_history_button);

        //get intent data
        Gson gson = new Gson();
        FuelStation fuelStation = gson.fromJson(getIntent().getStringExtra("FUEL_STATION_DATA"), FuelStation.class);
        stationName.setText("Name :"+fuelStation.getName());
        stationAddress.setText("Address :"+fuelStation.getAddress());
        fuelStationID = fuelStation.getId();

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

        historyViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OwnerViewFuelDataHistory.class);
                intent.putExtra("FUEL_ID", fuelStationID);
                startActivity(intent);
            }
        });

        stationFuelTypes.setText("Fuel Type : "+allFuelTypes);

        //get data
        fuelDataList = new ArrayList<>();
        getFuelData();

        Log.i("Fuel Station Data: ", fuelStation.getAddress());
    }

    private void getFuelData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_FUEL_URL = BACKEND_URI+"fuel/station_owner/"+fuelStationID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_FUEL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                circularProgressIndicator.hide();
                recyclerView.setVisibility(View.VISIBLE);
                if(response.length() == 0){
                    dataNotFound.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject resObj = response.getJSONObject(i);
                        JSONObject responseObj = resObj.getJSONObject("fuel");
                        String fuelID = responseObj.getString("id");
                        String fuelStatus = responseObj.getString("status");
                        String fuelName = responseObj.getString("name");
                        String fuelType = responseObj.getString("type");
                        String fuelArrivalTime = responseObj.getString("arrivalTime");
                        String stationID = responseObj.getString("fuelStationID");
                        String fuelAmountSt = responseObj.getString("amount");
                        double fuelAmount = Double.parseDouble(fuelAmountSt);

                        JSONObject userVCountObj = resObj.getJSONObject("vCountList");
                        String completedQVehicles = userVCountObj.get("CompletedFilling").toString();
                        String leftQVehicles = userVCountObj.get("LeftQueue").toString();
                        String inQVehicles = userVCountObj.get("InQueue").toString();

                        Map<String, String> vehicleCountList = new HashMap<String, String>();
                        vehicleCountList.put("QCompleted", completedQVehicles);
                        vehicleCountList.put("QLeft", leftQVehicles);
                        vehicleCountList.put("QIn", inQVehicles);

                        Fuel fuelData = new Fuel(fuelID, fuelName, fuelType, fuelStatus, fuelAmount, fuelArrivalTime, "", stationID, vehicleCountList);
                        fuelDataList.add(fuelData);
                        buildRecyclerView();
                        Log.v("Fuel Data", fuelName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        circularProgressIndicator.show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewShedDataActivity.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void buildRecyclerView() {
        adapter = new OwnerViewFuelListAdapter(fuelDataList, ViewShedDataActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }
}