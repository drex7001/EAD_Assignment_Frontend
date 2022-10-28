package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.models.Fuel;
import com.example.myapplication.models.FuelStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OwnerViewFuelDataHistory extends AppCompatActivity {

    Toolbar fuelHistoryToolbar;
    String stationID;
    TextView dataNotFound;

    ArrayList<String> fuelDataDetails = new ArrayList<>();
    ListView listView;
    private ArrayAdapter fuel_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_view_fuel_data_history);

        //get data
        stationID = getIntent().getStringExtra("FUEL_ID");

        //ids init
        dataNotFound = findViewById(R.id.owner_history_notFound);
        listView = findViewById(R.id.fuel_history_list);

        //set toolbar
        fuelHistoryToolbar = findViewById(R.id.owner_fuel_history_toolbar);
        setSupportActionBar(fuelHistoryToolbar);
        getSupportActionBar().setTitle("Fuel Q - Fuel History");
        fuelHistoryToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFuelData();

    }

    private void getFuelData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_FUEL_HISTORY_URL = BACKEND_URI+"fuel/fuel_history/"+stationID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_FUEL_HISTORY_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() == 0){
                    dataNotFound.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject resObj = response.getJSONObject(i);
                        JSONObject responseObj = resObj.getJSONObject("fuel");
                        String fuelStatus = responseObj.getString("status");
                        String fuelName = responseObj.getString("name");
                        String fuelType = responseObj.getString("type");
                        String fuelArrivalTime = responseObj.getString("arrivalTime");
                        String fuelFinishedTime = responseObj.getString("finishTime");
                        String fuelAmount = responseObj.getString("amount");
                        JSONArray userVCountArr = resObj.getJSONArray("vehicleCountList");
                        String vehicleString = "";
                        for (int j = 0; j < userVCountArr.length(); j++) {
                            JSONObject vCountObj = userVCountArr.getJSONObject(j);
                            vehicleString = vehicleString + "" + vCountObj.getString("name") + " : " + vCountObj.getString("count") + "\n";
                        }

                        String fuelHistoryData = "Fuel Name:"+fuelName + "\n" + "Fuel Status:"+fuelStatus+"\n"+"Fuel Type:"+ fuelType+"\n" + "Arrival Time:"+fuelArrivalTime+ "\n"+ "Finished Time:"+fuelFinishedTime+"\n"+"Amount:"+fuelAmount+"\n"+"Vehicles:\n"+ vehicleString;
                        fuelDataDetails.add(fuelHistoryData);
                        Log.v("Fuel Data", fuelName);
                        buildFuelDataList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerViewFuelDataHistory.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void buildFuelDataList() {
        fuel_list_adapter = new ArrayAdapter<String>(this, R.layout.fuel_history_item, R.id.fuelData_name, fuelDataDetails);
        listView.setAdapter(fuel_list_adapter);
    }


}