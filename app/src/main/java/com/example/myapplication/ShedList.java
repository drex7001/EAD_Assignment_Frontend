package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ShedList extends AppCompatActivity {
    ListView listView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shed_list);
        String[] sheds = new String[]{"a","b","c","a","b","c","a","b","c","a","b","c","a","b","c","a","b","c","a","b","c","a","b","c",};
        ListAdapter Shed_list = new ArrayAdapter<String>(this,R.layout.shed_item,R.id.shed_name,sheds);
        listView = findViewById(R.id.shed_list);
        listView.setAdapter(Shed_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String gg = String.valueOf(adapterView.getItemAtPosition(i));
//                Toast.makeText(ShedList.this, gg, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShedList.this, QueueList.class);
                startActivity(intent);
            }
        });
    }
}