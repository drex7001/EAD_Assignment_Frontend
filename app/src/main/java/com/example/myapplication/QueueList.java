package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueueList extends AppCompatActivity {

    ListView queueList;
    ArrayList<String> queue_type = new ArrayList<>(); //fuel types
    ArrayList<String> volume_list = new ArrayList<>();
    ArrayList<String> vehicle_count = new ArrayList<>();

    Button join;
    String fuelStationID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);

        fuelStationID = getIntent().getStringExtra("FUEL_STATION_ID");
        Log.i("fuelStationID", fuelStationID);

        getFuelData();

    }

    private void getFuelData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_FUEL_URL = BACKEND_URI+"fuel/station/"+fuelStationID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_FUEL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject resObj = response.getJSONObject(i);
                        JSONObject responseObj = resObj.getJSONObject("fuel");
                        String fuelName = responseObj.getString("name");
                        String fuelAmountSt = responseObj.getString("amount");
                        queue_type.add(fuelName);
                        volume_list.add("Available Amount: "+ fuelAmountSt + " L");
                        Log.i("queue_type", queue_type.toString());

                        JSONArray userVCountArr = resObj.getJSONArray("vehicleCountList");
                        String vehicleString = "";
                        for (int j = 0; j < userVCountArr.length(); j++) {
                            JSONObject vCountObj = userVCountArr.getJSONObject(j);
                            vehicleString = vehicleString + "" + vCountObj.getString("name") + " : " + vCountObj.getString("count") + "\n";
                        }
                        vehicle_count.add(vehicleString);
                        Log.v("Fuel Data", fuelName);
                        createListViewAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QueueList.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void createListViewAdapter(){
        queueList = findViewById(R.id.queue_list);
        CustomAdapter adapter = new CustomAdapter(this,queue_type,volume_list,vehicle_count);
        queueList.setAdapter(adapter);

        queueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent = new Intent(QueueList.this, Queue.class);
                startActivity(intent);
            }
        });
    }

}

class CustomAdapter extends ArrayAdapter<String>{

    Context content;
    ArrayList<String> queue_type = new ArrayList<>();
    ArrayList<String> volume_list = new ArrayList<>();
    ArrayList<String> vehicle_count = new ArrayList<>();

    public CustomAdapter(Context context,ArrayList<String> queue_type,ArrayList<String> volume,ArrayList<String> vehicle_count) {
        super(context, R.layout.activity_queue,R.id.queue_type,queue_type);
        this.content = context;
        this.queue_type = queue_type;
        this.volume_list = volume;
        this.vehicle_count = vehicle_count;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.queue_item,parent,false);
        TextView queue_typeView = row.findViewById(R.id.queue_type);
        TextView volumeView = row.findViewById(R.id.volume);
        TextView vehicle_countView = row.findViewById(R.id.vehicle_count);

        queue_typeView.setText(queue_type.get(position));
        volumeView.setText(volume_list.get(position));
        vehicle_countView.setText(vehicle_count.get(position));

        return row;
    }

}