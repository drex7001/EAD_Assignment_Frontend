package com.example.myapplication;

import static com.example.myapplication.models.Utils.BACKEND_URI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.List;

public class ShedList extends AppCompatActivity {
    ListView listView;
    Toolbar userShedListToolbar;
    EditText searchText;
    ArrayList<String> allSheds = new ArrayList<>();
    private ArrayList<String> fuelStationsIDList = new ArrayList<String>();
    private ArrayAdapter shed_list_adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shed_list);

        //init ids
        searchText = findViewById(R.id.user_search_station);
        shed_list_adapter = new ArrayAdapter<String>(this, R.layout.shed_item, R.id.shed_name, allSheds);

        //init toolbar
        userShedListToolbar = findViewById(R.id.user_dashboard_toolbar);
        setSupportActionBar(userShedListToolbar);
        getSupportActionBar().setTitle("FuelQ - Available Fuel Stations");
        userShedListToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAllStationData();

        //search function
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                shed_list_adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    private void getAllStationData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_URL = BACKEND_URI + "fuelstation";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ALL_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject responseObj = response.getJSONObject(i);
                        String stationName = responseObj.getString("name");
                        String stationId = responseObj.getString("id");
                        allSheds.add(stationName);
                        fuelStationsIDList.add(stationId);
                        buildFuelStationsList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShedList.this, "Fail to get shed data", Toast.LENGTH_SHORT).show();
                dialogBox();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void buildFuelStationsList() {
//        Log.i("Shed data", allSheds.toString());
        listView = findViewById(R.id.shed_list);
        listView.setAdapter(shed_list_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fuelStationID = fuelStationsIDList.get(i);
                Intent intent = new Intent(ShedList.this, QueueList.class);
                intent.putExtra("FUEL_STATION_ID", fuelStationID);
                startActivity(intent);
            }
        });
    }

    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Connection Error!");
        alertDialogBuilder.setMessage("Please try again later.");
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