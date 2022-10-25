package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class OwnerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout ownerDrawerLayout;
    NavigationView ownerNavigationView;
    Toolbar ownerToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        // Nav bar
        ownerDrawerLayout = findViewById(R.id.owner_home);
        ownerNavigationView = findViewById(R.id.owner_nav_bar);
        ownerToolbar =  findViewById(R.id.owner_toolbar);
        setSupportActionBar(ownerToolbar);
        getSupportActionBar().setTitle("Fuel Q - Owner Menu");

        ownerNavigationView.bringToFront();
        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(this, ownerDrawerLayout, ownerToolbar, R.string.owner_nav_open, R.string.owner_nav_close);
        ownerDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ownerNavigationView.setNavigationItemSelectedListener(this);

        //List view

    }

    @Override
    public void onBackPressed(){
        if(ownerDrawerLayout.isDrawerOpen(GravityCompat.START)){
            ownerDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
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