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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.models.Fuel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnerAddNewShed extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    String ownerID;

    Toolbar addNewStationToolbar;
    Spinner spinner;
    TextView stationName;
    TextView stationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_add_new_shed);

        //get owner id form login user
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        ownerID = sharedpreferences.getString(USER_ID_KEY, null);

        //set toolbar
        addNewStationToolbar =  findViewById(R.id.owner_toolbar);
        setSupportActionBar(addNewStationToolbar);
        getSupportActionBar().setTitle("Fuel Q - Add Fuel Station");
        addNewStationToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set spinner
        spinner = (Spinner) findViewById(R.id.owner_add_fuel_types_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fuel_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //form
        stationName = (TextView) findViewById(R.id.addFuelStation_StationName);
        stationAddress = (TextView) findViewById(R.id.addFuelStation_StationAddress);

        Button submitBtn = findViewById(R.id.add_fuel_station_button);

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String fType = spinner.getSelectedItem().toString();
                if( stationName.getText().toString().matches("") || stationAddress.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), "Please Fill All Fields!", Toast.LENGTH_SHORT).show();
                }else{
                    //api call
                    String sName = stationName.getText().toString();
                    String sAddress= stationAddress.getText().toString();
                    List<String> fTypes= new ArrayList<>();

                    if(fType.equals("Petrol")){
                        fTypes.add("P");
                    }else if(fType.equals("Diesel")){
                        fTypes.add("D");
                    }else{
                        fTypes.add("D");
                        fTypes.add("P");
                    }

                    Log.i("FType", fTypes.get(0));
                    postData(sName, sAddress, fTypes);
                }
            }
        });

    }

    private void postData(String name, String address, List<String> types) {
        NukeSSLCerts.nuke();
        String ADD_NEW_FUEL_STATION = BACKEND_URI+"fuelstation";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject responseBodyData = new JSONObject();
        try {
            responseBodyData.put("name", name);
            responseBodyData.put("address", address);
            responseBodyData.put("ownerID", ownerID);
            responseBodyData.put("fuelTypes", new JSONArray(types));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, ADD_NEW_FUEL_STATION, responseBodyData,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                stationName.setText("");
                stationAddress.setText("");
                Toast.makeText(OwnerAddNewShed.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                try {
                    String createdId = response.getString("id");
                    Log.i("Created Station ID", "Created!");
                    dialogBox("Success!", "Fuel Station Created Successfully!");
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

    public void dialogBox(String type, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(type);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        Intent intent = new Intent(OwnerAddNewShed.this, OwnerHomeActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}