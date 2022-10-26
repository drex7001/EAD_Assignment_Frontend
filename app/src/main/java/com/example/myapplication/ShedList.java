package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapters.OwnerHomeStationListAdapter;
import com.example.myapplication.models.Fuel;
import com.example.myapplication.models.FuelStation;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShedList extends AppCompatActivity {
    ListView listView;
    CircularProgressIndicator circularProgressIndicator;
    ArrayList<FuelStation> fuelStationArrayList;
    OwnerHomeStationListAdapter adapter;
    Toolbar userShedListToolbar;
    ArrayList<String> allSheds = new ArrayList<>();
    private ArrayList<String> fuelStationsIDList = new ArrayList<String>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shed_list);
        getAllStationData();
    }

    private void getAllStationData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_URL = BACKEND_URI+"fuelstation";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String stationName = responseObj.getString("name");
                        String stationId = responseObj.getString("id");
                        allSheds.add(stationName);
                        fuelStationsIDList.add(stationId);
                        buildFuelStationsList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShedList.this, "Fail to get shed data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void buildFuelStationsList(){
//        Log.i("Shed data", allSheds.toString());
        ListAdapter Shed_list = new ArrayAdapter<String>(this,R.layout.shed_item,R.id.shed_name,allSheds);
        listView = findViewById(R.id.shed_list);
        listView.setAdapter(Shed_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fuelStationID = fuelStationsIDList.get(i);
                Intent intent = new Intent(ShedList.this, QueueList.class);
                intent.putExtra("FUEL_STATION_ID", fuelStationID);
                startActivity(intent);
            }
        });
    }

}