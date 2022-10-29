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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAddVehicleActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView plateNoView, fuelAmountView;
    Spinner vehicle_fuelTypes, vehicle_categories;
    Button saveBtn;
    String userID;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_vehicle);

        //ids init
        plateNoView = findViewById(R.id.addUser_plateNumber);
        fuelAmountView = findViewById(R.id.addUser_fuelCapacity);
        vehicle_fuelTypes = (Spinner) findViewById(R.id.addUser_fuelType);
        vehicle_categories = (Spinner) findViewById(R.id.addUserVehicle_category);
        toolbar = findViewById(R.id.user_add_vehicle_toolbar);
        saveBtn = findViewById(R.id.user_add_vehicle_button);

        //get user id
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(USER_ID_KEY, null);

        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - Add My Vehicle");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<String> duelTypes = new ArrayList<>();
        duelTypes.add("Petrol");
        duelTypes.add("Diesel");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, duelTypes);
        vehicle_fuelTypes.setAdapter(adapter1);
        vehicle_fuelTypes.setEnabled(true);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.vehicleTypes, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicle_categories.setAdapter(adapter2);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fType = vehicle_fuelTypes.getSelectedItem().toString();
                String vType = vehicle_categories.getSelectedItem().toString();
                if (plateNoView.getText().toString().matches("") || fuelAmountView.getText().toString().matches("") || fType.matches("") || vType.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please Fill All Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    //api call
                    String vPlate = plateNoView.getText().toString();
                    String VFCapacity = fuelAmountView.getText().toString();
                    postData(vPlate, vType, fType, Double.parseDouble(VFCapacity));
                }
            }
        });
    }

    private void postData(String plateNumber, String category, String fuelType, double capacity) {
        NukeSSLCerts.nuke();
        String ADD_VEHICLE = BACKEND_URI + "vehicles";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject responseBodyData = new JSONObject();
        try {
            responseBodyData.put("plateNumber", plateNumber);
            responseBodyData.put("category", category);
            responseBodyData.put("fuelType", fuelType);
            responseBodyData.put("fuelCapacity", capacity);
            responseBodyData.put("userID", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ADD_VEHICLE, responseBodyData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(UserAddVehicleActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                try {
                    String createdId = response.getString("id");
                    Log.i("Created Station ID", "Created!");
                    dialogBox("Success!", "Vehicle Added Successfully!");
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
                        Intent intent = new Intent(UserAddVehicleActivity.this, UserDashboardActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}