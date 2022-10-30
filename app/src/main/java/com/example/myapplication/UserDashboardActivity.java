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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class UserDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedpreferences;

    DrawerLayout userDrawerLayout;
    NavigationView userNavigationView;

    String UserInQID, userID;
    boolean userIsINQ = false;

    TextView userNameView, userQJoinBtnView;
    LinearLayout linearLayoutJoinQ;
    LinearLayout linerLayoutViewVehicles;
    LinearLayout allViewLayout;
    Toolbar toolbar;

    ProgressBar progressBar;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        //id init
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userNameView = (TextView) findViewById(R.id.textView1);
        userQJoinBtnView = (TextView) findViewById(R.id.qJoinView);
        allViewLayout = findViewById(R.id.activity_dashboard);
        progressBar = findViewById(R.id.progress_loading);

        //get user data
        allViewLayout.setVisibility(View.INVISIBLE);
        userID = sharedpreferences.getString(USER_ID_KEY, null);
        userInQData();

    }

    //get user in q data
    private void userInQData() {
        NukeSSLCerts.nuke(); //trust certificates
        String GET_ALL_URL = BACKEND_URI + "fuelqueueuser/user/q_status/" + userID;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_ALL_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                try {
                    boolean userQStatus = responseObj.getBoolean("inQ");

                    if (userQStatus) {
                        userIsINQ = userQStatus;
                        String userInQueueID = responseObj.getString("id");
                        UserInQID = userInQueueID;

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(USER_QUEUE_ID, UserInQID);
                        editor.putString(USER_QUEUE_STATUS, "joined");
                        editor.apply();
                    }

                    continueBuildUserLayout();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserDashboardActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(UserDashboardActivity.this, "Fail to get user Q data", Toast.LENGTH_SHORT).show();
                dialogBox("Error!", "No Connection! Pleas try again later.");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void continueBuildUserLayout() {
        //nav and toolbar
        userDrawerLayout = findViewById(R.id.user_home);
        userNavigationView = findViewById(R.id.user_nav_bar);
        toolbar = findViewById(R.id.user_dashboard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FuelQ - User Dashboard");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //setup drawer
        userNavigationView.setVisibility(View.VISIBLE);
        userNavigationView.bringToFront();
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(this, userDrawerLayout, toolbar, R.string.owner_nav_open, R.string.owner_nav_close);
        userDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        userNavigationView.setNavigationItemSelectedListener(this);

        //Setup dashboard body
        linearLayoutJoinQ = findViewById(R.id.join_queue);
        linerLayoutViewVehicles = findViewById(R.id.user_vehicles);

        String userName = sharedpreferences.getString(USER_NAME_KEY, null);
        userNameView.setText("Hi, " + userName);

        //check user in a queue
        String userStatusQ = sharedpreferences.getString(USER_QUEUE_STATUS, null);
        Intent intent;
        if (Objects.equals(userStatusQ, "joined")) {
            userQJoinBtnView.setText("View Queue");
            intent = new Intent(UserDashboardActivity.this, Queue.class);
        } else {
            userQJoinBtnView.setText("Join Queue");
            intent = new Intent(UserDashboardActivity.this, ShedList.class);
        }

        //set visibility
        allViewLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        linearLayoutJoinQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });
        linerLayoutViewVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, UserViewVehiclesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.user_dashboard_item:
//                finish();
//                startActivity(getIntent());
                break;
            case R.id.user_vehicle_add_item:
                Intent intent = new Intent(UserDashboardActivity.this, UserAddVehicleActivity.class);
                startActivity(intent);
                break;
            case R.id.user_all_vehicles_item:
                Intent intent2 = new Intent(UserDashboardActivity.this, UserViewVehiclesActivity.class);
                startActivity(intent2);
                break;
            case R.id.user_previous_fuel_list:
                Intent intent3 = new Intent(UserDashboardActivity.this, UserFuelHistoryActivity.class);
                startActivity(intent3);
                break;
            case R.id.user_logout_item:
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear().commit();

                Toast.makeText(UserDashboardActivity.this, "Log Out Successful", Toast.LENGTH_LONG).show();

                Intent intentLogout = new Intent(UserDashboardActivity.this, UserLoginActivity.class);
                startActivity(intentLogout);
                finish();
                break;
        }

        userDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
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