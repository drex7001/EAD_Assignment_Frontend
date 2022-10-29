package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;
import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_ID_KEY;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserFuelHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;

    SharedPreferences sharedPreferences;

    String userID;
    TextView dataNotFound;

    ArrayList<String> fuelQDataDetails = new ArrayList<>();
    ListView listView;
    private ArrayAdapter fuel_qlist_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fuel_history);

        //shared pref init.
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(USER_ID_KEY, null);

        //ids
        listView = findViewById(R.id.user_qhistory_list);
        dataNotFound = findViewById(R.id.user_history_notFound);

        //toolbar
        toolbar = findViewById(R.id.user_fuel_history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - User Fuel History Data");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFuelQData();
    }

    private void getFuelQData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_FUEL_QUSER_DATA = BACKEND_URI+"fuelqueueuser/user/"+userID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_FUEL_QUSER_DATA, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() == 0){
                    dataNotFound.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject resObj = response.getJSONObject(i);

                        JSONObject responseObjQueueUser = resObj.getJSONObject("fuelQUser");
                        JSONObject responseObjFuel = resObj.getJSONObject("fuel");
                        JSONObject responseObjVehicle = resObj.getJSONObject("vehicle");

                        String fuelName = responseObjFuel.getString("name");
                        String fuelType = responseObjFuel.getString("type");

                        String vehiclePlate = responseObjVehicle.getString("plateNumber");
                        String vehicleCategory = responseObjVehicle.getString("category");

                        String arrivalTime = getDateTime(responseObjQueueUser.getString("arrivalTime"));
                        String finishedTime = getDateTime(responseObjQueueUser.getString("departTime"));
                        String fuelAmount = responseObjQueueUser.getString("amount");
                        String fuelStatus = responseObjQueueUser.getString("status");

                        String fuelHistoryData = "Vehicle: "+vehiclePlate+"\nVehicle Type: "+vehicleCategory+"\nFuel Name: "+fuelName+"\nFuel Type: "+fuelType+"\nFuelQ Arrival Time: "+arrivalTime+"\nFuelQ Completed/Left Time: "+finishedTime+"\nFilled Fuel Amount: "+fuelAmount+" L\nFuelQ Status: "+fuelStatus;

                        fuelQDataDetails.add(fuelHistoryData);
                        Log.v("Fuel Data", "fuelName");
                        buildFuelDataList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialogBox("Error!", "Please try again later.");
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserFuelHistoryActivity.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void buildFuelDataList() {
        fuel_qlist_adapter = new ArrayAdapter<String>(this, R.layout.fuel_history_item, R.id.fuelData_name, fuelQDataDetails);
        listView.setAdapter(fuel_qlist_adapter);
    }

    public String getDateTime(String datetimeUTC){
        String[] parts = datetimeUTC.split("T");
        String timeRem = parts[1].toString();
        String[] parts2 = timeRem.split("\\.");
        String time = parts2[0];
        String datetime = parts[0] +":"+ time;
        return datetime;
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
                        Intent intent = new Intent(UserFuelHistoryActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}