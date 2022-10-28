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
import java.util.Objects;

public class OwnerAddFuel extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    Spinner spinnerFuel, spinnerShed;
    String ownerID;
    ArrayList<String> fuelStationNames = new ArrayList<>();
    ArrayList<String> fuelStationIds = new ArrayList<>();
    ArrayList<ArrayList<String>> stationFuelTypes = new ArrayList<>();
    String selectedStationName = "";
    String fuelStationID = "";

    Toolbar addFuelToolbar;
    Button submitBtn, continueBtn;
    TextView fuelName, fuelAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_add_fuel);

        //get owner id form login user
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        ownerID = sharedpreferences.getString(USER_ID_KEY, null);

        //set toolbar
        addFuelToolbar = findViewById(R.id.owner_add_fuel_toolbar);
        setSupportActionBar(addFuelToolbar);
        getSupportActionBar().setTitle("Fuel Q - Add Fuel Data");
        addFuelToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set spinner
        spinnerShed = (Spinner) findViewById(R.id.addFuelData_FuelStation);
        spinnerFuel = (Spinner) findViewById(R.id.addFuelData_FuelType);
        spinnerShed.setEnabled(false);
        spinnerFuel.setEnabled(false);

        //form
        fuelName = (TextView) findViewById(R.id.addFuelData_FuelName);
        fuelAmount = (TextView) findViewById(R.id.addFuelData_FuelAmount);
        submitBtn = findViewById(R.id.add_fuel_submit_button);
        continueBtn = findViewById(R.id.add_fuel_continue_btn);

        //get station data
        getStationData();
    }

    private void getStationData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_URL = BACKEND_URI + "fuelstation/users/" + ownerID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() == 0) {
//                    notFound.setVisibility(View.VISIBLE);
                    dialogBox("Error!", "You don't have any fuel stations to add fuel. Please create fuel station to continue.");
                }

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String stationId = responseObj.getString("id");
                        String stationName = responseObj.getString("name");
                        ArrayList<String> fuelTypes = new ArrayList<>();
                        JSONArray fuelTypesArrJson = responseObj.getJSONArray("fuelTypes");
                        for (int j = 0; j < fuelTypesArrJson.length(); j++) {
                            fuelTypes.add(fuelTypesArrJson.getString(j));
                        }
                        stationFuelTypes.add(fuelTypes);
                        fuelStationIds.add(stationId);
                        fuelStationNames.add(stationName);
//                        Log.v("Fuel Station Data", fuelTypes.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //continue building form
                continueFormSetup();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerAddFuel.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void continueFormSetup() {
//        Log.i("Station_Names", fuelStationNames.toString());
        ArrayAdapter<String> shedNamesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, fuelStationNames);
        spinnerShed.setAdapter(shedNamesAdapter);
        spinnerShed.setEnabled(true);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedStationName = spinnerShed.getSelectedItem().toString();
                if (selectedStationName.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please Select Station!", Toast.LENGTH_SHORT).show();
                } else {
                    //continue
                    continueBtn.setVisibility(View.INVISIBLE);
                    spinnerShed.setEnabled(false);
                    fuelName.setVisibility(View.VISIBLE);
                    fuelAmount.setVisibility(View.VISIBLE);
                    submitBtn.setVisibility(View.VISIBLE);

                    //set spinner
                    setupFuelSpinner(selectedStationName);
                }
            }
        });

        //form submit final step
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fType = spinnerFuel.getSelectedItem().toString();
                if (fuelName.getText().toString().matches("") || fuelAmount.getText().toString().matches("") || fType.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please Fill All Fields!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    //api call
                    double amount = Double.parseDouble(fuelAmount.getText().toString());
                    String name = fuelName.getText().toString();
                    postData(name, fType, amount);
                }
            }
        });

    }

    private void postData(String name, String type, double amount) {
        NukeSSLCerts.nuke();
        String ADD_NEW_FUEL = BACKEND_URI+"fuel";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject responseBodyData = new JSONObject();
        try {
            responseBodyData.put("name", name);
            responseBodyData.put("type", type);
            responseBodyData.put("amount", amount);
            responseBodyData.put("fuelStationID", fuelStationID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ADD_NEW_FUEL, responseBodyData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(OwnerAddFuel.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                try {
                    String createdId = response.getString("id");
                    Log.i("Created Fuel ID", "Created!");
                    dialogBox("Success!", "Fuel Data Saved Successfully!");
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
    }

    public void setupFuelSpinner(String station) {
        ArrayList<String> availableFuelTypes = new ArrayList<>();
        ArrayList<String> aFuelTypes = new ArrayList<>();
        availableFuelTypes = getFuelTypesByStationName(station);

        if(availableFuelTypes.size() == 2){
            aFuelTypes.add("Petrol");
            aFuelTypes.add("Diesel");
        }else{
            if(availableFuelTypes.size() == 1){
                String type = availableFuelTypes.get(0);
                if(Objects.equals(type, "P")){
                    aFuelTypes.add("Petrol");
                }else{
                    aFuelTypes.add("Diesel");
                }
            }
        }
        ArrayAdapter<String> availableFuelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, aFuelTypes);
        spinnerFuel.setAdapter(availableFuelAdapter);
        spinnerFuel.setEnabled(true);
        spinnerFuel.setVisibility(View.VISIBLE);
//        Log.i("AVAILABLE_FUEL_TYPES", aFuelTypes.toString());
    }

    public ArrayList<String> getFuelTypesByStationName(String stationName) {
        ArrayList<String> fuelTypes = new ArrayList<>();
        int stationIndex = 0;
        for (int i = 0; i < fuelStationNames.size(); i++) {
            if (Objects.equals(fuelStationNames.get(i), stationName)) {
                stationIndex = i;
            }
        }
        //get id
        fuelStationID = fuelStationIds.get(stationIndex);
        fuelTypes = stationFuelTypes.get(stationIndex);
        return fuelTypes;
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
                        Intent intent = new Intent(OwnerAddFuel.this, OwnerHomeActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}