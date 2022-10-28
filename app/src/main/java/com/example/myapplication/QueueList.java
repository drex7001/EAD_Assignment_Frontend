package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QueueList extends AppCompatActivity {

    ListView queueList;
    ArrayList<String> queue_type = new ArrayList<>(); //fuel types
    ArrayList<String> volume_list = new ArrayList<>();
    ArrayList<String> vehicle_count = new ArrayList<>();

    String fuelStationID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);

        //toolbar
        Toolbar toolbar;
        toolbar = findViewById(R.id.user_shed_data_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - Station Fuel Data");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fuelStationID = getIntent().getStringExtra("FUEL_STATION_ID");
        Log.i("fuelStationID", fuelStationID);

        getFuelData();

    }

    private void getFuelData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_FUEL_URL = BACKEND_URI + "fuel/station/" + fuelStationID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_FUEL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
                    String type = "Sorry!.Active Fuel Queue Not Found.";
                    String message = "Please wait until station owner update the status or select another fuel station.";
                    dialogBox(type, message);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject resObj = response.getJSONObject(i);
                        JSONObject responseObj = resObj.getJSONObject("fuel");
                        String fuelName = responseObj.getString("name");
                        String fuelAmountSt = responseObj.getString("amount");
                        queue_type.add(fuelName);
                        volume_list.add("Available Amount: " + fuelAmountSt + " L");
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

    public void createListViewAdapter() {
        queueList = findViewById(R.id.queue_list);
        CustomAdapter adapter = new CustomAdapter(this, queue_type, volume_list, vehicle_count);
        queueList.setAdapter(adapter);

        queueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent = new Intent(QueueList.this, Queue.class);
                startActivity(intent);
            }
        });
    }

    public void dialogBox(String type, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(type);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}

class CustomAdapter extends ArrayAdapter<String> {

    Context content;
    ArrayList<String> queue_type = new ArrayList<>();
    ArrayList<String> volume_list = new ArrayList<>();
    ArrayList<String> vehicle_count = new ArrayList<>();

    public CustomAdapter(Context context, ArrayList<String> queue_type, ArrayList<String> volume, ArrayList<String> vehicle_count) {
        super(context, R.layout.activity_queue, R.id.queue_type, queue_type);
        this.content = context;
        this.queue_type = queue_type;
        this.volume_list = volume;
        this.vehicle_count = vehicle_count;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.queue_item, parent, false);
        TextView queue_typeView = row.findViewById(R.id.queue_type);
        TextView volumeView = row.findViewById(R.id.volume);
        TextView vehicle_countView = row.findViewById(R.id.vehicle_count);

        queue_typeView.setText(queue_type.get(position));
        volumeView.setText(volume_list.get(position));
        vehicle_countView.setText(vehicle_count.get(position));

        return row;
    }

}