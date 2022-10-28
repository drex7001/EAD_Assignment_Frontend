package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;
import static com.example.myapplication.models.Utils.EMAIL_KEY;
import static com.example.myapplication.models.Utils.PASSWORD_KEY;
import static com.example.myapplication.models.Utils.ROLE_KEY;
import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_ID_KEY;
import static com.example.myapplication.models.Utils.USER_NAME_KEY;
import static com.example.myapplication.models.Utils.USER_QUEUE_ID;
import static com.example.myapplication.models.Utils.USER_QUEUE_STATUS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QueueList extends AppCompatActivity {

    ListView queueList;
    ArrayList<String> queue_type = new ArrayList<>(); //fuel types
    ArrayList<String> volume_list = new ArrayList<>();
    ArrayList<String> vehicle_count = new ArrayList<>();
    ArrayList<String> fuel_available_ids = new ArrayList<>();

    ArrayList<String> userVehicles = new ArrayList<>();
    ArrayList<String> userVehicleIDs = new ArrayList<>();

    String fuelStationID;
    String selectedFuelID;
    String userID;
    String userQStatus;

    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);

        //get user data
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(USER_ID_KEY, null);
        userQStatus = sharedPreferences.getString(USER_QUEUE_STATUS, null);

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
                        String fuelID = responseObj.getString("id");
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
                        fuel_available_ids.add(fuelID);
                        Log.v("Fuel Data", fuelName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                getVehicleData();
                createListViewAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QueueList.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void getVehicleData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_USER_VEHICLES = BACKEND_URI+"vehicles/users/"+userID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_USER_VEHICLES, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() == 0){
                    String type = "Sorry!.No vehicles Found.";
                    String message = "Please add your vehicle.";
                    dialogBox(type, message);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String vID = responseObj.getString("id");
                        String plateNo = responseObj.getString("plateNumber");
                        String category = responseObj.getString("category");
                        String vData = "Plate: "+ plateNo + " | Category: "+category;
                        userVehicles.add(vData);
                        userVehicleIDs.add(vID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QueueList.this, "Fail to get data", Toast.LENGTH_SHORT).show();
                dialogBox("Error!", "Please try again later!");
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

                if (Objects.equals(userQStatus, "joined")) {
                    //if already in a queue
                    dialogBox("Warning!", "You are already in a queue.");
                }else{
                    selectedFuelID = fuel_available_ids.get(i);
                    joinQueueDialogBox();
                }
            }
        });
    }

    public void joinQueueDialogBox() {

        final ArrayAdapter<String> adp = new ArrayAdapter<String>(QueueList.this,
                android.R.layout.simple_spinner_item, userVehicles);

        Spinner sp_vehicle = new Spinner(QueueList.this);
        sp_vehicle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        sp_vehicle.setAdapter(adp);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(sp_vehicle);

        alertDialogBuilder.setTitle("Join Fuel Queue");
        alertDialogBuilder.setMessage("Please Select Vehicle to join fuel queue.");

        alertDialogBuilder.setPositiveButton("Confirm Join",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //join queue
                        String vehicleData = sp_vehicle.getSelectedItem().toString();
                        String vehicleID = getVehicleID(vehicleData);
                        userJoinQueue(vehicleID);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String getVehicleID(String vData){
        int index = 0;
        String vID = "";
        for (int i = 0; i < userVehicles.size(); i++) {
            if(Objects.equals(userVehicles.get(i), vData)){
                index = i;
            }
        }
        vID = userVehicleIDs.get(index);
        return vID;
    }

    public void userJoinQueue(String vehicleID){
        NukeSSLCerts.nuke();
        String ADD_USER_TO_QUEUE = BACKEND_URI + "fuelqueueuser";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject responseBodyData = new JSONObject();
        try {
            String status = "InQueue";
            responseBodyData.put("userId", userID);
            responseBodyData.put("vehicleId", vehicleID);
            responseBodyData.put("fuelID", selectedFuelID);
            responseBodyData.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ADD_USER_TO_QUEUE, responseBodyData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(QueueList.this, "Joined Successfully!", Toast.LENGTH_SHORT).show();
                try {
                    String createdId = response.getString("id");
                    Log.i("Created USERQueue ID", "Created!");

                    //save user queue status
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_QUEUE_STATUS, "joined");
                    editor.putString(USER_QUEUE_ID, createdId);
                    editor.apply();

                    Intent intent = new Intent(QueueList.this, Queue.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialogBox("Error!", "Please try again later!");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
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