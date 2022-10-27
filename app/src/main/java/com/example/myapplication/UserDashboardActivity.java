package com.example.myapplication;

import static com.example.myapplication.models.Utils.SHARED_PREFS;
import static com.example.myapplication.models.Utils.USER_NAME_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;


public class UserDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedpreferences;

    DrawerLayout userDrawerLayout;
    NavigationView userNavigationView;

    TextView userNameView;
    LinearLayout linearLayoutJoinQ;
    Toolbar toolbar;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        //id init
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userNameView = (TextView)findViewById(R.id.textView1);

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
        String userName = sharedpreferences.getString(USER_NAME_KEY, null);
        userNameView.setText("Hi, "+ userName);

        linearLayoutJoinQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, ShedList.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.user_dashboard_item:
                finish();
                startActivity(getIntent());
                break;
            case R.id.user_vehicle_add_item:
                Intent intent = new Intent(UserDashboardActivity.this, UserAddVehicleActivity.class);
                startActivity(intent);
                break;
            case R.id.user_all_vehicles_item:
//                Intent intent2 = new Intent(UserDashboardActivity.this, UserAddVehicleActivity.class);
//                startActivity(intent2);
                break;
            case R.id.user_previous_fuel_list:
//                Intent intent3 = new Intent(UserDashboardActivity.this, UserAddVehicleActivity.class);
//                startActivity(intent3);
                break;
            case R.id.user_logout_item:
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear().commit();

                Toast.makeText(UserDashboardActivity.this,"Log Out Successful", Toast.LENGTH_LONG).show();

                Intent intentLogout = new Intent(UserDashboardActivity.this, UserLoginActivity.class);
                startActivity(intentLogout);
                finish();
                break;
        }

        userDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}