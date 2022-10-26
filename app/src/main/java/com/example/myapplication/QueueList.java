package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class QueueList extends AppCompatActivity {

    ListView queueList;
    String queue_type[] = {"Petrol 95","Diesel",};
    String volume[] = {"1000","9000",};
    String vehicle_count[] = {"20","5",};
    Button join;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_list);

        queueList = findViewById(R.id.queue_list);
        CustomAdapter adapter = new CustomAdapter(this,queue_type,volume,vehicle_count);
        queueList.setAdapter(adapter);


        queueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent intent = new Intent(QueueList.this, Queue.class);
                startActivity(intent);
            }
        });
    }


}


class CustomAdapter extends ArrayAdapter<String>{

    Context content;
    String[] queue_type;
    String[] volume;
    String[] vehicle_count;

    public CustomAdapter(Context context,String[] queue_type,String[] volume,String[] vehicle_count) {
        super(context, R.layout.activity_queue,R.id.queue_type,queue_type);
        this.content = context;
        this.queue_type = queue_type;
        this.volume = volume;
        this.vehicle_count = vehicle_count;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.queue_item,parent,false);
        TextView queue_typeView = row.findViewById(R.id.queue_type);
        TextView volumeView = row.findViewById(R.id.volume);
        TextView vehicle_countView = row.findViewById(R.id.vehicle_count);

        queue_typeView.setText(queue_type[position]);
        volumeView.setText(volume[position]);
        vehicle_countView.setText(vehicle_count[position]);

        return row;
    }

}