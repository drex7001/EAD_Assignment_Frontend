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

public class UserViewVehiclesActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    String userID;
    TextView dataNotFound;

    Toolbar userVehiclesToolbar;

    ArrayList<String> userVehicleDataList = new ArrayList<>();
    ArrayAdapter<String> userVehicleAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_vehicles);

        //get user id
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(USER_ID_KEY, null);

        //ids init
        userVehiclesToolbar = findViewById(R.id.user_vehicles_toolbar);
        dataNotFound = findViewById(R.id.user_vehicles_notFound);
        listView = findViewById(R.id.user_vehicles_list);

        //toolbar
        setSupportActionBar(userVehiclesToolbar);
        getSupportActionBar().setTitle("FuelQ - My Vehicles");
        userVehiclesToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getVehicleData();
    }

    private void getVehicleData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_USER_VEHICLES = BACKEND_URI+"vehicles/users/"+userID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_USER_VEHICLES, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() == 0){
                    dataNotFound.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String plateNo = responseObj.getString("plateNumber");
                        String category = responseObj.getString("category");
                        String fuelType = responseObj.getString("fuelType");
                        String fuelCapacity = responseObj.getString("fuelCapacity");

                        String userVehicleData = "Plate Number: "+plateNo+"\n"+ "Category: "+category+ "\n"+ "Fuel Type: "+fuelType+"\n"+"Fuel Capacity: "+fuelCapacity + " L";
                        userVehicleDataList.add(userVehicleData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //build list view
                buildListView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserViewVehiclesActivity.this, "Fail to get data", Toast.LENGTH_SHORT).show();
                dialogBox("Error!", "Please try again later!");
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void buildListView() {
        userVehicleAdapter = new ArrayAdapter<String>(this, R.layout.user_vehicle_item, R.id.vehicle_data, userVehicleDataList);
        listView.setAdapter(userVehicleAdapter);
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
                        Intent intent = new Intent(UserViewVehiclesActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}