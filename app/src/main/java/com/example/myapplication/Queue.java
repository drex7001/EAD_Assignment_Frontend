package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;
import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_QUEUE_ID;
import static com.example.myapplication.models.Utils.USER_QUEUE_STATUS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import java.util.List;
import java.util.Map;

public class Queue extends AppCompatActivity {

    Button leave_btn, complete_btn;
    TextView shedNameView, shedAddressView, fuelNameView, fuelAmountView, vehicleDataView, joinedTime;

    SharedPreferences sharedPreferences;
    String userQID, shedName, shedAddress, fuelName, vehicleData, arrivalTime;
    double fuelAmount;
    double userCompletedFuelAmount;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        //init ids
        leave_btn = findViewById(R.id.user_join_leave_queue_btn);
        complete_btn = findViewById(R.id.user_join_complete_filling_btn);
        shedNameView = findViewById(R.id.user_joined_shed_title);
        shedAddressView = findViewById(R.id.user_joined_shed_address);
        fuelNameView = findViewById(R.id.user_queue_fuelType);
        fuelAmountView = findViewById(R.id.user_queue_fuelAmount);
        vehicleDataView = findViewById(R.id.user_queue_vehicles);
        joinedTime = findViewById(R.id.viewStation_station_joinedTime);
        relativeLayout = findViewById(R.id.user_q_data_layout);

        //shared pref init
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userQID = sharedPreferences.getString(USER_QUEUE_ID, null);

        //toolbar
        relativeLayout.setVisibility(View.INVISIBLE);
        Toolbar toolbar;
        toolbar = findViewById(R.id.user_shed_join_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - Joined Queue Data");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Queue.this, UserDashboardActivity.class);
                startActivity(intent);
            }
        });

        leave_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                leaveQueueStatus();
            }
        });

        complete_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.complete_q_dialog_layout, (ViewGroup) findViewById(R.id.layout_root));
                final EditText fuelAmount = (EditText) layout.findViewById(R.id.user_completeQ_amount);

                AlertDialog.Builder builder = new AlertDialog.Builder(Queue.this);
                builder.setView(layout);
                builder.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amountSt = fuelAmount.getText().toString();
                        if(amountSt.equals("")){
                            Toast.makeText(Queue.this, "Error! Please Add Amount", Toast.LENGTH_LONG).show();
                        }else{
                            //save info where you want it
                            userCompletedFuelAmount = Double.parseDouble(amountSt);
                            completeQueueStatus();
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //get joined queue data
        getFuelQData();

    }

    private void getFuelQData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_FUELQ_DATA = BACKEND_URI + "fuelqueueuser/" + userQID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_FUELQ_DATA, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObjQData = response.getJSONObject("queueData");
                            JSONObject responseObjStationData = response.getJSONObject("shedData");
                            JSONObject responseObjFuelData = response.getJSONObject("fuelData");
                            JSONObject responseObjVehicleData = response.getJSONObject("vehicleData");

                            shedName = responseObjStationData.getString("name");
                            shedAddress = responseObjStationData.getString("address");

                            fuelName = responseObjFuelData.getString("name");
                            fuelAmount = responseObjFuelData.getDouble("amount");

                            arrivalTime = getDateTime(responseObjQData.getString("arrivalTime"));

                            String vPlate = responseObjVehicleData.getString("plateNumber");
                            String vCat = responseObjVehicleData.getString("category");
                            vehicleData = "Plate No: "+vPlate+"\nCategory: "+vCat;

                            continueBuildProcess();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Queue.this, "Error!", Toast.LENGTH_SHORT).show();
                            dialogBox("Error!", "Please try again later.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Queue.this, "Fail to get data", Toast.LENGTH_SHORT).show();
                        dialogBox("Error!", "Please try again later.");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    public void continueBuildProcess() {
        Log.i("vehicleData", shedName);
        shedNameView.setText("Station Name : "+shedName);
        shedAddressView.setText("Station Address : "+shedAddress);
        fuelNameView.setText("Fuel Name : "+fuelName);
        fuelAmountView.setText("Fuel Amount : "+String.valueOf(fuelAmount) + " L");
        joinedTime.setText("Fuel Arrival Time : "+arrivalTime);
        vehicleDataView.setText("Vehicle Data\n"+vehicleData);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public String getDateTime(String datetimeUTC) {
        String[] parts = datetimeUTC.split("T");
        String timeRem = parts[1].toString();
        String[] parts2 = timeRem.split("\\.");
        String time = parts2[0];
        String datetime = parts[0] + ":" + time;
        return datetime;
    }

    private void completeQueueStatus() {
        NukeSSLCerts.nuke();
        String COMPLETE_QUEUE = BACKEND_URI + "fuelqueueuser/complete/"+userQID+"/"+userCompletedFuelAmount;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest requestComplete = new JsonObjectRequest(Request.Method.PUT, COMPLETE_QUEUE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(Queue.this, "Completed Successfully!", Toast.LENGTH_SHORT).show();
                try {
                    boolean resUpdate = response.getBoolean("success");
                    Log.i("Completed Fuel Q", "Completed!");
                    afterQActionDialogBox("Success!", "Fuel Queue Completed Successfully! Thank you for the service.");
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
        });
        requestQueue.add(requestComplete);
    }

    private void leaveQueueStatus() {
        NukeSSLCerts.nuke();
        String LEAVE_QUEUE = BACKEND_URI + "fuelqueueuser/exit/"+userQID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, LEAVE_QUEUE, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(Queue.this, "Successfully Left The Queue!", Toast.LENGTH_SHORT).show();
                try {
                    boolean resultLeave = response.getBoolean("success");
                    Log.i("Left Q", "Success!");
                    afterQActionDialogBox("Success!", "You left the Queue!");
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
        });
        requestQueue.add(request);
    }

    public void afterQActionDialogBox(String type, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(type);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(USER_QUEUE_ID);
                        editor.remove(USER_QUEUE_STATUS);
                        editor.apply();

                        finish();
                        Intent intent = new Intent(Queue.this, UserDashboardActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                        Intent intent = new Intent(Queue.this, UserDashboardActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}