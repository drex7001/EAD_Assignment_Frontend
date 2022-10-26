package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.adapters.OwnerHomeStationListAdapter;
import com.example.myapplication.models.FuelStation;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OwnerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout ownerDrawerLayout;
    NavigationView ownerNavigationView;
    Toolbar ownerToolbar;
    CircularProgressIndicator circularProgressIndicator;
    ArrayList<FuelStation> fuelStationArrayList;
    OwnerHomeStationListAdapter adapter;
    RecyclerView stationRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        // Nav bar
        ownerDrawerLayout = findViewById(R.id.owner_home);
        ownerNavigationView = findViewById(R.id.owner_nav_bar);
        ownerToolbar = findViewById(R.id.owner_toolbar);
        circularProgressIndicator = findViewById(R.id.owner_home_progressIndicator);
        stationRV = findViewById(R.id.owner_home_shedList_rview);
        setSupportActionBar(ownerToolbar);
        getSupportActionBar().setTitle("Fuel Q - Owner Menu");

        ownerNavigationView.bringToFront();
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(this, ownerDrawerLayout, ownerToolbar, R.string.owner_nav_open, R.string.owner_nav_close);
        ownerDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ownerNavigationView.setNavigationItemSelectedListener(this);

        //Handle REST API
        fuelStationArrayList = new ArrayList<>();
        getStationData();

    }

    private void getStationData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_URL = BACKEND_URI+"fuelstation/users/63575ba58e1aee177127c7a1";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                circularProgressIndicator.hide();
                stationRV.setVisibility(View.VISIBLE);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String stationId = responseObj.getString("id");
                        String stationName = responseObj.getString("name");
                        String stationAddress = responseObj.getString("address");
                        List<String> fuelTypes = new ArrayList<>();
                        JSONArray fuelTypesArrJson = responseObj.getJSONArray("fuelTypes");
                        for (int j = 0; j < fuelTypesArrJson.length(); j++) {
                            fuelTypes.add(fuelTypesArrJson.getString(j));
                        }
                        FuelStation fuelStation = new FuelStation(stationId, stationName, stationAddress, fuelTypes);
                        fuelStationArrayList.add(fuelStation);
                        buildRecyclerView();
//                        Log.v("Fuel Station Data", fuelTypes.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        circularProgressIndicator.show();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OwnerHomeActivity.this, "Fail to get data", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void buildRecyclerView() {
        adapter = new OwnerHomeStationListAdapter(fuelStationArrayList, OwnerHomeActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        stationRV.setHasFixedSize(true);
        stationRV.setLayoutManager(manager);
        stationRV.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (ownerDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            ownerDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.owner_nav_home:
                break;
            case R.id.owner_nav_add_shed:
                Intent intent = new Intent(OwnerHomeActivity.this, OwnerAddNewShed.class);
                startActivity(intent);
                break;
            case R.id.owner_nav_add_fuel:
                Intent intent2 = new Intent(OwnerHomeActivity.this, OwnerAddFuel.class);
                startActivity(intent2);
//                finish();
                break;
            case R.id.owner_nav_logout:
                break;
        }

        ownerDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}